package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateRelationshipEvent;
import org.apache.cayenne.modeler.event.listener.CreateRelationshipListener;
import org.apache.cayenne.modeler.undo.CreateRelationshipUndoableEdit;

public class CreateRelationshipActionListener implements CreateRelationshipListener{

    @Override
    public void createRelationship(CreateRelationshipEvent e) {
        if(e.getObjEntity() != null) {
            Application.getInstance().getUndoManager().addEdit(
                    new CreateRelationshipUndoableEdit(e.getObjEntity(), new ObjRelationship[]{e.getObjRelationship()}));
        } else if(e.getDbEntity() != null) {
            Application.getInstance().getUndoManager().addEdit(
                    new CreateRelationshipUndoableEdit(e.getDbEntity(), new DbRelationship[]{e.getDbRelationship()}));
        }
    }

}
