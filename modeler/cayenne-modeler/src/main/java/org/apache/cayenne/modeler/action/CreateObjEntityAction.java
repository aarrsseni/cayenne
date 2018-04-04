/*
 *    Licensed to the Apache Software Foundation (ASF) under one
 *    or more contributor license agreements.  See the NOTICE file
 *    distributed with this work for additional information
 *    regarding copyright ownership.  The ASF licenses this file
 *    to you under the Apache License, Version 2.0 (the
 *    "License"); you may not use this file except in compliance
 *    with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */
package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.services.ObjEntityService;
import org.apache.cayenne.modeler.util.CayenneAction;

import java.awt.event.ActionEvent;

public class CreateObjEntityAction extends CayenneAction {

    @Inject
    public ObjEntityService objEntityService;

    /**
     * Constructor for CreateObjEntityAction.
     */
    public CreateObjEntityAction() {
        super(getActionName());
    }

    public static String getActionName() {
        return "Create ObjEntity";
    }

    @Override
    public String getIconName() {
        return "icon-new_objentity.png";
    }

    @Override
    public void performAction(ActionEvent e) {
        objEntityService.createObjEntity();
    }

    /**
     * Returns <code>true</code> if path contains a DataMap object.
     */
    @Override
    public boolean enableForPath(ConfigurationNode object) {
        if (object == null) {
            return false;
        }

        if (object instanceof ObjEntity) {
            return ((ObjEntity) object).getParent() != null
                    && ((ObjEntity) object).getParent() instanceof DataMap;
        }

        return false;
    }
}
