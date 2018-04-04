package org.apache.cayenne.modeler.event;

import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;

import java.util.EventListener;
import java.util.EventObject;

public class CreateRelationshipEvent extends EventObject{

    private DbEntity dbEntity;
    private ObjEntity objEntity;
    private DbRelationship dbRelationship;
    private ObjRelationship objRelationship;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateRelationshipEvent(Object source) {
        super(source);
    }

    public CreateRelationshipEvent(Object source, DbEntity dbEntity, DbRelationship dbRelationship) {
        this(source);
        this.dbEntity = dbEntity;
        this.dbRelationship = dbRelationship;
    }

    public CreateRelationshipEvent(Object source, ObjEntity objEntity, ObjRelationship objRelationship) {
        this(source);
        this.objEntity = objEntity;
        this.objRelationship = objRelationship;
    }

    public DbEntity getDbEntity() {
        return dbEntity;
    }

    public ObjEntity getObjEntity() {
        return objEntity;
    }

    public DbRelationship getDbRelationship() {
        return dbRelationship;
    }

    public ObjRelationship getObjRelationship() {
        return objRelationship;
    }

    public Class<? extends EventListener> getEventListener(){
        return CreateRelationshipListener.class;
    }
}
