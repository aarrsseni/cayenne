package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.AttributeService;

public class CreateAttributeAction extends AbstractCayenneAction{

    @Inject
    private ProjectController projectController;

    @Inject
    private AttributeService createAttributeService;

    @Override
    public void handle(Event event) {
        if (projectController.getCurrentState().getEmbeddable() != null) {
            createAttributeService.createEmbAttribute();
        }

        if (projectController.getCurrentState().getObjEntity() != null) {
            createAttributeService.createObjAttribute();
        } else if (projectController.getCurrentState().getDbEntity() != null) {
            createAttributeService.createDbAttribute();
        }
    }
}
