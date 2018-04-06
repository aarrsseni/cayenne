package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateAttributeEvent;

import java.util.EventListener;

public interface CreateAttributeListener extends EventListener{
    void addAttr(CreateAttributeEvent e);
}
