package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.services.DataMapService;

public class CreateDataMapAction extends AbstractCayenneAction {

    @Inject
    private DataMapService dataMapService;

    @Override
    public void handle(Event event) {
        dataMapService.createDataMap();
    }
}
