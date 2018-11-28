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
package org.apache.cayenne.modeler.undo;

import org.apache.cayenne.modeler.editor.CallbackType;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.services.CallbackMethodService;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class RemoveCallbackMethodUndoableEdit extends CayenneUndoableEdit {

    private CallbackType callbackType;
    private ObjCallbackMethod[] methods;

    public RemoveCallbackMethodUndoableEdit(CallbackType callbackType,
    		ObjCallbackMethod[] methods) {
        this.callbackType = callbackType;
        this.methods = methods;
    }

    @Override
    public String getPresentationName() {
    	return "Remove Obj Callback Methods";
    }

    @Override
    public void redo() throws CannotRedoException {
    	CallbackMethodService callbackMethodService = controller.getBootiqueInjector().getInstance(CallbackMethodService.class);
        for (ObjCallbackMethod method : methods) {
            callbackMethodService.removeCallbackMethod(callbackType, method.getName());
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        CallbackMethodService callbackMethodService = controller.getBootiqueInjector().getInstance(CallbackMethodService.class);
        for (ObjCallbackMethod method : methods) {
            callbackMethodService.createCallbackMethod(callbackType, method.getName());
        }
    }
}
