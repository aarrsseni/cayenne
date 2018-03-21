package org.apache.cayenne.modeler.event;

import java.util.EventListener;

/*
 * @since 4.1
 */
public interface ProjectFileChangeTrackerListener extends EventListener{

    void onChange(ProjectFileChangeTrackerEvent e);

    void onRemove(ProjectFileChangeTrackerEvent e);

}
