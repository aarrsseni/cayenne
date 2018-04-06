package org.apache.cayenne.modeler.event.listener;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @since 4.1
 */
public interface ListenerDescriptor {

    Class<? extends EventListener> getListenerClass();

    void callEvent(Object listener, EventObject event);
}
