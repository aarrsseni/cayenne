package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.ProjectDirtyEvent;

import java.util.EventListener;

/**
 * @since 4.1
 */
public interface ProjectDirtyEventListener extends EventListener {

    void setProjectDirty(ProjectDirtyEvent e);

}
