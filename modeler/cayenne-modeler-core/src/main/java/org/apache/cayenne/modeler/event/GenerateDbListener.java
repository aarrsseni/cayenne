package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface GenerateDbListener extends EventListener {
    void generateDb(GenerateDbEvent e);
}
