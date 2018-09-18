/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler;

import java.lang.reflect.Array;
import java.util.Deque;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 4.1
 * A class that holds all listeners.
 */
public class EventListenerMap {
    private Map<Object, Deque<Object>> listenerMap;

    EventListenerMap(){
        this.listenerMap = new ConcurrentHashMap<>();
    }

    /**
     * Return listeners of predetermined type.
     *
     * @since 4.1
     */
    @SuppressWarnings("unchecked")
    <T extends EventListener> T[] getListeners(Class<T> key){
        //Order of listeners is important!
        if(listenerMap.containsKey(key)) {
            Deque<Object> result = new LinkedList<>(listenerMap.get(key));

            for(Object o : result){
                if(o.getClass() == ControllerState.class){
                    result.remove(o);
                    result.addFirst(o);
                    break;
                }
            }

            return result.toArray((T[]) Array.newInstance(key, result.size()));
        }
        return (T[])Array.newInstance(key, 0);
    }

    /**
     * @since 4.1
     */
    public synchronized <T extends EventListener> void add(Class<T> keyClass, T val){
        Objects.requireNonNull(listenerMap.compute(keyClass, (key, list) -> listenerMap.containsKey(key) ? list : new LinkedList<>())).addFirst(val);
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




