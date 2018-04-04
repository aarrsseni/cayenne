package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateDataMapEvent;
import org.apache.cayenne.modeler.event.CreateDataMapListener;
import org.apache.cayenne.modeler.undo.CreateDataMapUndoableEdit;

public class CreateDataMapActionListener implements CreateDataMapListener{

    @Override
    public void createDataMap(CreateDataMapEvent e) {
        Application.getInstance().getUndoManager().addEdit(new CreateDataMapUndoableEdit(e.getDataChannelDescriptor(), e.getDataMap()));
    }

}
