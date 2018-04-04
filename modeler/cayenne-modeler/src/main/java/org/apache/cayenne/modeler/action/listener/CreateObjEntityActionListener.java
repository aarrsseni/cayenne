package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateObjEntityEvent;
import org.apache.cayenne.modeler.event.CreateObjEntityListener;
import org.apache.cayenne.modeler.undo.CreateObjEntityUndoableEdit;

public class CreateObjEntityActionListener implements CreateObjEntityListener{
    @Override
    public void createObjEntity(CreateObjEntityEvent e) {
        Application.getInstance().getUndoManager().addEdit(new CreateObjEntityUndoableEdit(e.getDataMap(), e.getObjEntity()));
    }
}
