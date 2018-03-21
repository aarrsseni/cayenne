package org.apache.cayenne.modeler.event;

import org.apache.cayenne.modeler.ProjectFileChangeTracker;

import java.util.EventObject;

/*
 * @since 4.1
 */
public class ProjectFileChangeTrackerEvent extends EventObject {

    protected ProjectFileChangeTracker projectFileChangeTracker;

    public ProjectFileChangeTrackerEvent(Object source) {
        super(source);
    }

    public ProjectFileChangeTrackerEvent(Object source, ProjectFileChangeTracker projectFileChangeTracker){
        super(source);
        this.projectFileChangeTracker = projectFileChangeTracker;
    }

    public ProjectFileChangeTracker getProjectFileChangeTracker() {
        return projectFileChangeTracker;
    }
}
