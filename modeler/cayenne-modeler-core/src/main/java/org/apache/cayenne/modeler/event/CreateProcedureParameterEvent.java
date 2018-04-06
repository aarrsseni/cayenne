package org.apache.cayenne.modeler.event;

import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.listener.CreateProcedureParameterListener;

import java.util.EventListener;
import java.util.EventObject;

public class CreateProcedureParameterEvent extends EventObject {

    private Procedure procedure;
    private ProcedureParameter procedureParameter;
    private ProjectController projectController;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateProcedureParameterEvent(Object source) {
        super(source);
    }

    public CreateProcedureParameterEvent(Object source, ProjectController projectController, Procedure procedure, ProcedureParameter procedureParameter) {
        this(source);
        this.projectController = projectController;
        this.procedure = procedure;
        this.procedureParameter = procedureParameter;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public ProcedureParameter getProcedureParameter() {
        return procedureParameter;
    }

    public ProjectController getProjectController() {
        return projectController;
    }

    public Class<? extends EventListener> getEventListener() {
        return CreateProcedureParameterListener.class;
    }
}
