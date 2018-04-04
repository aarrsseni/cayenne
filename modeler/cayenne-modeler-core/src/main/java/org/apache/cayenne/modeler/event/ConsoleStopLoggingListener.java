package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface ConsoleStopLoggingListener extends EventListener{
    void stopLogging(ConsoleStopLoggingEvent e);
}
