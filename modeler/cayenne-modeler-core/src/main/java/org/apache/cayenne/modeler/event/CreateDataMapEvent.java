package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.DataMapListener;
import org.apache.cayenne.map.DataMap;

import java.util.EventListener;
import java.util.EventObject;

public class CreateDataMapEvent extends EventObject{

    private DataChannelDescriptor dataChannelDescriptor;

    private DataMap dataMap;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateDataMapEvent(Object source) {
        super(source);
    }

    public CreateDataMapEvent(Object source, DataChannelDescriptor dataChannelDescriptor, DataMap dataMap) {
        this(source);
        this.dataChannelDescriptor = dataChannelDescriptor;
        this.dataMap = dataMap;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public DataChannelDescriptor getDataChannelDescriptor() {
        return dataChannelDescriptor;
    }

    public Class<? extends EventListener> getEventListener() {
        return CreateDataMapListener.class;
    }
}
