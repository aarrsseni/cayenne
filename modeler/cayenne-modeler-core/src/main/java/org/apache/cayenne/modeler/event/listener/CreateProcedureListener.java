package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateProcedureEvent;

import java.util.EventListener;

public interface CreateProcedureListener extends EventListener{
    void createProcedure(CreateProcedureEvent e);
}
