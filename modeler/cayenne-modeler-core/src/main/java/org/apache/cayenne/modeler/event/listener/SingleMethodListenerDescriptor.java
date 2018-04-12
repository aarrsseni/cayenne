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

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.EventObject;

/**
 * @since 4.1
 */
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
