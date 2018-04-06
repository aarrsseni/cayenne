package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateCallbackMethodEvent;

import java.util.EventListener;

public interface CreateCallbackMethodListener extends EventListener {
    void createCallbackMethod(CreateCallbackMethodEvent e);
}
