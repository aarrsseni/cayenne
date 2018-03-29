package org.apache.cayenne.configuration.event;

import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.Relationship;
import org.apache.cayenne.map.event.RelationshipEvent;

public class DbRelationshipEvent extends RelationshipEvent {

    public DbRelationshipEvent(Object src, Relationship rel, Entity entity) {
        super(src, rel, entity);
    }

    public DbRelationshipEvent(Object src, Relationship rel, Entity entity, int id) {
        super(src, rel, entity, id);
    }

    public DbRelationshipEvent(Object src, Relationship rel, Entity entity, String oldName) {
        super(src, rel, entity, oldName);
    }

}
