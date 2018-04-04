package org.apache.cayenne.modeler.event;

import org.apache.cayenne.map.DataMap;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;

public class GenerateCodeEvent extends EventObject{

    private Collection<DataMap> dataMaps;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public GenerateCodeEvent(Object source) {
        super(source);
    }

    public GenerateCodeEvent(Object source, Collection<DataMap> dataMaps) {
        this(source);
        this.dataMaps = dataMaps;
    }

    public Collection<DataMap> getDataMaps() {
        return dataMaps;
    }

    public Class<? extends EventListener> getEventListener() {
        return GenerateCodeListener.class;
    }
}
