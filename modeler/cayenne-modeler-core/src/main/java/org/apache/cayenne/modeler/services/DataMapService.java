package org.apache.cayenne.modeler.services;

import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;

import java.io.File;

public interface DataMapService {

    void createDataMap();

    /** Calls addDataMap() or creates new data map if no data node selected. */
    void createDataMap(DataMap map);

    void importDataMap(File dataMapFile) throws Exception;

    void linkDataMaps();

    void linkDataMap(DataMap map, DataNodeDescriptor node);

    void removeDataMap(DataMap map);

    void removeDataMapFromDataNode(DataNodeDescriptor node, DataMap map);
}
