package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateObjEntityEvent;

import java.util.EventListener;

public interface CreateObjEntityListener extends EventListener{
    void createObjEntity(CreateObjEntityEvent e);
}
