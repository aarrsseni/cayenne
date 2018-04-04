package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateProcedureParameterEvent;
import org.apache.cayenne.modeler.event.CreateProcedureParameterListener;
import org.apache.cayenne.modeler.undo.CreateProcedureParameterUndoableEdit;

public class CreateProcedureParameterActionListener implements CreateProcedureParameterListener{
    @Override
    public void createProcedureParameter(CreateProcedureParameterEvent e) {
        Application.getInstance().getUndoManager().addEdit(
                    new CreateProcedureParameterUndoableEdit(
                            (DataChannelDescriptor) e.getProjectController().getProject().getRootNode(), e.getProjectController().getCurrentState().getDataMap(),
                            e.getProcedure(), e.getProcedureParameter()
                    )
        );
    }
}
