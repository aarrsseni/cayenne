package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateEmbeddableEvent;
import org.apache.cayenne.modeler.event.CreateEmbeddableListener;
import org.apache.cayenne.modeler.undo.CreateEmbeddableUndoableEdit;

public class CreateEmbeddableActionListener implements CreateEmbeddableListener{

    @Override
    public void createEmbeddable(CreateEmbeddableEvent e) {
        Application.getInstance().getUndoManager().addEdit(new CreateEmbeddableUndoableEdit(e.getDataMap(), e.getEmbeddable()));
    }

}
