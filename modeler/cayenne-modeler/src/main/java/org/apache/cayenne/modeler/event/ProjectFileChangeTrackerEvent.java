//package org.apache.cayenne.modeler.event;
//
//import org.apache.cayenne.modeler.ProjectFileChangeTracker;
//
//import java.util.EventObject;
//
//// TODO Move to core when will be ready
///*
// * @since 4.1
// */
//public class ProjectFileChangeTrackerEvent extends EventObject {
//
//    protected ProjectFileChangeTracker projectFileChangeTracker;
//
//    /**
//     * Constructs a prototypical Event.
//     *
//     * @param source The object on which the Event initially occurred.
//     * @throws IllegalArgumentException if source is null.
//     */
//    public ProjectFileChangeTrackerEvent(Object source) {
//        super(source);
//    }
//
//    public ProjectFileChangeTrackerEvent(Object source, ProjectFileChangeTracker projectFileChangeTracker){
//        super(source);
//        this.projectFileChangeTracker = projectFileChangeTracker;
//    }
//
//    public ProjectFileChangeTracker getProjectFileChangeTracker() {
//        return projectFileChangeTracker;
//    }
//}
