package org.apache.cayenne.modeler.event;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;

import java.util.EventListener;
import java.util.EventObject;

public class CreateDbEntityEvent extends EventObject {

    private DataMap dataMap;
    private DbEntity dbEntity;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateDbEntityEvent(Object source) {
        super(source);
    }

    public CreateDbEntityEvent(Object source, DataMap dataMap, DbEntity dbEntity) {
        this(source);
        this.dataMap = dataMap;
        this.dbEntity = dbEntity;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public DbEntity getDbEntity() {
        return dbEntity;
    }

    public Class<? extends EventListener> getEventListener() {
        return CreateDbEntityListener.class;
    }
}
