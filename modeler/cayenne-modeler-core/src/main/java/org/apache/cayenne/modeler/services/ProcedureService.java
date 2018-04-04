package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.modeler.ProjectController;

public interface ProcedureService {
    void createProcedure();

    void createProcedure(DataMap map, Procedure procedure);

    void removeProcedure(DataMap map, Procedure procedure);

    void removeProcedureParameters(Procedure procedure, ProcedureParameter[] parameters);

    void fireProcedureEvent(Object src, ProjectController mediator, DataMap dataMap, Procedure procedure);
}
