package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateAttributeEvent;
import org.apache.cayenne.modeler.event.CreateAttributeListener;
import org.apache.cayenne.modeler.undo.CreateAttributeUndoableEdit;
import org.apache.cayenne.modeler.undo.CreateEmbAttributeUndoableEdit;

public class CreateAttributeActionListener implements CreateAttributeListener {

    private static final String EMBEDDABLE_ATTR = "EmbeddableAttr";
    private static final String DB_ATTR = "DbAttr";
    private static final String OBJ_ATTR = "ObjAttr";

    @Override
    public void addAttr(CreateAttributeEvent e) {
        switch (e.getType()) {
            case EMBEDDABLE_ATTR:
                Application.getInstance().getUndoManager().addEdit(
                        new CreateEmbAttributeUndoableEdit(e.getEmbeddable(), new EmbeddableAttribute[]{e.getEmbeddableAttribute()}));
                break;
            case OBJ_ATTR:
                Application.getInstance().getUndoManager().addEdit(
                        new CreateAttributeUndoableEdit((DataChannelDescriptor) Application.getInstance().getProjectController().getProject().getRootNode(),
                                Application.getInstance().getProjectController().getCurrentState().getDataMap(), e.getObjEntity(), e.getObjAttribute()));
                break;
            case DB_ATTR:
                Application.getInstance().getUndoManager().addEdit(
                        new CreateAttributeUndoableEdit((DataChannelDescriptor) Application.getInstance().getProjectController().getProject().getRootNode(),
                                Application.getInstance().getProjectController().getCurrentState().getDataMap(), e.getDbEntity(), e.getDbAttribute()));
                break;
            default:
                throw new IllegalArgumentException("No attributes for " + e.getType());
        }

    }

}
