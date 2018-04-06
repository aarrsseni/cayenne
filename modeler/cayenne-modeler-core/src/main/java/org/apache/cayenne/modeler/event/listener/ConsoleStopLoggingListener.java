package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.ConsoleStopLoggingEvent;

import java.util.EventListener;

public interface ConsoleStopLoggingListener extends EventListener{
    void stopLogging(ConsoleStopLoggingEvent e);
}
