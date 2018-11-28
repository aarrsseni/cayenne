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
import org.apache.cayenne.map.EmbeddableAttribute;

import java.awt.event.ActionEvent;

/**
 * Action for cutting attribute(s)
 */
public class CutAttributeAction extends CutAction implements MultipleObjectsAction {

    @Inject
    public CopyAttributeAction copyAttributeAction;

    @Inject
    public RemoveAttributeAction removeAttributeAction;

    private final static String ACTION_NAME = "Cut Attribute";

    /**
     * Name of action if multiple attrs are selected
     */
    private final static String ACTION_NAME_MULTIPLE = "Cut Attributes";

    public static String getActionName() {
        return ACTION_NAME;
    }

    public String getActionName(boolean multiple) {
        return multiple ? ACTION_NAME_MULTIPLE : ACTION_NAME;
    }

    public CutAttributeAction() {
        super(ACTION_NAME);
    }

    /**
     * Returns <code>true</code> if last object in the path contains a removable
     * attribute.
     */
    @Override
    public boolean enableForPath(ConfigurationNode object) {
        if (object == null) {
            return false;
        }
        boolean isEnable = object instanceof Attribute;
        if (!isEnable) {
            isEnable = object instanceof EmbeddableAttribute;
        }

        return isEnable;
    }

    /**
     * Performs cutting of items
     */
    @Override
    public void performAction(ActionEvent e) {
        copyAttributeAction.performAction(e);
        removeAttributeAction.performAction(e, false);
    }
}
