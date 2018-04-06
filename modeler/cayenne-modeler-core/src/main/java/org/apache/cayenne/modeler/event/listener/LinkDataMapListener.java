package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.LinkDataMapEvent;

import java.util.EventListener;

public interface LinkDataMapListener extends EventListener{
    void linkDataMap(LinkDataMapEvent e);
}
