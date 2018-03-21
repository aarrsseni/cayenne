package org.apache.cayenne.modeler.event;

import java.util.EventListener;

/**
 * @since 4.1
 */
public interface ActionManagerChangesListener extends EventListener {

    /**
     * @since 4.1
     */
    void projectOpenedChanges();

    /**
     * @since 4.1
     */
    void domainSelectedChanges();

}
