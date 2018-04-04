package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.modeler.ProjectController;

public interface ProcedureParameterService {
    void createProcedureParameter();

    void createProcedureParameter(Procedure procedure, ProcedureParameter parameter);

    /**
     * Fires events when an proc parameter was added
     */
    void fireProcedureParameterEvent(Object src, ProjectController mediator, Procedure procedure, ProcedureParameter parameter);

    void removeProcedureParameters();

    void removeProcedureParameters(Procedure procedure, ProcedureParameter[] parameters);
}
