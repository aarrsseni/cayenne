package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateDataMapEvent;

import java.util.EventListener;

public interface CreateDataMapListener extends EventListener{

    void createDataMap(CreateDataMapEvent e);

}
