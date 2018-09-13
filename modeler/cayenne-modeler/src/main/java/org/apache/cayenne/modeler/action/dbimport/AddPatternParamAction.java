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

package org.apache.cayenne.modeler.action.dbimport;

import com.google.inject.Inject;
import org.apache.cayenne.dbsync.reverse.dbimport.*;
import org.apache.cayenne.modeler.dialog.db.load.DbImportTreeNode;
import org.apache.cayenne.modeler.services.ReverseEngineeringService;

import java.awt.event.ActionEvent;

/**
 * @since 4.1
 */
public abstract class AddPatternParamAction extends TreeManipulationAction {

    @Inject
    ReverseEngineeringService reverseEngineeringService;

    private Class paramClass;

    AddPatternParamAction(String name) {
        super(name);
    }

    private void addPatternParamToContainer(Class paramClass, Object selectedObject, String name, DbImportTreeNode node) {
        node.add(new DbImportTreeNode(reverseEngineeringService.getPatternParamToContainer(paramClass, selectedObject, name)));
    }

    private void addPatternParamToIncludeTable(Class paramClass, Object selectedObject, String name, DbImportTreeNode node) {
        node.add(new DbImportTreeNode(reverseEngineeringService.getPatternParamToIncludeTable(paramClass, selectedObject, name)));
    }

    @Override
    public void performAction(ActionEvent e) {
        ReverseEngineering reverseEngineeringOldCopy = prepareElements();
        Object selectedObject;
        if (reverseEngineeringService.reverseEngineeringIsEmpty(tree.getReverseEngineering())) {
            tree.getRootNode().removeAllChildren();
        }
        if (canBeInserted(selectedElement)) {
            selectedObject = selectedElement.getUserObject();
            if (selectedObject instanceof FilterContainer) {
                addPatternParamToContainer(paramClass, selectedObject, name, selectedElement);
            } else if (selectedObject instanceof IncludeTable) {
                addPatternParamToIncludeTable(paramClass, selectedObject, name, selectedElement);
            }
            updateSelected = true;
        } else {
            if (parentElement == null) {
                parentElement = tree.getRootNode();
            }
            selectedObject = parentElement.getUserObject();
            if (selectedObject instanceof FilterContainer) {
                addPatternParamToContainer(paramClass, selectedObject, name, parentElement);
            } else if (selectedObject instanceof IncludeTable) {
                addPatternParamToIncludeTable(paramClass, selectedObject, name, parentElement);
            }
            updateSelected = false;
        }
        completeInserting(reverseEngineeringOldCopy);
    }

    public void setParamClass(Class paramClass) {
        this.paramClass = paramClass;
    }
}
