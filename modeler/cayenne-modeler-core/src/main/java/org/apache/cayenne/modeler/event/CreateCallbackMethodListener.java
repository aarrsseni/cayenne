package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateCallbackMethodListener extends EventListener {
    void createCallbackMethod(CreateCallbackMethodEvent e);
}
