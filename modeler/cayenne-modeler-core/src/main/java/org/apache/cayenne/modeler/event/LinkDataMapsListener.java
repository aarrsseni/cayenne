package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface LinkDataMapsListener extends EventListener{
    void linkDataMaps(LinkDataMapsEvent e);
}
