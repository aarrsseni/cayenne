package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.Relationship;

public class ObjRelationshipDisplayEvent extends RelationshipDisplayEvent{

    public ObjRelationshipDisplayEvent(Object src, Relationship relationship, Entity entity, DataMap map, DataChannelDescriptor domain) {
        super(src, relationship, entity, map, domain);
    }

    public ObjRelationshipDisplayEvent(Object src, Relationship[] relationships, Entity entity, DataMap map, DataChannelDescriptor domain) {
        super(src, relationships, entity, map, domain);
    }

}
