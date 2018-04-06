package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateDbEntityEvent;

import java.util.EventListener;

public interface CreateDbEntityListener extends EventListener {
    void createDbEntity(CreateDbEntityEvent e);
}
