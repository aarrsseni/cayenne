package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.services.ObjEntityService;

public class CreateObjEntityAction extends AbstractCayenneAction{

    @Inject
    private ObjEntityService objEntityService;

    @Override
    public void handle(Event event) {
        objEntityService.createObjEntity();
    }
}
