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
package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.CallbackMap;
import org.apache.cayenne.map.LifecycleEvent;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.editor.CallbackType;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.event.CallbackMethodEvent;
import org.apache.cayenne.modeler.event.CreateCallbackMethodEvent;
import org.apache.cayenne.modeler.event.RemoveCallbackMethodEvent;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;
import org.apache.cayenne.util.Util;

/**
 * @since 4.1
 */
public class DefaultCallbackMethodService implements CallbackMethodService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createCallbackMethod() {
        CallbackType callbackType = projectController.getCurrentState().getCallbackType();

        String methodName = NameBuilder
                .builderForCallbackMethod(projectController.getCurrentState().getObjEntity())
                .baseName(toMethodName(callbackType.getType()))
                .name();

        createCallbackMethod(callbackType, methodName);

        projectController.fireEvent(new CreateCallbackMethodEvent(this, callbackType, methodName));
    }

    public void createCallbackMethod(
            CallbackType callbackType,
            String methodName) {
        getCallbackMap().getCallbackDescriptor(callbackType.getType()).addCallbackMethod(methodName);

        CallbackMethodEvent ce = new CallbackMethodEvent(
                this,
                null,
                methodName,
                MapEvent.ADD);

        projectController.fireEvent(ce);
    }

    public void removeCallbackMethod(CallbackType callbackType, String method) {
        getCallbackMap().getCallbackDescriptor(callbackType.getType()).removeCallbackMethod(method);

        CallbackMethodEvent e = new CallbackMethodEvent(
                this,
                null,
                method,
                MapEvent.REMOVE);

        projectController.fireEvent(e);
    }

    @Override
    public RemoveServiceStatus isRemove() {
        ObjCallbackMethod[] methods = projectController.getCurrentState().getCallbackMethods();
        if (methods.length > 0) {
            if(methods.length == 1){
                return new RemoveServiceStatus("callback method", methods[0].getName());
            } else {
                return new RemoveServiceStatus(null, "selected callback methods");
            }
        }
        return null;
    }

    @Override
    public void remove() {
        ObjCallbackMethod[] methods = projectController.getCurrentState().getCallbackMethods();
        if (methods.length > 0) {
            CallbackType callbackType = projectController.getCurrentState().getCallbackType();

            for (ObjCallbackMethod callbackMethod : methods) {
                removeCallbackMethod(callbackType, callbackMethod.getName());
            }
            
            projectController.fireEvent(new RemoveCallbackMethodEvent(this, callbackType, methods));
        }
    }

    private String toMethodName(LifecycleEvent event) {
        return "on" + Util.underscoredToJava(event.name(), true);
    }

    /**
     * @return CallbackMap instance where to create a method
     */
    public CallbackMap getCallbackMap() {
        return projectController.getCurrentState().getObjEntity().getCallbackMap();
    }
}
