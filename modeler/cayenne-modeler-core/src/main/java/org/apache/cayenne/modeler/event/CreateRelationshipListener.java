package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface CreateRelationshipListener extends EventListener{
    void createRelationship(CreateRelationshipEvent e);
}
