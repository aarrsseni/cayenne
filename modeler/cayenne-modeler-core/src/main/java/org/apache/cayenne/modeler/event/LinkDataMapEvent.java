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
package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.listener.LinkDataMapListener;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;

/**
 * @since 4.1
 */
public class LinkDataMapEvent extends EventObject{

    private DataMap dataMap;
    private DataNodeDescriptor node;
    private Collection<DataNodeDescriptor> unlinkedNodes;
    private ProjectController projectController;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public LinkDataMapEvent(Object source) {
        super(source);
    }

    public LinkDataMapEvent(Object source, DataMap dataMap, DataNodeDescriptor node, Collection<DataNodeDescriptor> unlinkedNodes, ProjectController projectController) {
        this(source);
        this.dataMap = dataMap;
        this.node = node;
        this.unlinkedNodes = unlinkedNodes;
        this.projectController = projectController;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public DataNodeDescriptor getNode() {
        return node;
    }

    public Collection<DataNodeDescriptor> getUnlinkedNodes() {
        return unlinkedNodes;
    }

    public ProjectController getProjectController() {
        return projectController;
    }

    public Class<? extends EventListener> getEventListener() {
        return LinkDataMapListener.class;
    }
}
