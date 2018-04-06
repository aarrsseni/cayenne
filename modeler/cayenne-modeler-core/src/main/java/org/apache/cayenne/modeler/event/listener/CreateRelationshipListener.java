package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.CreateRelationshipEvent;

import java.util.EventListener;

public interface CreateRelationshipListener extends EventListener{
    void createRelationship(CreateRelationshipEvent e);
}
