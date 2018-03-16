package org.apache.cayenne.modeler.event;

import java.util.EventListener;

/**
 * @since 4.1
 */
public interface SaveListener extends EventListener {

    /**
     * @since 4.1
     */
    void saveFlagChange(SaveFlagEvent e);

}
