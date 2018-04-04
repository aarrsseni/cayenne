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
import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.ConfirmRemoveDialog;
import org.apache.cayenne.modeler.services.AttributeService;
import org.apache.cayenne.modeler.undo.RemoveAttributeUndoableEdit;

import java.awt.event.ActionEvent;

/**
 * Removes currently selected attribute from either the DbEntity or ObjEntity.
 */
public class RemoveAttributeAction extends RemoveAction implements MultipleObjectsAction {

    private final static String ACTION_NAME = "Remove Attribute";

    @Inject
    public Application application;

    @Inject
    public AttributeService attributeService;

    /**
     * Name of action if multiple rels are selected
     */
    private final static String ACTION_NAME_MULTIPLE = "Remove Attributes";

    public static String getActionName() {
        return ACTION_NAME;
    }

    public String getActionName(boolean multiple) {
        return multiple ? ACTION_NAME_MULTIPLE : ACTION_NAME;
    }

    public RemoveAttributeAction() {
        super(ACTION_NAME);
    }

    /**
     * Returns <code>true</code> if last object in the path contains a removable
     * attribute.
     */
    @Override
    public boolean enableForPath(ConfigurationNode object) {
        return object != null && object instanceof Attribute;
    }

    @Override
    public void performAction(ActionEvent e, boolean allowAsking) {
        ConfirmRemoveDialog dialog = getConfirmDeleteDialog(allowAsking);
        ProjectController mediator = getProjectController();

        EmbeddableAttribute[] embAttrs = getProjectController().getCurrentState().getEmbAttrs();
        ObjAttribute[] objAttrs = getProjectController().getCurrentState().getObjAttrs();
        DbAttribute[] dbAttrs = getProjectController().getCurrentState().getDbAttrs();

        
        if (embAttrs != null && embAttrs.length > 0) {
            if ((embAttrs.length == 1 && dialog.shouldDelete(
                    "Embeddable Attribute",
                    embAttrs[0].getName()))
                    || (embAttrs.length > 1 && dialog
                            .shouldDelete("selected EmbAttributes"))) {

                Embeddable embeddable = mediator.getCurrentState().getEmbeddable();

                application.getUndoManager().addEdit(
                        new RemoveAttributeUndoableEdit(embeddable, embAttrs));

                attributeService.removeEmbeddableAttributes(embeddable, embAttrs);

            }
        } else if (objAttrs != null && objAttrs.length > 0) {
            if ((objAttrs.length == 1 && dialog.shouldDelete("ObjAttribute", objAttrs[0]
                    .getName()))
                    || (objAttrs.length > 1 && dialog.shouldDelete("selected ObjAttributes"))) {

                ObjEntity entity = mediator.getCurrentState().getObjEntity();

                application.getUndoManager().addEdit(new RemoveAttributeUndoableEdit(entity, objAttrs));

                attributeService.removeObjAttributes(entity, objAttrs);
            }
        } else if (dbAttrs != null && dbAttrs.length > 0) {
        	if ((dbAttrs.length == 1 && dialog.shouldDelete("DbAttribute", dbAttrs[0]
        			.getName()))
                    || (dbAttrs.length > 1 && dialog.shouldDelete("selected DbAttributes"))) {

        		DbEntity entity = mediator.getCurrentState().getDbEntity();

                application.getUndoManager().addEdit(new RemoveAttributeUndoableEdit(entity, dbAttrs));

                attributeService.removeDbAttributes(mediator.getCurrentState().getDataMap(), entity, dbAttrs);
        	}
        }
    }
}
