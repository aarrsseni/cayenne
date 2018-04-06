/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler.dialog.db.merge;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.event.DataMapEvent;
import org.apache.cayenne.dbsync.merge.DataMapMerger;
import org.apache.cayenne.dbsync.merge.context.MergeDirection;
import org.apache.cayenne.dbsync.merge.context.MergerContext;
import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactory;
import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactoryProvider;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.dbsync.merge.token.db.AbstractToDbToken;
import org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator;
import org.apache.cayenne.dbsync.naming.NoStemStemmer;
import org.apache.cayenne.dbsync.reverse.dbimport.DefaultDbImportAction;
import org.apache.cayenne.dbsync.reverse.dbload.*;
import org.apache.cayenne.dbsync.reverse.filters.FiltersConfig;
import org.apache.cayenne.dbsync.reverse.filters.PatternFilter;
import org.apache.cayenne.dbsync.reverse.filters.TableFilter;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.ValidationResultBrowser;
import org.apache.cayenne.modeler.event.ProjectDirtyEvent;
import org.apache.cayenne.modeler.services.DbService;
import org.apache.cayenne.modeler.util.CayenneController;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.swing.BindingBuilder;
import org.apache.cayenne.swing.ObjectBinding;
import org.apache.cayenne.validation.ValidationResult;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MergerOptions extends CayenneController {

    protected MergerOptionsView view;
    protected ObjectBinding sqlBinding;

    protected DataMap dataMap;
    protected String textForSQL;

    protected MergerTokenSelectorController tokens;
    protected String defaultCatalog;
    protected String defaultSchema;
    private MergerTokenFactoryProvider mergerTokenFactoryProvider;

    protected ProjectController projectController;

    protected DbService dbService;

    public MergerOptions(ProjectController projectController,
                         String title,
                         DataMap dataMap,
                         String defaultCatalog,
                         String defaultSchema,
                         MergerTokenFactoryProvider mergerTokenFactoryProvider) {
        super();

        this.projectController = projectController;

        this.dbService = getProjectController().getBootiqueInjector().getInstance(DbService.class);

        this.mergerTokenFactoryProvider = mergerTokenFactoryProvider;
        this.dataMap = dataMap;
        this.tokens = new MergerTokenSelectorController(projectController);
        this.view = new MergerOptionsView(tokens.getView());
        this.defaultCatalog = defaultCatalog;
        this.defaultSchema = defaultSchema;
        this.view.setTitle(title);
        initController();

        prepareMigrator();
        createSQL();
        refreshView();
    }

    public Component getView() {
        return view;
    }

    public String getTextForSQL() {
        return textForSQL;
    }

    protected void initController() {

        BindingBuilder builder = new BindingBuilder(
                getApplication().getBindingFactory(),
                this);

        sqlBinding = builder.bindToTextArea(view.getSql(), "textForSQL");

        builder.bindToAction(view.getGenerateButton(), "generateSchemaAction()");
        builder.bindToAction(view.getSaveSqlButton(), "storeSQLAction()");
        builder.bindToAction(view.getCancelButton(), "closeAction()");

        // refresh SQL if different tables were selected
        view.getTabs().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (view.getTabs().getSelectedIndex() == 1) {
                    // this assumes that some tables where checked/unchecked... not very
                    // efficient
                    refreshGeneratorAction();
                }
            }
        });
    }

    /**
     * check database and create the {@link List} of {@link MergerToken}s
     */
    protected void prepareMigrator() {
        try {
            dbService.createDbAdapter(dbService.getDbConnectionInfo());

            MergerTokenFactory mergerTokenFactory = mergerTokenFactoryProvider.get(dbService.getDbAdapter());
            tokens.setMergerTokenFactory(mergerTokenFactory);


            FiltersConfig filters = FiltersConfig.create(defaultCatalog, defaultSchema, TableFilter.everything(),
                    PatternFilter.INCLUDE_NOTHING);

            DataMapMerger merger = DataMapMerger.builder(mergerTokenFactory)
                    .filters(filters)
                    .build();

            DbLoaderConfiguration config = new DbLoaderConfiguration();
            config.setFiltersConfig(filters);

            DataSource dataSource = dbService.createDataSource(dbService.getDbConnectionInfo());

            DataMap dbImport;
            try (Connection conn = dataSource.getConnection();) {
                dbImport = new DbLoader(dbService.getDbAdapter(), conn,
                        config,
                        new LoggingDbLoaderDelegate(LoggerFactory.getLogger(DbLoader.class)),
                        new DefaultObjectNameGenerator(NoStemStemmer.getInstance()))
                        .load();
            } catch (SQLException e) {
                throw new CayenneRuntimeException("Can't doLoad dataMap from db.", e);
            }

            tokens.setTokens(merger.createMergeTokens(dataMap, dbImport));
        } catch (Exception ex) {
            reportError("Error loading adapter", ex);
        }
    }

    /**
     * Returns SQL statements generated for selected schema generation options.
     */
    protected void createSQL() {
        // convert them to string representation for display
        StringBuilder buf = new StringBuilder();

        Iterator<MergerToken> it = tokens.getSelectedTokens().iterator();
        String batchTerminator = dbService.getDbAdapter().getBatchTerminator();

        String lineEnd = batchTerminator != null ? "\n" + batchTerminator + "\n\n" : "\n\n";
        while (it.hasNext()) {
            MergerToken token = it.next();

            if (token instanceof AbstractToDbToken) {
                AbstractToDbToken tdb = (AbstractToDbToken) token;
                for (String sql : tdb.createSql(dbService.getDbAdapter())) {
                    buf.append(sql);
                    buf.append(lineEnd);
                }
            }
        }

        textForSQL = buf.toString();
    }

    protected void refreshView() {
        sqlBinding.updateView();
    }

    // ===============
    // Actions
    // ===============

    /**
     * Starts options dialog.
     */
    public void startupAction() {
        view.pack();
        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        view.setModal(true);
        makeCloseableOnEscape();
        centerView();
        view.setVisible(true);
    }

    public void refreshGeneratorAction() {
        refreshSQLAction();
    }

    /**
     * Updates a text area showing generated SQL.
     */
    public void refreshSQLAction() {
        createSQL();
        sqlBinding.updateView();
    }

    /**
     * Performs configured schema operations via DbGenerator.
     */
    public void generateSchemaAction() {
        refreshGeneratorAction();

        // sanity check...
        List<MergerToken> tokensToMigrate = tokens.getSelectedTokens();
        if (tokensToMigrate.isEmpty()) {
            JOptionPane.showMessageDialog(getView(), "Nothing to migrate.");
            return;
        }

        DataSource dataSource;
        try {
            dataSource = dbService.createDataSource(dbService.getDbConnectionInfo());
        } catch (SQLException ex) {
            reportError("Migration Error", ex);
            return;
        }

        final Collection<ObjEntity> loadedObjEntities = new LinkedList<>();

        MergerContext mergerContext = MergerContext.builder(dataMap)
                .syntheticDataNode(dataSource, dbService.getDbAdapter())
                .delegate(createDelegate(loadedObjEntities))
                .build();

        boolean modelChanged = applyTokens(tokensToMigrate, mergerContext);

        DefaultDbImportAction.flattenManyToManyRelationships(
                dataMap,
                loadedObjEntities,
                mergerContext.getNameGenerator());

        notifyProjectModified(modelChanged);

        reportFailures(mergerContext);

        if(tokens.isReverse()) {
            getApplication().getUndoManager().discardAllEdits();
        }
    }

    private ModelMergeDelegate createDelegate(final Collection<ObjEntity> loadedObjEntities) {
        return new ProxyModelMergeDelegate(new DefaultModelMergeDelegate()) {
            @Override
            public void objEntityAdded(ObjEntity ent) {
                loadedObjEntities.add(ent);
                super.objEntityAdded(ent);
            }
        };
    }

    private boolean applyTokens(List<MergerToken> tokensToMigrate, MergerContext mergerContext) {
        boolean modelChanged = false;

        try {
            for (MergerToken tok : tokensToMigrate) {
                int numOfFailuresBefore = getFailuresCount(mergerContext);

                tok.execute(mergerContext);

                if (!modelChanged && tok.getDirection().equals(MergeDirection.TO_MODEL)) {
                    modelChanged = true;
                }
                if (numOfFailuresBefore == getFailuresCount(mergerContext)) {
                    // looks like the token executed without failures
                    tokens.removeToken(tok);
                }
            }
        } catch (Throwable th) {
            reportError("Migration Error", th);
        }

        return modelChanged;
    }

    private int getFailuresCount(MergerContext mergerContext) {
        return mergerContext.getValidationResult().getFailures().size();
    }

    private void reportFailures(MergerContext mergerContext) {
        ValidationResult failures = mergerContext.getValidationResult();
        if (failures == null || !failures.hasFailures()) {
            JOptionPane.showMessageDialog(getView(), "Migration Complete.");
        } else {
            new ValidationResultBrowser(this).startupAction(
                    "Migration Complete",
                    "Migration finished. The following problem(s) were ignored.",
                    failures);
        }
    }

    private void notifyProjectModified(boolean modelChanged) {
        if(!modelChanged) {
            return;
        }

        // mark the model as unsaved
        Project project = getApplication().getProject();
        project.setModified(true);

        ProjectController projectController = getProjectController();
        projectController.fireEvent(new ProjectDirtyEvent(this,true));

        projectController.fireEvent(new DataMapEvent(Application.getFrame(),
                dataMap, MapEvent.REMOVE));
        projectController.fireEvent(new DataMapEvent(Application.getFrame(),
                dataMap, MapEvent.ADD));
    }

    /**
     * Allows user to save generated SQL in a file.
     */
    public void storeSQLAction() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setDialogTitle("Save SQL Script");

        Resource projectDir = getApplication().getProject().getConfigurationResource();

        if (projectDir != null) {
            fc.setCurrentDirectory(new File(projectDir.getURL().getPath()));
        }

        if (fc.showSaveDialog(getView()) == JFileChooser.APPROVE_OPTION) {
            refreshGeneratorAction();

            try {
                File file = fc.getSelectedFile();
                FileWriter fw = new FileWriter(file);
                PrintWriter pw = new PrintWriter(fw);
                pw.print(textForSQL);
                pw.flush();
                pw.close();
            } catch (IOException ex) {
                reportError("Error Saving SQL", ex);
            }
        }
    }

    private ProjectController getProjectController() {
        return getApplication().getFrameController().getProjectController();
    }

    public void closeAction() {
        view.dispose();
    }

}
