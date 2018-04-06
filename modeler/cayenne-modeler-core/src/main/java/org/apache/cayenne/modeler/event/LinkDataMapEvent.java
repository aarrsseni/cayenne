package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.listener.LinkDataMapListener;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;

public class LinkDataMapEvent extends EventObject{

    private DataMap dataMap;
    private DataNodeDescriptor node;
    private Collection<DataNodeDescriptor> unlinkedNodes;
    private ProjectController projectController;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public LinkDataMapEvent(Object source) {
        super(source);
    }

    public LinkDataMapEvent(Object source, DataMap dataMap, DataNodeDescriptor node, Collection<DataNodeDescriptor> unlinkedNodes, ProjectController projectController) {
        this(source);
        this.dataMap = dataMap;
        this.node = node;
        this.unlinkedNodes = unlinkedNodes;
        this.projectController = projectController;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public DataNodeDescriptor getNode() {
        return node;
    }

    public Collection<DataNodeDescriptor> getUnlinkedNodes() {
        return unlinkedNodes;
    }

    public ProjectController getProjectController() {
        return projectController;
    }

    public Class<? extends EventListener> getEventListener() {
        return LinkDataMapListener.class;
    }
}
