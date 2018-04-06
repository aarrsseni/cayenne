package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.LinkDataMapsEvent;

import java.util.EventListener;

public interface LinkDataMapsListener extends EventListener{
    void linkDataMaps(LinkDataMapsEvent e);
}
