package org.apache.cayenne.modeler.event;

import org.apache.cayenne.modeler.event.listener.DbLoaderExceptionListener;

import java.util.EventListener;
import java.util.EventObject;

public class DbLoaderExceptionEvent extends EventObject {

    private Throwable th;
    private String msg;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @param th
     * @param msg
     * @throws IllegalArgumentException if source is null.
     */
    public DbLoaderExceptionEvent(Object source, Throwable th, String msg) {
        super(source);
        this.th = th;
        this.msg = msg;
    }

    public Throwable getTh() {
        return th;
    }

    public String getMsg() {
        return msg;
    }

    public Class<? extends EventListener> getEventLIstener() {
        return DbLoaderExceptionListener.class;
    }
}
