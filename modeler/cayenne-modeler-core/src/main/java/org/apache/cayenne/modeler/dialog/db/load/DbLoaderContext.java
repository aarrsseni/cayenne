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
import org.apache.cayenne.dbsync.reverse.dbimport.DbImportConfiguration;
import org.apache.cayenne.dbsync.reverse.dbimport.ReverseEngineering;
import org.apache.cayenne.dbsync.reverse.dbload.DbLoaderDelegate;
import org.apache.cayenne.dbsync.reverse.filters.FiltersConfigBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.ReverseEngineeringEvent;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;
import org.apache.cayenne.modeler.services.DbService;

import java.io.File;

/**
 * @since 4.0
 */
public class DbLoaderContext {

    private DbImportConfiguration config;

    @Inject
    private ProjectController projectController;

    @Inject
    private DbService dbService;

    private boolean existingMap;
    private DataMap dataMap;
    private boolean stopping;
    private String loadStatusNote;

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

    private void setConfig(DbImportConfiguration config) {
        this.config = config;
    }

    DbImportConfiguration getConfig() {
        return config;
    }

    boolean isStopping() {
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

    public boolean buildConfig(DBConnectionInfo connectionInfo) {
        if (connectionInfo == null) {
            return false;
        }
        // Build reverse engineering from metadata and dialog values
        ReverseEngineering metaReverseEngineering = projectController.getMetaData().get(getProjectController().getCurrentState().getDataMap(), ReverseEngineering.class);
        projectController.fireEvent(new ReverseEngineeringEvent(this, metaReverseEngineering));
        // Create copy of metaReverseEngineering
        ReverseEngineering reverseEngineering = new ReverseEngineering(metaReverseEngineering);

        DbImportConfiguration config = new DbImportConfiguration() {
            @Override
            public DbLoaderDelegate createLoaderDelegate() {
                return new LoaderDelegate(DbLoaderContext.this);
            }
        };
        fillConfig(config, connectionInfo, reverseEngineering);
        setConfig(config);

        prepareDataMap();

        return true;
    }

    // Fill config from metadata reverseEngineering
    private void fillConfig(DbImportConfiguration config, DBConnectionInfo connectionInfo,
                            ReverseEngineering reverseEngineering) {
        FiltersConfigBuilder filtersConfigBuilder = new FiltersConfigBuilder(reverseEngineering);
        config.setAdapter(connectionInfo.getDbAdapter());
        config.setUsername(connectionInfo.getUserName());
        config.setPassword(connectionInfo.getPassword());
        config.setDriver(connectionInfo.getJdbcDriver());
        config.setUrl(connectionInfo.getUrl());
        config.getDbLoaderConfig().setFiltersConfig(filtersConfigBuilder.build());
        config.setMeaningfulPkTables(reverseEngineering.getMeaningfulPkTables());
        config.setNamingStrategy(reverseEngineering.getNamingStrategy());
        config.setDefaultPackage(reverseEngineering.getDefaultPackage());
        config.setStripFromTableNames(reverseEngineering.getStripFromTableNames());
        config.setUsePrimitives(reverseEngineering.isUsePrimitives());
        config.setUseJava7Types(reverseEngineering.isUseJava7Types());
        config.setForceDataMapCatalog(reverseEngineering.isForceDataMapCatalog());
        config.setForceDataMapSchema(reverseEngineering.isForceDataMapSchema());
        config.setSkipRelationshipsLoading(reverseEngineering.getSkipRelationshipsLoading());
        config.setSkipPrimaryKeyLoading(reverseEngineering.getSkipPrimaryKeyLoading());
        config.setTableTypes(new String[] {"TABLE", "VIEW", "SYSTEM TABLE"});
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
}
