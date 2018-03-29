package org.apache.cayenne.modeler.event;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.map.event.MapEvent;

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.EventObject;

/**
 * @since 4.1
 */
class MultiMethodListenerDecriptor implements ListenerDescriptor {

    protected Class<? extends EventListener> listenerClass;
    protected Method[] methods;

    public MultiMethodListenerDecriptor(Class<? extends EventListener> listenerClass) {
        this.listenerClass = listenerClass;
        this.methods = checkMethodsOrder();
    }

    @Override
    public Class<? extends EventListener> getListenerClass() {
        return listenerClass;
    }

    @Override
    public void callEvent(Object listener, EventObject event) {
        try {
            methods[((MapEvent)event).getId() - 1].invoke(listener, event);
        } catch (Exception e) {
            throw new CayenneRuntimeException("Can't invoke method " + methods[((MapEvent)event).getId() - 1].getName() + " in " + listenerClass.getName());
        }
    }

    private Method[] checkMethodsOrder() {
        Method[] methods = new Method[3];
        for(Method method : listenerClass.getDeclaredMethods()) {
            if(method.getName().endsWith("Changed")) {
                methods[0] = method;
            } else if(method.getName().endsWith("Added")) {
                methods[1] = method;
            } else if(method.getName().endsWith("Removed")){
                methods[2] = method;
            } else {
                throw new IllegalArgumentException("Invalid method's name " + method.getName() + " in " + listenerClass.getName());
            }
        }
        return methods;
    }
}
