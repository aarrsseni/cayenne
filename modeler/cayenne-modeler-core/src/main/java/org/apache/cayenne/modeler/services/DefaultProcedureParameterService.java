package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.ProcedureParameterEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateProcedureParameterEvent;
import org.apache.cayenne.modeler.event.ProcedureParameterDisplayEvent;

public class DefaultProcedureParameterService implements ProcedureParameterService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createProcedureParameter() {
        if (projectController.getCurrentState().getProcedure() != null) {
            Procedure procedure = projectController.getCurrentState().getProcedure();
            ProcedureParameter parameter = new ProcedureParameter();
            parameter.setName(NameBuilder.builder(parameter, procedure).name());

            createProcedureParameter(procedure, parameter);

            projectController.fireEvent(new CreateProcedureParameterEvent(this, projectController, procedure, parameter));
        }
    }

    public void createProcedureParameter(Procedure procedure, ProcedureParameter parameter) {
        procedure.addCallParameter(parameter);
        fireProcedureParameterEvent(this, projectController, procedure, parameter);
    }

    public void fireProcedureParameterEvent(Object src, ProjectController mediator, Procedure procedure,
                                     ProcedureParameter parameter) {
        mediator.fireEvent(new ProcedureParameterEvent(src, parameter, MapEvent.ADD));

        mediator.fireEvent(new ProcedureParameterDisplayEvent(src, parameter, procedure,
                mediator.getCurrentState().getDataMap(), (DataChannelDescriptor) mediator.getProject().getRootNode()));
    }

    public void removeProcedureParameters() {
        ProcedureParameter[] parameters = projectController.getCurrentState().getProcedureParameters();
        removeProcedureParameters(projectController.getCurrentState().getProcedure(), parameters);
    }

    public void removeProcedureParameters(
            Procedure procedure,
            ProcedureParameter[] parameters) {

        for (ProcedureParameter parameter : parameters) {

            procedure.removeCallParameter(parameter.getName());

            ProcedureParameterEvent e = new ProcedureParameterEvent(this, parameter, MapEvent.REMOVE);

            projectController.fireEvent(e);
        }
    }

}
