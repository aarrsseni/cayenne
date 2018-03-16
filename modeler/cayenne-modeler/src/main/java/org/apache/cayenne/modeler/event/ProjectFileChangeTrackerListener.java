package org.apache.cayenne.modeler.event;

import java.util.EventListener;

// TODO Move to core when will be ready
/*
 * @since 4.1
 */
public interface ProjectFileChangeTrackerListener extends EventListener{

    void doOnChange(ProjectFileChangeTrackerEvent e);

    void doOnRemove(ProjectFileChangeTrackerEvent e);

}
