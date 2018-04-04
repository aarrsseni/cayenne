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
import org.apache.cayenne.modeler.services.CallbackMethodService;
import org.apache.cayenne.modeler.util.CayenneAction;

import java.awt.event.ActionEvent;

/**
 * Action class for creating callback methods on ObjEntity
 */
public class CreateCallbackMethodAction extends CayenneAction {

    @Inject
    public CallbackMethodService callbackMethodService;

    /**
     * unique action name
     */
    public static final String ACTION_NAME = "Create callback method";

    /**
     * Constructor.
     */
    public CreateCallbackMethodAction() {
        super(getActionName());
        setAlwaysOn(true);
    }


    public static String getActionName() {
        return ACTION_NAME;
    }

    /**
     * @return icon file name for button
     */
    public String getIconName() {
        return "icon-create-method.png";
    }

    /**
     * performs adding new callback method
     *
     * @param e event
     */
    public final void performAction(ActionEvent e) {
        callbackMethodService.createCallbackMethod();
    }
}

