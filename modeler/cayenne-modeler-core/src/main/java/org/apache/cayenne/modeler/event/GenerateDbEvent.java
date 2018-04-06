package org.apache.cayenne.modeler.event;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.listener.GenerateDbListener;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;

public class GenerateDbEvent extends EventObject{

    private Collection<DataMap> dataMaps;
    private ProjectController projectController;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public GenerateDbEvent(Object source) {
        super(source);
    }

    public GenerateDbEvent(Object source, Collection<DataMap> dataMaps, ProjectController projectController) {
        this(source);
        this.dataMaps = dataMaps;
        this.projectController = projectController;
    }

    public Collection<DataMap> getDataMaps() {
        return dataMaps;
    }

    public Class<? extends EventListener> getEventListener() {
        return GenerateDbListener.class;
    }

    public ProjectController getProjectController() {
        return projectController;
    }
}
