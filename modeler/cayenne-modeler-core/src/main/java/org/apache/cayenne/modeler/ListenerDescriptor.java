package org.apache.cayenne.modeler;

import java.lang.reflect.InvocationTargetException;

public interface ListenerDescriptor {
    void callSingleMethodEvent(Object listener, Object event) throws InvocationTargetException, IllegalAccessException;
}
