package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateDbEntityEvent;
import org.apache.cayenne.modeler.event.CreateDbEntityListener;
import org.apache.cayenne.modeler.undo.CreateDbEntityUndoableEdit;

public class CreateDbEntityActionListener implements CreateDbEntityListener {

    @Override
    public void createDbEntity(CreateDbEntityEvent e) {
        Application.getInstance().getUndoManager().addEdit(new CreateDbEntityUndoableEdit(e.getDataMap(), e.getDbEntity()));
    }

}
