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
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.util.CayenneAction;

import java.awt.event.ActionEvent;


public class GenerateCodeAction extends CayenneAction {

    @Inject
    private ProjectController projectController;

    public static String getActionName() {
        return "Generate Classes";
    }

    public GenerateCodeAction() {
        super(getActionName());
    }

    public String getIconName() {
        return "icon-gen_java.png";
    }

    public void performAction(ActionEvent e) {
        projectController.fireEvent(new DomainDisplayEvent(this, (DataChannelDescriptor) projectController.getProject().getRootNode()));
    }
}
