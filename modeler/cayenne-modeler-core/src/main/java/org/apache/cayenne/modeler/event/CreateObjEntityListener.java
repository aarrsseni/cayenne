package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateObjEntityListener extends EventListener{
    void createObjEntity(CreateObjEntityEvent e);
}
