package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateDataMapListener extends EventListener{

    void createDataMap(CreateDataMapEvent e);

}
