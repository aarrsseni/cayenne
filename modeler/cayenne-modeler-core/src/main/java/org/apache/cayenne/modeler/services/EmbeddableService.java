package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.modeler.ProjectController;

public interface EmbeddableService {
    void createEmbeddable();

    void createEmbeddable(DataMap dataMap, Embeddable embeddable);

    void removeEmbeddable(DataMap map, Embeddable embeddable);

    void fireEmbeddableEvent(Object src, ProjectController mediator, DataMap dataMap, Embeddable embeddable);
}
