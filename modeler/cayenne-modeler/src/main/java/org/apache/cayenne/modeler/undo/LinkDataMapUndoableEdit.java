/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.modeler.undo;

import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.event.DataNodeEvent;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.action.LinkDataMapAction;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.Collection;

public class LinkDataMapUndoableEdit extends CayenneUndoableEdit {

    DataMap map;
    DataNodeDescriptor node;
    Collection<DataNodeDescriptor> unlinkedNodes;
    ProjectController mediator;

    @Override
    public String getPresentationName() {
        return "Link unlinked DataMaps";
    }

    public LinkDataMapUndoableEdit(DataMap map, DataNodeDescriptor node, Collection<DataNodeDescriptor> unlinkedNodes, ProjectController mediator) {
        this.map = map;
        this.node = node;
        this.unlinkedNodes = unlinkedNodes;
        this.mediator = mediator;
    }

    @Override
    public void redo() throws CannotRedoException {
        LinkDataMapAction action = actionManager.getAction(LinkDataMapAction.class);
        action.linkDataMap(map, node);
    }

    @Override
    public void undo() throws CannotUndoException {
        if (node != null) {
            node.getDataMapNames().remove(map.getName());
            mediator.fireDataNodeEvent(new DataNodeEvent(this, node));
        }

        if (!unlinkedNodes.isEmpty()) {
            for (DataNodeDescriptor unlinkedNode : unlinkedNodes) {
                unlinkedNode.getDataMapNames().add(map.getName());
                mediator.fireDataNodeEvent(new DataNodeEvent(this, unlinkedNode));
            }
        }
    }

}
