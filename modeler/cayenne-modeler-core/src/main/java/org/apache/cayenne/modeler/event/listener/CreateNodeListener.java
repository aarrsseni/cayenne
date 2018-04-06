package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateNodeEvent;

import java.util.EventListener;

public interface CreateNodeListener extends EventListener {
    void createNode(CreateNodeEvent e);
}
