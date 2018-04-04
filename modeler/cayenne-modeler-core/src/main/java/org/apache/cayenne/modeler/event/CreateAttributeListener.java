package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateAttributeListener extends EventListener{
    void addAttr(CreateAttributeEvent e);
}
