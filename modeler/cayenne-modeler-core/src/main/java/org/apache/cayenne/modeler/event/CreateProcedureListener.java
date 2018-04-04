package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateProcedureListener extends EventListener{
    void createProcedure(CreateProcedureEvent e);
}
