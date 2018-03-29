package org.apache.cayenne.configuration.event;

import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.event.EntityEvent;

public class DbEntityEvent extends EntityEvent{

    public DbEntityEvent(Object src, Entity entity) {
        super(src, entity);
    }

    public DbEntityEvent(Object src, Entity entity, int id) {
        super(src, entity, id);
    }

    public DbEntityEvent(Object src, Entity entity, String oldName) {
        super(src, entity, oldName);
    }

}
