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
import org.apache.cayenne.access.dbsync.SkipSchemaUpdateStrategy;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.event.DataNodeEvent;
import org.apache.cayenne.configuration.server.XMLPoolingDataSourceFactory;
import org.apache.cayenne.conn.DataSourceInfo;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateNodeEvent;
import org.apache.cayenne.modeler.event.DataNodeDisplayEvent;

/**
 * @since 4.1
 */
public class DefaultNodeService implements NodeService {

    @Inject
    public ProjectController projectController;

    @Override
    public void createNode() {
        DataChannelDescriptor domain = (DataChannelDescriptor) projectController.getProject().getRootNode();

        DataNodeDescriptor node = new DataNodeDescriptor();
        node.setName(NameBuilder.builder(node, domain).name());
        node.setDataChannelDescriptor(domain);

        DataSourceInfo src = new DataSourceInfo();
        node.setDataSourceDescriptor(src);

        // by default create JDBC Node
        node.setDataSourceFactoryType(XMLPoolingDataSourceFactory.class.getName());
        node.setSchemaUpdateStrategyType(SkipSchemaUpdateStrategy.class.getName());

        domain.getNodeDescriptors().add(node);
        projectController.fireEvent(new DataNodeEvent(this, node, MapEvent.ADD));
        projectController.fireEvent(new DataNodeDisplayEvent(this, domain, node));

        projectController.fireEvent(new CreateNodeEvent(this, node));
    }

    public void createDataNode(DataNodeDescriptor node) {
        DataChannelDescriptor domain = (DataChannelDescriptor) projectController.getProject().getRootNode();
        domain.getNodeDescriptors().add(node);
        projectController.fireEvent(new DataNodeEvent(this, node, MapEvent.ADD));
        projectController.fireEvent(new DataNodeDisplayEvent(this, domain, node));
    }

    public void removeDataNode(DataNodeDescriptor node) {
        DataChannelDescriptor domain = (DataChannelDescriptor) projectController.getProject().getRootNode();
        DataNodeEvent e = new DataNodeEvent(this, node, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        domain.getNodeDescriptors().remove(node);
        projectController.fireEvent(e);
    }

    /**
     * Creates a new DataNode, adding to the current domain, but doesn't send
     * any events.
     */
    public DataNodeDescriptor buildDataNode() {
        DataChannelDescriptor domain = (DataChannelDescriptor) projectController.getProject().getRootNode();

        DataNodeDescriptor node = buildDataNode(domain);

        DataSourceInfo src = new DataSourceInfo();
        node.setDataSourceDescriptor(src);

        // by default create JDBC Node
        node.setDataSourceFactoryType(XMLPoolingDataSourceFactory.class.getName());
        node.setSchemaUpdateStrategyType(SkipSchemaUpdateStrategy.class.getName());

        return node;
    }

    /**
     * A factory method that makes a new DataNode.
     */
    DataNodeDescriptor buildDataNode(DataChannelDescriptor dataChannelDescriptor) {
        DataNodeDescriptor node = new DataNodeDescriptor();
        node.setName(NameBuilder.builder(node, dataChannelDescriptor).name());
        node.setDataChannelDescriptor(dataChannelDescriptor);

        return node;
    }
}
