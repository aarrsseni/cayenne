package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateDbEntityListener extends EventListener {
    void createDbEntity(CreateDbEntityEvent e);
}
