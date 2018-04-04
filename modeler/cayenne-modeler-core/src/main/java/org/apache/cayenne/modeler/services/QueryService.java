package org.apache.cayenne.modeler.services;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.modeler.ProjectController;

public interface QueryService {
    void createQuery(DataChannelDescriptor domain, DataMap dataMap, QueryDescriptor query);

    void removeQuery(DataMap map, QueryDescriptor query);

    void fireQueryEvent(Object src, ProjectController mediator, DataChannelDescriptor domain, DataMap dataMap, QueryDescriptor query);
}
