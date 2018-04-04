package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateEmbeddableListener extends EventListener{
    void createEmbeddable(CreateEmbeddableEvent e);
}
