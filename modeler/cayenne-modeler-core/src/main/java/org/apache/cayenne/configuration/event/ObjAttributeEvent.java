package org.apache.cayenne.configuration.event;

import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.event.AttributeEvent;
import org.apache.cayenne.map.event.ObjAttributeListener;

import java.util.EventListener;

public class ObjAttributeEvent extends AttributeEvent{

    public ObjAttributeEvent(Object src, Attribute attr, Entity entity) {
        super(src, attr, entity);
    }

    public ObjAttributeEvent(Object src, Attribute attr, Entity entity, int id) {
        super(src, attr, entity, id);
    }

    public ObjAttributeEvent(Object src, Attribute attr, Entity entity, String oldName) {
        super(src, attr, entity, oldName);
    }

    public Class<? extends EventListener> getEventListener() {
        return ObjAttributeListener.class;
    }
}
