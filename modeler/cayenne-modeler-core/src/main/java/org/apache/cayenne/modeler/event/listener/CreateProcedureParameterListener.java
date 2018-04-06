package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateProcedureParameterEvent;

import java.util.EventListener;

public interface CreateProcedureParameterListener extends EventListener{
    void createProcedureParameter(CreateProcedureParameterEvent e);
}
