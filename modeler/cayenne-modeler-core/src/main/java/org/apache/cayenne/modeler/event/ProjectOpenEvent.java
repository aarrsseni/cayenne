package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.event.DataMapListener;
import org.apache.cayenne.project.Project;

import java.util.EventListener;
import java.util.EventObject;

public class ProjectOpenEvent extends EventObject{

    protected Project project;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ProjectOpenEvent(Object source) {
        super(source);
    }

    public ProjectOpenEvent(Object source, Project project) {
        this(source);
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public Class<? extends EventListener> getEventListener() {
        return ProjectOpenListener.class;
    }
}
