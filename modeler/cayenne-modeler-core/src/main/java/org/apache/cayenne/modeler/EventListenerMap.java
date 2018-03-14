package org.apache.cayenne.modeler;

import javax.swing.event.EventListenerList;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A class that holds all listeners.
 *
 * @since 4.1
 */
public class EventListenerMap {
    private Map<Object, List<Object>> listenerMap;

    public EventListenerMap(){
        this.listenerMap = new ConcurrentHashMap<>();
    }

    /**
     * Return listeners of predetermined type.
     *
     * @since 4.1
     */
    public <T extends EventListener> T[] getListeners(Class<T> key){
        //Order of listeners is important!
        if(listenerMap.containsKey(key)) {
            List<Object> result = new ArrayList<>(listenerMap.get(key));
            Collections.reverse(result);
            return result.toArray((T[]) Array.newInstance(key, result.size()));
        }
        return (T[])Array.newInstance(key, 0);
    }

    /**
     * @since 4.1
     */
    public synchronized <T extends EventListener> void add(Class<T> keyClass, T val){
        listenerMap.compute(keyClass, (key, list) -> listenerMap.containsKey(key) ? list : new ArrayList<>()).add(val);
    }

    /**
     * @since 4.1
     */
    public synchronized <T extends EventListener> void remove(Class<T> key, T val){
        if(listenerMap.containsKey(key)) {
            listenerMap.get(key).remove(val);
        }
    }

}




