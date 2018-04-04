package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateProcedureParameterListener extends EventListener{
    void createProcedureParameter(CreateProcedureParameterEvent e);
}
