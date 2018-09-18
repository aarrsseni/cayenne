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
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.DbRelationshipEvent;
import org.apache.cayenne.configuration.event.ObjRelationshipEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateRelationshipEvent;
import org.apache.cayenne.modeler.event.DbRelationshipDisplayEvent;
import org.apache.cayenne.modeler.event.ObjRelationshipDisplayEvent;
import org.apache.cayenne.modeler.event.RemoveDbRelationshipsEvent;
import org.apache.cayenne.modeler.event.RemoveObjRelationshipsEvent;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;
import org.apache.cayenne.modeler.util.ProjectUtil;
import org.apache.cayenne.util.DeleteRuleUpdater;

/**
 * @since 4.1
 */
public class DefaultRelationshipService implements RelationshipService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createRelationship() {
        ObjEntity objEnt = projectController.getCurrentState().getObjEntity();
        if (objEnt != null) {

            ObjRelationship rel = new ObjRelationship();
            rel.setName(NameBuilder.builder(rel, objEnt).name());
            createObjRelationship(objEnt, rel);

            projectController.fireEvent(new CreateRelationshipEvent(this, objEnt, rel));
        } else {
            DbEntity dbEnt = projectController.getCurrentState().getDbEntity();
            if (dbEnt != null) {

                DbRelationship rel = new DbRelationship();
                rel.setName(NameBuilder.builder(rel, dbEnt).name());
                createDbRelationship(dbEnt, rel);

                projectController.fireEvent(new CreateRelationshipEvent(this, dbEnt, rel));
            }
        }
    }

    public void createObjRelationship(ObjEntity objEntity, ObjRelationship rel) {
        rel.setSourceEntity(objEntity);
        DeleteRuleUpdater.updateObjRelationship(rel);

        objEntity.addRelationship(rel);
        fireObjRelationshipEvent(this, projectController, objEntity, rel);
    }

    public void createDbRelationship(DbEntity dbEntity, DbRelationship rel) {
        rel.setSourceEntity(dbEntity);
        dbEntity.addRelationship(rel);

        fireDbRelationshipEvent(this, projectController, dbEntity, rel);
    }

    public void removeObjRelationships(ObjEntity entity, ObjRelationship[] rels) {

        for (ObjRelationship rel : rels) {
            entity.removeRelationship(rel.getName());
            ObjRelationshipEvent e = new ObjRelationshipEvent(this,
                    rel, entity, MapEvent.REMOVE);
            projectController.fireEvent(e);
        }
    }

    public void removeDbRelationships(DbEntity entity, DbRelationship[] rels) {

        for (DbRelationship rel : rels) {
            entity.removeRelationship(rel.getName());

            DbRelationshipEvent e = new DbRelationshipEvent(this,
                    rel, entity, MapEvent.REMOVE);
            projectController.fireEvent(e);
        }

        ProjectUtil.cleanObjMappings(projectController.getCurrentState().getDataMap());
    }

    /**
     * Fires events when a obj rel was added
     */
    public void fireObjRelationshipEvent(Object src, ProjectController mediator, ObjEntity objEntity,
                                         ObjRelationship rel) {

        mediator.fireEvent(new ObjRelationshipEvent(src, rel, objEntity, MapEvent.ADD));

        ObjRelationshipDisplayEvent rde = new ObjRelationshipDisplayEvent(src, rel, objEntity, mediator.getCurrentState().getDataMap(),
                (DataChannelDescriptor) mediator.getProject().getRootNode());

        mediator.fireEvent(rde);
    }

    /**
     * Fires events when a db rel was added
     */
    public void fireDbRelationshipEvent(Object src, ProjectController mediator, DbEntity dbEntity, DbRelationship rel) {

        mediator.fireEvent(new DbRelationshipEvent(src, rel, dbEntity, MapEvent.ADD));

        DbRelationshipDisplayEvent rde = new DbRelationshipDisplayEvent(src, rel, dbEntity, mediator.getCurrentState().getDataMap(),
                (DataChannelDescriptor) mediator.getProject().getRootNode());

        mediator.fireEvent(rde);
    }

    @Override
    public RemoveServiceStatus isRemove() {
        ObjRelationship[] rels = projectController
                .getCurrentState()
                .getObjRels();
        if (rels != null && rels.length > 0) {
            if(rels.length == 1) {
                return new RemoveServiceStatus("ObjRelationship", rels[0].getName());
            } else {
                return new RemoveServiceStatus(null, "selected ObjRelationships");
            }
        } else {
            DbRelationship[] dbRels = projectController
                    .getCurrentState()
                    .getDbRels();
            if (dbRels != null && dbRels.length > 0) {
                if(dbRels.length == 1) {
                    return new RemoveServiceStatus("DbRelationship", dbRels[0].getName());
                } else {
                    return new RemoveServiceStatus(null, "selected DbRelationships");
                }
            }
        }
        return null;
    }

    @Override
    public void remove() {
        ObjRelationship[] rels = projectController
                .getCurrentState()
                .getObjRels();
        if(rels != null && rels.length > 0) {
            ObjEntity entity = projectController.getCurrentState().getObjEntity();
            removeObjRelationships(entity, rels);
            projectController.fireEvent(new RemoveObjRelationshipsEvent(this, entity, rels));
        } else {
            DbRelationship[] dbRels = projectController
                    .getCurrentState()
                    .getDbRels();
            if(dbRels != null && dbRels.length > 0) {
                DbEntity entity = projectController.getCurrentState().getDbEntity();
                removeDbRelationships(entity, dbRels);
                projectController.fireEvent(new RemoveDbRelationshipsEvent(this, entity, dbRels));
            }
        }
    }
}
