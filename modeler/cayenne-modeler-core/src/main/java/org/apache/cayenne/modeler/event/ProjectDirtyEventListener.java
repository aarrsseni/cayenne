package org.apache.cayenne.modeler.event;

import java.util.EventListener;

/**
 * @since 4.1
 */
public interface ProjectDirtyEventListener extends EventListener {

    void setProjectDirty(ProjectDirtyEvent e);

}
