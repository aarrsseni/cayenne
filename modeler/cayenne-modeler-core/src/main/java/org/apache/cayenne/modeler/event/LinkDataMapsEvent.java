package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.modeler.event.listener.LinkDataMapsListener;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;

public class LinkDataMapsEvent extends EventObject{

    private DataNodeDescriptor dataNodeDescriptor;
    private Collection<String> linkedDataMaps;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public LinkDataMapsEvent(Object source) {
        super(source);
    }

    public LinkDataMapsEvent(Object source, DataNodeDescriptor dataNodeDescriptor, Collection<String> linkedDataMaps) {
        this(source);
        this.dataNodeDescriptor = dataNodeDescriptor;
        this.linkedDataMaps = linkedDataMaps;
    }


    public DataNodeDescriptor getDataNodeDescriptor() {
        return dataNodeDescriptor;
    }

    public Collection<String> getLinkedDataMaps() {
        return linkedDataMaps;
    }

    public Class<? extends EventListener> getEventListener() {
        return LinkDataMapsListener.class;
    }
}
