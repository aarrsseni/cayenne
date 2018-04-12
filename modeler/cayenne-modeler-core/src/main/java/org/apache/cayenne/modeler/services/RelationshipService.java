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

import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;

/**
 * @since 4.1
 */
public interface RelationshipService {

    void createRelationship();

    void createDbRelationship(DbEntity dbEntity, DbRelationship rel);

    void createObjRelationship(ObjEntity objEntity, ObjRelationship rel);

    void removeObjRelationships(ObjEntity entity, ObjRelationship[] rels);

    void removeDbRelationships(DbEntity entity, DbRelationship[] rels);

    /**
     * Fires events when a obj rel was added
     */
    void fireObjRelationshipEvent(Object src, ProjectController mediator, ObjEntity objEntity, ObjRelationship rel);

    /**
     * Fires events when a db rel was added
     */
    void fireDbRelationshipEvent(Object src, ProjectController mediator, DbEntity dbEntity, DbRelationship rel);

    RemoveServiceStatus isRemove();

    void remove();
}
