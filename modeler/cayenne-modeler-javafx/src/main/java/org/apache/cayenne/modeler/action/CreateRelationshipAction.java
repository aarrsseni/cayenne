package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.services.RelationshipService;

public class CreateRelationshipAction extends AbstractCayenneAction{

    @Inject
    private RelationshipService relationshipService;

    @Override
    public void handle(Event event) {
        relationshipService.createRelationship();
    }
}
