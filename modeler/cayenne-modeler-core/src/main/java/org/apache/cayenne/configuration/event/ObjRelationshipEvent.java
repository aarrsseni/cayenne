package org.apache.cayenne.configuration.event;

import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.Relationship;
import org.apache.cayenne.map.event.ObjRelationshipListener;
import org.apache.cayenne.map.event.RelationshipEvent;

import java.util.EventListener;

public class ObjRelationshipEvent extends RelationshipEvent{

    public ObjRelationshipEvent(Object src, Relationship rel, Entity entity) {
        super(src, rel, entity);
    }

    public ObjRelationshipEvent(Object src, Relationship rel, Entity entity, int id) {
        super(src, rel, entity, id);
    }

    public ObjRelationshipEvent(Object src, Relationship rel, Entity entity, String oldName) {
        super(src, rel, entity, oldName);
    }

    public Class<? extends EventListener> getEventListener() {
        return ObjRelationshipListener.class;
    }
}
