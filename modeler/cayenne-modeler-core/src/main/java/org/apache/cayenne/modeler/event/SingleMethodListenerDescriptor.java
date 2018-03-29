package org.apache.cayenne.modeler.event;

import org.apache.cayenne.CayenneRuntimeException;

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.EventObject;

class SingleMethodListenerDescriptor implements ListenerDescriptor{

    protected Class<? extends EventListener> listenerClass;
    protected Method method;

    public SingleMethodListenerDescriptor(Class<? extends EventListener> listenerClass) {
        this.listenerClass = listenerClass;
        this.method = listenerClass.getDeclaredMethods()[0];
    }

    @Override
    public Class<? extends EventListener> getListenerClass() {
        return listenerClass;
    }

    @Override
    public void callEvent(Object listener, EventObject event) {
        try {
            method.invoke(listener, event);
        } catch (Exception e) {
            throw new CayenneRuntimeException("Can't invoke method " + method.getName() + " in " + listenerClass.getName());
        }
    }
}
