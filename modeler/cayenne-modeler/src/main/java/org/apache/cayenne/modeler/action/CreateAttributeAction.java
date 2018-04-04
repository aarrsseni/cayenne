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
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.AttributeService;
import org.apache.cayenne.modeler.util.CayenneAction;

import java.awt.event.ActionEvent;

/**
 */
public class CreateAttributeAction extends CayenneAction {

    @Inject
    public AttributeService createAttributeService;

    @Inject
    public ProjectController projectController;

    /**
     * Constructor for CreateAttributeAction.
     */
    public CreateAttributeAction() {
        super(getActionName());
    }

    public static String getActionName() {
        return "Create Attribute";
    }

    @Override
    public String getIconName() {
        return "icon-attribute.png";
    }

    /**
     * Creates ObjAttribute, DbAttribute depending on context.
     */
    @Override
    public void performAction(ActionEvent e) {
        if (projectController.getCurrentState().getEmbeddable() != null) {
            createAttributeService.createEmbAttribute();
        }

        if (projectController.getCurrentState().getObjEntity() != null) {
            createAttributeService.createObjAttribute();
        } else if (projectController.getCurrentState().getDbEntity() != null) {
            createAttributeService.createDbAttribute();
        }
    }

    /**
     * Returns <code>true</code> if path contains an Entity object.
     */
    @Override
    public boolean enableForPath(ConfigurationNode object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Attribute) {
            return ((Attribute) object).getParent() != null && ((Attribute) object).getParent() instanceof Entity;
        }

        return false;
    }
}
