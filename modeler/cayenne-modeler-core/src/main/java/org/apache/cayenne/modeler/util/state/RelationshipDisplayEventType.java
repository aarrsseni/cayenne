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

package org.apache.cayenne.modeler.util.state;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.*;

import java.util.ArrayList;
import java.util.List;

class RelationshipDisplayEventType extends EntityDisplayEventType {

    RelationshipDisplayEventType(ProjectController controller) {
        super(controller);
    }

    @Override
    public void fireLastDisplayEvent() {
        DataChannelDescriptor dataChannel = (DataChannelDescriptor) controller.getProject().getRootNode();
        if (!dataChannel.getName().equals(preferences.getDomain())) {
            return;
        }

        DataNodeDescriptor dataNode = dataChannel.getNodeDescriptor(preferences.getNode());
        DataMap dataMap = dataChannel.getDataMap(preferences.getDataMap());
        if (dataMap == null) {
            return;
        }

        Entity entity = getLastEntity(dataMap);
        if (entity == null) {
            return;
        }

        Relationship[] relationships = getLastEntityRelationships(entity);

        if (entity instanceof ObjEntity) {
            controller.fireObjEntityDisplayEvent(new ObjEntityDisplayEvent(this, entity, dataMap, dataNode, dataChannel));
            controller.fireObjRelationshipDisplayEvent(new ObjRelationshipDisplayEvent(this, relationships, entity, dataMap, dataChannel));
        } else if (entity instanceof DbEntity) {
            controller.fireDbEntityDisplayEvent(new DbEntityDisplayEvent(this, entity, dataMap, dataNode, dataChannel));
            controller.fireDbRelationshipDisplayEvent(new DbRelationshipDisplayEvent(this, relationships, entity, dataMap, dataChannel));
        }
    }

    @Override
    public void saveLastDisplayEvent() {
        preferences.setEvent(RelationshipDisplayEvent.class.getSimpleName());
        preferences.setDomain(controller.getCurrentState().getDomain().getName());
        preferences.setNode(controller.getCurrentState().getNode() != null ? controller.getCurrentState().getNode().getName() : "");
        preferences.setDataMap(controller.getCurrentState().getDataMap().getName());

        if (controller.getCurrentState().getObjEntity() != null) {
            preferences.setObjEntity(controller.getCurrentState().getObjEntity().getName());
            preferences.setObjRels(parseToString(controller.getCurrentState().getObjRels()));
            preferences.setDbEntity(null);
        } else if (controller.getCurrentState().getDbEntity() != null) {
            preferences.setDbEntity(controller.getCurrentState().getDbEntity().getName());
            preferences.setDbRels(parseToString(controller.getCurrentState().getDbRels()));
            preferences.setObjEntity(null);
        }
    }

    private Relationship[] getLastEntityRelationships(Entity entity) {
        List<Relationship> relationshipList = new ArrayList<>();

        String rels = (entity instanceof ObjEntity) ? preferences.getObjRels() : preferences.getDbRels();
        for (String objRelName : rels.split(",")) {
            Relationship rel = entity.getRelationship(objRelName);
            if(rel != null) {
                relationshipList.add(rel);
            }
        }

        return relationshipList.toArray(new Relationship[0]);
    }

}
