package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.CayenneRuntimeException;

import java.util.EventListener;

/**
 * @since 4.1
 */
public class ListenerDescriptorCreator {

    public ListenerDescriptor create(Class<? extends EventListener> listenerClass) {
        if(listenerClass.getDeclaredMethods().length == 1) {
            return createSingleMethodDescriptor(listenerClass);
        } else if(listenerClass.getDeclaredMethods().length == 3){
            return createMultiMethodDescriptor(listenerClass);
        } else {
            throw new CayenneRuntimeException("Can't find descriptor for listener class with " + listenerClass.getDeclaredMethods().length + " declared methods.");
        }
    }

    private SingleMethodListenerDescriptor createSingleMethodDescriptor(Class<? extends EventListener> listenerClass) {
        return new SingleMethodListenerDescriptor(listenerClass);
    }

    private MultiMethodListenerDecriptor createMultiMethodDescriptor(Class<? extends EventListener> listenerClass) {
        return new MultiMethodListenerDecriptor(listenerClass);
    }
}
