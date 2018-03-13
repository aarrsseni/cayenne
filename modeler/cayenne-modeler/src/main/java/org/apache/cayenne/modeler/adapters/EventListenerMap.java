package org.apache.cayenne.modeler.adapters;

import com.google.common.collect.Lists;

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
        List<Object> result = new ArrayList<>(listenerMap.get(key));
        Collections.reverse(result);
//        Lists.reverse(listenerMap.get(key)).toArray((T[]) Array.newInstance(key, listenerMap.get(key).size()));
        return result.toArray((T[]) Array.newInstance(key, result.size()));
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
        listenerMap.get(key).remove(val);
    }

}




