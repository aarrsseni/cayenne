package org.apache.cayenne.modeler.services;

import org.apache.cayenne.configuration.DataNodeDescriptor;

public interface NodeService {
    void createNode();

    void createDataNode(DataNodeDescriptor node);

    void removeDataNode(DataNodeDescriptor node);

    DataNodeDescriptor buildDataNode();
}
