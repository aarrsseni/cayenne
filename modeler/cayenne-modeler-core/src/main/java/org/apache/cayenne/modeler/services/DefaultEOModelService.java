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
package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.event.DataNodeEvent;
import org.apache.cayenne.configuration.event.DbEntityEvent;
import org.apache.cayenne.configuration.event.ObjEntityEvent;
import org.apache.cayenne.configuration.event.QueryEvent;
import org.apache.cayenne.configuration.server.JNDIDataSourceFactory;
import org.apache.cayenne.configuration.server.XMLPoolingDataSourceFactory;
import org.apache.cayenne.conn.DataSourceInfo;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ClassLoadingService;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DataMapDisplayEvent;
import org.apache.cayenne.modeler.event.DataNodeDisplayEvent;
import org.apache.cayenne.modeler.util.AdapterMapping;
import org.apache.cayenne.wocompat.EOModelProcessor;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @since 4.1
 */
public class DefaultEOModelService implements EOModelService {

    @Inject
    public ProjectController projectController;

    @Inject
    public NodeService nodeService;

    @Inject
    public ClassLoadingService classLoadingService;

    @Override
    public void importEOModel(File file) throws Exception {
        DataMap currentMap = projectController.getCurrentState().getDataMap();

        URL url = file.toURI().toURL();

        EOModelProcessor processor = new EOModelProcessor();

        // load DataNode if we are not merging with an existing map
        if (currentMap == null) {
            loadDataNode(processor.loadModeIndex(url));
        }

        // load DataMap
        DataMap map = processor.loadEOModel(url);
        addDataMap(map, currentMap);
    }

    /**
     * Adds DataMap into the project.
     */
    protected void addDataMap(DataMap map, DataMap currentMap) {

        if (currentMap != null) {
            // merge with existing map... have to memorize map state before and after
            // to do the right events

            Collection<ObjEntity> originalOE = new ArrayList<>(currentMap.getObjEntities());
            Collection<DbEntity> originalDE = new ArrayList<>(currentMap.getDbEntities());
            Collection<QueryDescriptor> originalQueries = new ArrayList<>(currentMap.getQueryDescriptors());

            currentMap.mergeWithDataMap(map);
            map = currentMap;

            // postprocess changes
            Collection<ObjEntity> newOE = new ArrayList<>(currentMap.getObjEntities());
            Collection<DbEntity> newDE = new ArrayList<>(currentMap.getDbEntities());
            Collection<QueryDescriptor> newQueries = new ArrayList<>(currentMap.getQueryDescriptors());

            ObjEntityEvent objEntityEvent = new ObjEntityEvent(this, null);
            QueryEvent queryEvent = new QueryEvent(this, null);

            // 1. ObjEntities
            Collection<ObjEntity> addedOE = new ArrayList<>(newOE);
            addedOE.removeAll(originalOE);
            for (ObjEntity e : addedOE) {
                objEntityEvent.setEntity(e);
                objEntityEvent.setId(MapEvent.ADD);
                projectController.fireEvent(objEntityEvent);
            }

            Collection<ObjEntity> removedOE = new ArrayList<>(originalOE);
            removedOE.removeAll(newOE);
            for (ObjEntity e : removedOE) {
                objEntityEvent.setEntity(e);
                objEntityEvent.setId(MapEvent.REMOVE);
                projectController.fireEvent(objEntityEvent);
            }

            DbEntityEvent dbEntityEvent = new DbEntityEvent(this, null);

            // 2. DbEntities
            Collection<DbEntity> addedDE = new ArrayList<>(newDE);
            addedDE.removeAll(originalDE);
            for(DbEntity e: addedDE) {
                dbEntityEvent.setEntity(e);
                dbEntityEvent.setId(MapEvent.ADD);
                projectController.fireEvent(dbEntityEvent);
            }

            Collection<DbEntity> removedDE = new ArrayList<>(originalDE);
            removedDE.removeAll(newDE);
            for(DbEntity e: removedDE) {
                dbEntityEvent.setEntity(e);
                dbEntityEvent.setId(MapEvent.REMOVE);
                projectController.fireEvent(dbEntityEvent);
            }

            // 3. queries
            Collection<QueryDescriptor> addedQueries = new ArrayList<>(newQueries);
            addedQueries.removeAll(originalQueries);
            for(QueryDescriptor q: addedQueries) {
                queryEvent.setQuery(q);
                queryEvent.setId(MapEvent.ADD);
                projectController.fireEvent(queryEvent);
            }

            Collection<QueryDescriptor> removedQueries = new ArrayList<>(originalQueries);
            removedQueries.removeAll(newQueries);
            for(QueryDescriptor q: removedQueries) {
                queryEvent.setQuery(q);
                queryEvent.setId(MapEvent.REMOVE);
                projectController.fireEvent(queryEvent);
            }

            projectController.fireEvent(new DataMapDisplayEvent(this, map, (DataChannelDescriptor) projectController
                    .getProject()
                    .getRootNode(), projectController.getCurrentState().getNode()));
        }
        else {
            // fix DataMap name, as there maybe a map with the same name already
            ConfigurationNode root = (DataChannelDescriptor) projectController.getProject().getRootNode();
            map.setName(NameBuilder
                    .builder(map, root)
                    .baseName(map.getName())
                    .name());

            // side effect of this operation is that if a node was created, this DataMap
            // will be linked with it...
            projectController.addDataMap(this , map);
        }
    }

