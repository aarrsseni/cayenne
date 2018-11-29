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

package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.dialog.db.DataSourceWizard;
import org.apache.cayenne.modeler.dialog.db.load.DbLoadResultDialog;
import org.apache.cayenne.modeler.dialog.db.load.DbLoaderContext;
import org.apache.cayenne.modeler.dialog.db.load.LoadDataMapTask;
import org.apache.cayenne.modeler.editor.DbImportController;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;
import org.apache.cayenne.modeler.services.DbService;
import org.apache.cayenne.modeler.services.ReverseEngineeringService;
import org.apache.cayenne.modeler.util.CayenneAction;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Action that imports database structure into a DataMap.
 */
public class ReverseEngineeringAction extends CayenneAction {

    private static final String ACTION_NAME = "Reengineer Database Schema";
    private static final String ICON_NAME = "icon-dbi-runImport.png";
    private static final String DIALOG_TITLE = "Reengineer DB Schema: Connect to Database";

    public String getIconName() {
        return ICON_NAME;
    }

    @Inject
    private Application application;

    @Inject
    private DbService dbService;

    @Inject
    private DbLoaderContext context;

    @Inject
    private ReverseEngineeringService reverseEngineeringService;

    public ReverseEngineeringAction() {
        super(getActionName());
    }

    public static String getActionName() {
        return ACTION_NAME;
    }

    public void performAction() {
        DBConnectionInfo connectionInfo;
        if (!reverseEngineeringService.datamapPreferencesExist()) {
            DataSourceWizard connectWizard = new DataSourceWizard(getProjectController(), DIALOG_TITLE);
            if (!connectWizard.startupAction()) {
                return ;
            }
            connectionInfo = dbService.getDbConnectionInfo();
            reverseEngineeringService.saveConnectionInfo();
        } else {
            connectionInfo = reverseEngineeringService.getConnectionInfoFromPreferences();
        }

        try {
            dbService.setConnection(dbService.createConnection());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    Application.getFrame(),
                    ex.getMessage(),
                    "Error loading schemas dialog",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DbImportController dbImportController = application.getFrameController().getDbImportController();
        DbLoadResultDialog dbLoadResultDialog = dbImportController.createDialog();
        if(!dbLoadResultDialog.isVisible()) {
            dbImportController.showDialog();
        }

        reverseEngineeringService.checkBuildConfig(connectionInfo);

        runLoaderInThread(context, () -> {
            application.getUndoManager().discardAllEdits();
            try {
                dbService.getConnection().close();
            } catch (SQLException ignored) {}
        });
    }

    /**
     * Connects to DB and delegates processing to DbLoaderController, starting it asynchronously.
     */
    @Override
    public void performAction(ActionEvent event) {
        performAction();
    }

    private void runLoaderInThread(final DbLoaderContext context, final Runnable callback) {
        Thread th = new Thread(() -> {
            LoadDataMapTask task = new LoadDataMapTask(Application.getFrame(), "Reengineering DB", context);
            task.startAndWait();
            SwingUtilities.invokeLater(callback);
        });
        th.start();
    }
}