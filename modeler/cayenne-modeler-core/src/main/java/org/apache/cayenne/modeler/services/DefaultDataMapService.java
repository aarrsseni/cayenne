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
import org.apache.cayenne.configuration.*;
import org.apache.cayenne.configuration.event.DataMapEvent;
import org.apache.cayenne.configuration.event.DataNodeEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateDataMapEvent;
import org.apache.cayenne.modeler.event.LinkDataMapEvent;
import org.apache.cayenne.modeler.event.LinkDataMapsEvent;
import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.resource.URLResource;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @since 4.1
 */
public class DefaultDataMapService implements DataMapService {

    @Inject
    public ProjectController projectController;

    @Inject
    private ConfigurationNameMapper nameMapper;

    @Override
    public void createDataMap() {

        DataChannelDescriptor dataChannelDescriptor = (DataChannelDescriptor) projectController
                .getProject()
                .getRootNode();

        DataMap map = new DataMap();
        map.setName(NameBuilder.builder(map, dataChannelDescriptor).name());
        createDataMap(map);

        projectController.fireEvent(new CreateDataMapEvent(this, dataChannelDescriptor, map));
    }

    /** Calls addDataMap() or creates new data map if no data node selected. */
    public void createDataMap(DataMap map) {
        projectController.addDataMap(this, map);
    }

    @Override
    public void importDataMap(File dataMapFile) throws Exception {
        URL url = dataMapFile.toURI().toURL();

        //TODO: Move DataMapLoader binding to CayenneModelerCore when will be ready
        DataMap newMap = projectController.getInjector().getInstance(DataMapLoader.class).load(new URLResource(url));

        ConfigurationNode root = projectController.getProject().getRootNode();
        newMap.setName(NameBuilder
                .builder(newMap, root)
                .baseName(newMap.getName())
                .name());

        Resource baseResource = ((DataChannelDescriptor) root).getConfigurationSource();

        if (baseResource != null) {
            Resource dataMapResource = baseResource.getRelativeResource(nameMapper.configurationLocation(newMap));
            newMap.setConfigurationSource(dataMapResource);
        }

        projectController.addDataMap(this, newMap);
    }

    @Override
    public void linkDataMaps() {
        DataChannelDescriptor dataChannelDescriptor = (DataChannelDescriptor) projectController.getProject().getRootNode();

        Collection<String> linkedDataMaps = new ArrayList<>();
        DataNodeDescriptor dataNodeDescriptor = projectController.getCurrentState().getNode();
        for (DataNodeDescriptor dataNodeDesc : dataChannelDescriptor.getNodeDescriptors()) {
            linkedDataMaps.addAll(dataNodeDesc.getDataMapNames());
        }

        for (DataMap dataMap : dataChannelDescriptor.getDataMaps()) {
            if (!linkedDataMaps.contains(dataMap.getName())) {
                dataNodeDescriptor.getDataMapNames().add(dataMap.getName());
                projectController.fireEvent(new DataNodeEvent(this, dataNodeDescriptor));
            }
        }
        projectController.fireEvent(new LinkDataMapsEvent(this, dataNodeDescriptor, linkedDataMaps));
    }

    public void linkDataMap(DataMap map, DataNodeDescriptor node) {
        if (map == null) {
            return;
        }

        // no change?
        if (node != null && node.getDataMapNames().contains(map.getName())) {
            return;
        }

        DataChannelDescriptor dataChannelDescriptor = (DataChannelDescriptor) projectController.getProject().getRootNode();
        Collection<DataNodeDescriptor> unlinkedNodes = new ArrayList<>();

        // unlink map from any nodes
        // Theoretically only one node may contain a datamap at each given time.
        // Being paranoid, we will still scan through all.
        for (DataNodeDescriptor nextNode : dataChannelDescriptor.getNodeDescriptors()) {
            if (nextNode.getDataMapNames().contains(map.getName())) {
                nextNode.getDataMapNames().remove(map.getName());
                projectController.fireEvent(new DataNodeEvent(this, nextNode));
                unlinkedNodes.add(nextNode);
            }
        }

        // link to a selected node
        if (node != null) {
            node.getDataMapNames().add(map.getName());

            // announce DataNode change
            projectController.fireEvent(new DataNodeEvent(this, node));
        }

        projectController.fireEvent(new LinkDataMapEvent(this, map, node, unlinkedNodes, projectController));
    }

    public void removeDataMap(DataMap map) {

        DataChannelDescriptor domain = (DataChannelDescriptor) projectController.getProject().getRootNode();
        DataMapEvent e = new DataMapEvent(this, map, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        domain.getDataMaps().remove(map);
        if (map.getConfigurationSource() != null) {
            URL mapURL = map.getConfigurationSource().getURL();
            Collection<URL> unusedResources = projectController.getProject().getUnusedResources();
            unusedResources.add(mapURL);
        }

        for (DataNodeDescriptor node : domain.getNodeDescriptors()) {
            if (node.getDataMapNames().contains(map.getName())) {
                removeDataMapFromDataNode(node, map);
            }
        }

        projectController.fireEvent(e);
    }

    public void removeDataMapFromDataNode(DataNodeDescriptor node, DataMap map) {

        DataNodeEvent e = new DataNodeEvent(this, node);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        node.getDataMapNames().remove(map.getName());

        // Force reloading of the data node in the browse view
        projectController.fireEvent(e);
    }

}
