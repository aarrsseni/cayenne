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
