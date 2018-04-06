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

package org.apache.cayenne.modeler.dialog.db.load;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.dbsync.reverse.dbimport.*;
import org.apache.cayenne.dbsync.reverse.dbload.DbLoaderDelegate;
import org.apache.cayenne.dbsync.reverse.filters.FiltersConfigBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DbLoaderExceptionEvent;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;
import org.apache.cayenne.modeler.services.DbService;
import org.apache.cayenne.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;

/**
 * @since 4.0
 */
public class DbLoaderContext {

    private static Logger LOGGER = LoggerFactory.getLogger(DbLoaderContext.class);

    private DbImportConfiguration config;

    @Inject
    private ProjectController projectController;

    private boolean existingMap;
    private DataMap dataMap;
    private boolean stopping;
    private String loadStatusNote;

    @Inject
    private DbService dbService;

    public DbLoaderContext() {
    }

    DataMap getDataMap() {
        return dataMap;
    }

    boolean isExistingDataMap() {
        return existingMap;
    }

    ProjectController getProjectController() {
        return projectController;
    }

    void setConfig(DbImportConfiguration config) {
        this.config = config;
    }

    DbImportConfiguration getConfig() {
        return config;
    }

    public boolean isStopping() {
        return stopping;
    }

    void setStopping(boolean stopping) {
        this.stopping = stopping;
    }

    String getStatusNote() {
        return loadStatusNote;
    }

    void setStatusNote(String loadStatusNote) {
        this.loadStatusNote = loadStatusNote;
    }

    public void buildConfig(DbLoaderData dialog) {

        // Build filters
        ReverseEngineering reverseEngineering = new ReverseEngineering();
        reverseEngineering.addCatalog(new Catalog(dialog.getCatalog()));
        reverseEngineering.addSchema(new Schema(dialog.getSchema()));
        reverseEngineering.addIncludeTable(new IncludeTable(dialog.getTableIncludePattern()));
        if(dialog.getTableExcludePattern() != null) {
            reverseEngineering.addExcludeTable(new ExcludeTable(dialog.getTableExcludePattern()));
        }
        // Add here auto_pk_support table
        reverseEngineering.addExcludeTable(new ExcludeTable("auto_pk_support|AUTO_PK_SUPPORT"));
        reverseEngineering.addIncludeProcedure(new IncludeProcedure(dialog.getProcedureNamePattern()));
        FiltersConfigBuilder filtersConfigBuilder = new FiltersConfigBuilder(reverseEngineering);

        DbImportConfiguration config = new DbImportConfiguration() {
            @Override
            public DbLoaderDelegate createLoaderDelegate() {
                return new LoaderDelegate(DbLoaderContext.this);
            }
        };

        // Build config
        DBConnectionInfo connectionInfo = dbService.getDbConnectionInfo();
        config.setAdapter(dbService.getDbAdapter().getClass().getName());
        config.setUsername(connectionInfo.getUserName());
        config.setPassword(connectionInfo.getPassword());
        config.setDriver(connectionInfo.getJdbcDriver());
        config.setUrl(connectionInfo.getUrl());
        config.getDbLoaderConfig().setFiltersConfig(filtersConfigBuilder.build());
        config.setMeaningfulPkTables(dialog.getMeaningfulPk());
        config.setNamingStrategy(dialog.getNamingStrategy());
        config.setUsePrimitives(dialog.isUsePrimitives());
        config.setUseJava7Types(dialog.isUseJava7Typed());
        setConfig(config);

        prepareDataMap();
    }

    private void prepareDataMap() {
        dataMap = getProjectController().getCurrentState().getDataMap();
        existingMap = dataMap != null;

        if (!existingMap) {
            ConfigurationNode root = getProjectController().getProject().getRootNode();
            dataMap = new DataMap();
            dataMap.setName(NameBuilder.builder(dataMap, root).name());
        }
        if (dataMap.getConfigurationSource() != null) {
            getConfig().setTargetDataMap(new File(dataMap.getConfigurationSource().getURL().getPath()));
        }
    }

    public void processWarn(final Throwable th, final String message) {
        LOGGER.warn(message, Util.unwindException(th));
    }

    public void processException(final Throwable th, final String message) {
        LOGGER.info("Exception on reverse engineering", Util.unwindException(th));
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                projectController.fireEvent(new DbLoaderExceptionEvent(this, th, message));
            }
        });
    }
}