    protected void loadDataNode(Map eomodelIndex) {
        // if this is JDBC or JNDI node and connection dictionary is specified, load a
        // DataNode, otherwise ignore it (meaning that pre 5.* EOModels will not have a
        // node).

        String adapter = (String) eomodelIndex.get("adaptorName");
        Map connection = (Map) eomodelIndex.get("connectionDictionary");

        if (adapter != null && connection != null) {
            // this should make created node current, resulting in the new map being added
            // to the node automatically once it is loaded
            DataNodeDescriptor node = nodeService.buildDataNode();

            // configure node...
            if ("JNDI".equalsIgnoreCase(adapter)) {
                node.setDataSourceFactoryType(JNDIDataSourceFactory.class.getName());
                node.setParameters((String) connection.get("serverUrl"));
            }
            else {
                // guess adapter from plugin or driver
                AdapterMapping adapterDefaults = projectController.getAdapterMapping();
                String cayenneAdapter = adapterDefaults.adapterForEOFPluginOrDriver(
                        (String) connection.get("plugin"),
                        (String) connection.get("driver"));
                if (cayenneAdapter != null) {
                    try {
                        Class<DbAdapter> adapterClass = classLoadingService
                                .loadClass(DbAdapter.class, cayenneAdapter);
                        node.setAdapterType(adapterClass.toString());
                    }
                    catch (Throwable ex) {
                        // ignore...
                    }
                }

                node
                        .setDataSourceFactoryType(XMLPoolingDataSourceFactory.class
                                .getName());

                DataSourceInfo dsi = node.getDataSourceDescriptor();

                dsi.setDataSourceUrl(keyAsString(connection, "URL"));
                dsi.setJdbcDriver(keyAsString(connection, "driver"));
                dsi.setPassword(keyAsString(connection, "password"));
                dsi.setUserName(keyAsString(connection, "username"));
            }

            DataChannelDescriptor domain = (DataChannelDescriptor) projectController
                    .getProject()
                    .getRootNode();
            domain.getNodeDescriptors().add(node);

            // send events after the node creation is complete
            projectController.fireEvent(
                    new DataNodeEvent(this, node, MapEvent.ADD));
            projectController.fireEvent(
                    new DataNodeDisplayEvent(
                            this,
                            (DataChannelDescriptor) projectController
                                    .getProject()
                                    .getRootNode(),
                            node));
        }
    }

    // CAY-246 - if user name or password is all numeric, it will
    // be returned as number, so we can't cast dictionary keys to String
    private String keyAsString(Map map, String key) {
        Object value = map.get(key);
        return (value != null) ? value.toString() : null;
    }
}
