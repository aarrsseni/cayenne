package org.apache.cayenne.modeler.event;

import org.apache.cayenne.dbsync.reverse.dbimport.ReverseEngineering;
import org.apache.cayenne.modeler.event.listener.ReverseEngineeringListener;

import java.util.EventListener;

public class ReverseEngineeringEvent extends DisplayEvent {

    protected ReverseEngineering reverseEngineering;

    public ReverseEngineeringEvent(Object source, ReverseEngineering reverseEngineering) {
        super(source);
        this.reverseEngineering = reverseEngineering;
    }

    public void setReverseEngineering(ReverseEngineering reverseEngineering) {
        this.reverseEngineering = reverseEngineering;
    }

    public ReverseEngineering getReverseEngineering() {
        return reverseEngineering;
    }

    public Class<? extends EventListener> getEventListener() {
        return ReverseEngineeringListener.class;
    }
}
