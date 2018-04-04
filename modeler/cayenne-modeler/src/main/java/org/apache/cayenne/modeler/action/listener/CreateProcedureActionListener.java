package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateProcedureEvent;
import org.apache.cayenne.modeler.event.CreateProcedureListener;
import org.apache.cayenne.modeler.undo.CreateProcedureUndoableEdit;

public class CreateProcedureActionListener implements CreateProcedureListener{
    @Override
    public void createProcedure(CreateProcedureEvent e) {
        Application.getInstance().getUndoManager().addEdit(new CreateProcedureUndoableEdit(e.getMap(), e.getProcedure()));
    }
}
