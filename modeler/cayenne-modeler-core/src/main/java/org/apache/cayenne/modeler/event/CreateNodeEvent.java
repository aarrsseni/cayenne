package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataNodeDescriptor;

import java.util.EventListener;
import java.util.EventObject;

public class CreateNodeEvent extends EventObject{

    private DataNodeDescriptor dataNodeDescriptor;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateNodeEvent(Object source) {
        super(source);
    }

    public CreateNodeEvent(Object source, DataNodeDescriptor dataNodeDescriptor) {
        this(source);
        this.dataNodeDescriptor = dataNodeDescriptor;
    }

    public DataNodeDescriptor getDataNodeDescriptor() {
        return dataNodeDescriptor;
    }

    public Class<? extends EventListener> getEventListener() {
        return CreateNodeListener.class;
    }
}
