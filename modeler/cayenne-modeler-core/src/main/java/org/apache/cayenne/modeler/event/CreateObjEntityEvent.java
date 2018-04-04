package org.apache.cayenne.modeler.event;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjEntity;

import java.util.EventListener;
import java.util.EventObject;

public class CreateObjEntityEvent extends EventObject {

    private DataMap dataMap;
    private ObjEntity objEntity;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateObjEntityEvent(Object source) {
        super(source);
    }

    public CreateObjEntityEvent(Object source, DataMap dataMap, ObjEntity objEntity) {
        this(source);
        this.dataMap = dataMap;
        this.objEntity = objEntity;
    }

    public ObjEntity getObjEntity() {
        return objEntity;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public Class<? extends EventListener> getEventListener() {
        return CreateObjEntityListener.class;
    }
}
