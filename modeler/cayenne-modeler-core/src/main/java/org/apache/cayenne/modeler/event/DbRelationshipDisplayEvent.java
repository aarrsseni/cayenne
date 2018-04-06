package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.Relationship;
import org.apache.cayenne.modeler.event.listener.DbRelationshipDisplayListener;

import java.util.EventListener;

public class DbRelationshipDisplayEvent extends RelationshipDisplayEvent{

    public DbRelationshipDisplayEvent(Object src, Relationship relationship, Entity entity, DataMap map, DataChannelDescriptor domain) {
        super(src, relationship, entity, map, domain);
    }

    public DbRelationshipDisplayEvent(Object src, Relationship[] relationships, Entity entity, DataMap map, DataChannelDescriptor domain) {
        super(src, relationships, entity, map, domain);
    }

    public Class<? extends EventListener> getEventListener() {
        return DbRelationshipDisplayListener.class;
    }
}
