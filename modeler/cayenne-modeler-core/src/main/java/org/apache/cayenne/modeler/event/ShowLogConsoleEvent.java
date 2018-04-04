package org.apache.cayenne.modeler.event;

import java.util.EventListener;
import java.util.EventObject;

public class ShowLogConsoleEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ShowLogConsoleEvent(Object source) {
        super(source);
    }

    public Class<? extends EventListener> getEventListener() {
        return ShowLogConsoleListener.class;
    }
}
