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
package org.apache.cayenne.modeler.event.listener;

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
