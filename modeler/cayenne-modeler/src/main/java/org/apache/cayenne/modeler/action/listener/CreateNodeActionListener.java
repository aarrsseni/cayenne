package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateNodeEvent;
import org.apache.cayenne.modeler.event.listener.CreateNodeListener;
import org.apache.cayenne.modeler.undo.CreateNodeUndoableEdit;

public class CreateNodeActionListener implements CreateNodeListener{

    @Override
    public void createNode(CreateNodeEvent e) {
        Application.getInstance().getUndoManager().addEdit(new CreateNodeUndoableEdit(Application.getInstance(), e.getDataNodeDescriptor()));
    }
}
