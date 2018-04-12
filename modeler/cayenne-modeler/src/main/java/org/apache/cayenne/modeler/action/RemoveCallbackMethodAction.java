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
package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import org.apache.cayenne.modeler.dialog.ConfirmRemoveDialog;
import org.apache.cayenne.modeler.services.CallbackMethodService;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;

import java.awt.event.ActionEvent;


/**
 * Action class for removing callback methods from ObjEntity
 *
 * @version 1.0 Oct 30, 2007
 */
public class RemoveCallbackMethodAction extends RemoveAction {

    @Inject
    public CallbackMethodService callbackMethodService;
    
    /**
     * unique action name
     */
    public final static String ACTION_NAME = "Remove Callback Method";
    
    /**
     * action name for multiple selection
     */
    private final static String ACTION_NAME_MULTIPLE = "Remove Callback Methods";

    /**
     * Constructor.
     */
    public RemoveCallbackMethodAction() {
        super(getActionName());
    }

    /**
     * @return icon file name for button
     */
    @Override
    public String getIconName() {
        return "icon-trash.png";
    }

    /**
     * @return unique action name
     */
    public static String getActionName() {
        return ACTION_NAME;
    }

    public String getActionName(boolean multiple) {
        return multiple ? ACTION_NAME_MULTIPLE : ACTION_NAME;
    }
    
    /**
     * performs callback method removing
     * @param e event
     */
    public void performAction(ActionEvent e, boolean allowAsking) {
        ConfirmRemoveDialog dialog = getConfirmDeleteDialog(allowAsking);

        RemoveServiceStatus status = callbackMethodService.isRemove();

        if(status != null && dialog.shouldDelete(status.getType(), status.getName())) {
            callbackMethodService.remove();
        }
    }
}

