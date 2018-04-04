package org.apache.cayenne.modeler.services;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;

public interface PasteService {
    void paste(Object where, Object content);

    void paste(Object where, Object content, DataChannelDescriptor dataChannelDescriptor, DataMap map);

    boolean isTreeLeaf(Object content);
}
