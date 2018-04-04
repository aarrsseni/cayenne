package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.QueryEvent;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.QueryDisplayEvent;

public class DefaultQueryService implements QueryService {

    @Inject
    public ProjectController projectController;

    public void createQuery(DataChannelDescriptor domain, DataMap dataMap, QueryDescriptor query) {
        dataMap.addQueryDescriptor(query);
        // notify listeners
        fireQueryEvent(this, projectController, domain, dataMap, query);
    }

    /**
     * Removes current Query from its DataMap and fires "remove" QueryEvent.
     */
    public void removeQuery(DataMap map, QueryDescriptor query) {

        QueryEvent e = new QueryEvent(this, query, MapEvent.REMOVE, map);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        map.removeQueryDescriptor(query.getName());
        projectController.fireEvent(e);
    }

    /**
     * Fires events when a query was added
     */
    public void fireQueryEvent(Object src, ProjectController mediator, DataChannelDescriptor domain,
                                      DataMap dataMap, QueryDescriptor query) {
        mediator.fireEvent(new QueryEvent(src, query, MapEvent.ADD, dataMap));
        mediator.fireEvent(new QueryDisplayEvent(src, query, dataMap, domain));
    }
}
