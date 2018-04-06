package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.GenerateDbEvent;

import java.util.EventListener;

public interface GenerateDbListener extends EventListener {
    void generateDb(GenerateDbEvent e);
}
