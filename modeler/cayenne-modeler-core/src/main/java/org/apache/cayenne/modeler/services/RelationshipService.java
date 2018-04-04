package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.modeler.ProjectController;

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
}
