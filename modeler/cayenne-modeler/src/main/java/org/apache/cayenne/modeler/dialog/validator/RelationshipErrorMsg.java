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

package org.apache.cayenne.modeler.dialog.validator;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DbEntityDisplayEvent;
import org.apache.cayenne.modeler.event.DbRelationshipDisplayEvent;
import org.apache.cayenne.modeler.event.ObjEntityDisplayEvent;
import org.apache.cayenne.modeler.event.ObjRelationshipDisplayEvent;
import org.apache.cayenne.validation.ValidationFailure;

import javax.swing.*;

/**
 * Relationship validation message.
 * 
 */
public class RelationshipErrorMsg extends ValidationDisplayHandler {

    protected DataMap map;
    protected Entity entity;
    protected Relationship rel;

    /**
     * Constructor for RelationshipErrorMsg.
     * 
     * @param result
     */
    public RelationshipErrorMsg(ValidationFailure result) {
        super(result);
        Object object = result.getSource();
        rel = (Relationship) object;
        entity = rel.getSourceEntity();
        map = entity.getDataMap();
        domain = (DataChannelDescriptor) Application
                .getInstance()
                .getProject()
                .getRootNode();
    }

    public void displayField(ProjectController mediator, JFrame frame) {
        // must first display entity, and then switch to relationship display .. so fire
        // twice
        if (entity instanceof ObjEntity) {
            ObjEntityDisplayEvent event = new ObjEntityDisplayEvent(
                    frame,
                    entity,
                    map,
                    domain);

            mediator.fireObjEntityDisplayEvent(event);

            ObjRelationshipDisplayEvent relEvent = new ObjRelationshipDisplayEvent(
                    frame,
                    rel,
                    entity,
                    map,
                    domain);
            mediator.fireObjRelationshipDisplayEvent(relEvent);
        }
        else if (entity instanceof DbEntity) {
            DbEntityDisplayEvent event = new DbEntityDisplayEvent(
                    frame,
                    entity,
                    map,
                    domain);

            mediator.fireDbEntityDisplayEvent(event);

            DbRelationshipDisplayEvent relEvent = new DbRelationshipDisplayEvent(
                    frame,
                    rel,
                    entity,
                    map,
                    domain);
            mediator.fireDbRelationshipDisplayEvent(relEvent);
        }
    }
}
