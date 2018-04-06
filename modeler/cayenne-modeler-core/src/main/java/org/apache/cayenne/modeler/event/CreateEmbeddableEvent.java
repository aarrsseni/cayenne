package org.apache.cayenne.modeler.event;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.modeler.event.listener.CreateEmbeddableListener;

import java.util.EventListener;
import java.util.EventObject;

public class CreateEmbeddableEvent extends EventObject {

    private DataMap dataMap;
    private Embeddable embeddable;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateEmbeddableEvent(Object source) {
        super(source);
    }

    public CreateEmbeddableEvent(Object source, DataMap dataMap, Embeddable embeddable) {
        this(source);
        this.dataMap = dataMap;
        this.embeddable = embeddable;
    }

    public Embeddable getEmbeddable() {
        return embeddable;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public Class<? extends EventListener> getEventListener() {
        return CreateEmbeddableListener.class;
    }
}
