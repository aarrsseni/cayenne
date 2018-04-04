package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateNodeListener extends EventListener {
    void createNode(CreateNodeEvent e);
}
