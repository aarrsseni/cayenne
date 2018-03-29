package org.apache.cayenne.configuration.event;

import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.event.AttributeEvent;

public class DbAttributeEvent extends AttributeEvent{

    public DbAttributeEvent(Object src, Attribute attr, Entity entity) {
        super(src, attr, entity);
    }

    public DbAttributeEvent(Object src, Attribute attr, Entity entity, int id) {
        super(src, attr, entity, id);
    }

    public DbAttributeEvent(Object src, Attribute attr, Entity entity, String oldName) {
        super(src, attr, entity, oldName);
    }

}
