package org.apache.cayenne.configuration.event;

import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.event.EntityEvent;
import org.apache.cayenne.map.event.ObjEntityListener;

import java.util.EventListener;

public class ObjEntityEvent extends EntityEvent{

    public ObjEntityEvent(Object src, Entity entity) {
        super(src, entity);
    }

    public ObjEntityEvent(Object src, Entity entity, int id) {
        super(src, entity, id);
    }

    public ObjEntityEvent(Object src, Entity entity, String oldName) {
        super(src, entity, oldName);
    }

    public Class<? extends EventListener> getEventListener() {
        return ObjEntityListener.class;
    }

}
