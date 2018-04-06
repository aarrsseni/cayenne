package org.apache.cayenne.modeler.event;

import org.apache.cayenne.modeler.event.listener.ConsoleStopLoggingListener;

import java.util.EventListener;
import java.util.EventObject;

public class ConsoleStopLoggingEvent extends EventObject{

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ConsoleStopLoggingEvent(Object source) {
        super(source);
    }

    public Class<? extends EventListener> getEventListener() {
        return ConsoleStopLoggingListener.class;
    }
}
