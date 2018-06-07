package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.services.DbEntityService;

public class CreateDbEntityAction extends AbstractCayenneAction{

    @Inject
    private DbEntityService dbEntityService;

    @Override
    public void handle(Event event) {
        dbEntityService.createDbEntity();
    }
}
