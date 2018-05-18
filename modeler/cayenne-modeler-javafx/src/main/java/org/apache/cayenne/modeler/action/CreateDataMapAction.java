package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.services.DataMapService;
import org.apache.cayenne.modeler.util.AbstractCayenneAction;

public class CreateDataMapAction extends AbstractCayenneAction {

    @Inject
    private DataMapService dataMapService;

    @Override
    public void handle(Event event) {
        dataMapService.createDataMap();
    }
}
