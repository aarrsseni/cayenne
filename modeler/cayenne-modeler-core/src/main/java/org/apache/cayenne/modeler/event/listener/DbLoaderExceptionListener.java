package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.DbLoaderExceptionEvent;

import java.util.EventListener;

public interface DbLoaderExceptionListener extends EventListener {
    void processException(DbLoaderExceptionEvent e);
}
