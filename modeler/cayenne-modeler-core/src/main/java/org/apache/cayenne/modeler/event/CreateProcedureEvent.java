package org.apache.cayenne.modeler.event;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Procedure;

import java.util.EventListener;
import java.util.EventObject;

public class CreateProcedureEvent extends EventObject{

    private DataMap map;
    private Procedure procedure;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateProcedureEvent(Object source) {
        super(source);
    }

    public CreateProcedureEvent(Object source, DataMap map, Procedure procedure){
        this(source);
        this.map = map;
        this.procedure = procedure;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public DataMap getMap() {
        return map;
    }

    public Class<? extends EventListener> getEventListener() {
        return CreateProcedureListener.class;
    }
}
