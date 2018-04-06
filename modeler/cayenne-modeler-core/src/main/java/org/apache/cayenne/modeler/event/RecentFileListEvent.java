package org.apache.cayenne.modeler.event;

import org.apache.cayenne.modeler.event.listener.RecentFileListListener;

import java.util.EventListener;
import java.util.EventObject;

public class RecentFileListEvent extends EventObject{

    public RecentFileListEvent(Object source) {
        super(source);
    }

    public Class<? extends EventListener> getEventListener() {
        return RecentFileListListener.class;
    }
}
