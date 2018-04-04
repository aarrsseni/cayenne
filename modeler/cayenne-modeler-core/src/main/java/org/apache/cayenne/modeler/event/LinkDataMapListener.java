package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface LinkDataMapListener extends EventListener{
    void linkDataMap(LinkDataMapEvent e);
}
