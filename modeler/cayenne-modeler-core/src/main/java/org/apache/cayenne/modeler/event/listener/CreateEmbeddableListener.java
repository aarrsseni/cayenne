package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateEmbeddableEvent;

import java.util.EventListener;

public interface CreateEmbeddableListener extends EventListener{
    void createEmbeddable(CreateEmbeddableEvent e);
}
