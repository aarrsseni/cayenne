package org.apache.cayenne.modeler.event;

import java.util.EventListener;

/**
 * @since 4.1
 */
public interface ProjectDirtyEventListener extends EventListener {

    /**
     * @since 4.1
     */
    void setProjectDirty(ProjectDirtyEvent e);

}
