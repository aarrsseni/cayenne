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
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.dialog.ErrorDebugDialog;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.services.PasteService;
import org.apache.cayenne.modeler.undo.PasteCompoundUndoableEdit;
import org.apache.cayenne.modeler.undo.PasteUndoableEdit;
import org.apache.cayenne.modeler.util.CayenneAction;
import org.apache.cayenne.modeler.util.CayenneTransferable;
import org.apache.cayenne.query.Query;

import javax.swing.KeyStroke;
import javax.swing.undo.UndoableEdit;
import java.awt.Toolkit;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Action for pasting entities, queries etc. from the system buffer
 */
public class PasteAction extends CayenneAction implements FlavorListener {

    @Inject
    public Application application;

    @Inject
    public PasteService pasteService;

    private static final String COPY_PATTERN = "copy of %s (%d)";

    /**
     * Constructor for PasteAction
     */
    public PasteAction() {
        super(getActionName());

        // add listener, so that button state would update event if clipboard was filled
        // by other app
        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(this);
    }

    public static String getActionName() {
        return "Paste";
    }

    @Override
    public String getIconName() {
        return "icon-paste.png";
    }

    @Override
    public KeyStroke getAcceleratorKey() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit
                .getDefaultToolkit()
                .getMenuShortcutKeyMask());
    }

    /**
     * Performs pasting items from the system buffer
     */
    @Override
    public void performAction(ActionEvent e) {
        try {
            Object content = Toolkit.getDefaultToolkit().getSystemClipboard().getData(
                    CayenneTransferable.CAYENNE_FLAVOR);

            Object currentObject = getProjectController().getCurrentObject();

            if(content instanceof DataMap) {
                currentObject = getProjectController().getProject().getRootNode();
            }

            if (content != null && currentObject != null) {
                DataChannelDescriptor domain = (DataChannelDescriptor) getProjectController()
                        .getProject()
                        .getRootNode();
                DataMap map = getProjectController().getCurrentState().getDataMap();

                UndoableEdit undoableEdit;
                if (content instanceof List) {
                    undoableEdit = new PasteCompoundUndoableEdit();

                    for (Object o : (List) content) {
                        pasteService.paste(currentObject, o);
                        undoableEdit.addEdit(new PasteUndoableEdit(
                                domain,
                                map,
                                currentObject,
                                o));
                    }
                } else {
                    pasteService.paste(currentObject, content);
                    undoableEdit = new PasteUndoableEdit(domain, map, currentObject, content);
                }

                application.getUndoManager().addEdit(undoableEdit);
            }
        } catch (UnsupportedFlavorException ufe) {
            // do nothing
        } catch (Exception ex) {
            ErrorDebugDialog.guiException(ex);
        }
    }

    /**
     * Returns <code>true</code> if last object in the path contains a removable object.
     */
    @Override
    public boolean enableForPath(ConfigurationNode object) {
        if (object == null) {
            return false;
        }

        return getState();
    }

    /**
     * Enables or disables the action, judging last selected component
     */
    public void updateState() {
        setEnabled(getState());
    }

    /**
     * Returns desired enable state for this action
     */
    private boolean getState() {
        try {
            Object content = Toolkit.getDefaultToolkit().getSystemClipboard().getData(
                    CayenneTransferable.CAYENNE_FLAVOR);

            if (content instanceof List) {
                content = ((List) content).get(0);
            }

            Object currentObject = getProjectController().getCurrentObject();

            if (currentObject == null) {
                return false;
            }

            /**
             * Checking all available pairs source-pasting object
             */

            return ((currentObject instanceof DataChannelDescriptor || currentObject instanceof DataNodeDescriptor) && content instanceof DataMap)
                    ||

                    (currentObject instanceof DataMap && pasteService.isTreeLeaf(content))
                    ||

                    (currentObject instanceof DataMap && content instanceof DataMap)
                    ||

                    (currentObject instanceof DbEntity && (content instanceof DbAttribute
                            || content instanceof DbRelationship || pasteService.isTreeLeaf(content)))
                    ||

                    (currentObject instanceof ObjEntity && (content instanceof ObjAttribute
                            || content instanceof ObjRelationship || content instanceof ObjCallbackMethod || pasteService.isTreeLeaf(content)))
                    ||

                    (currentObject instanceof Embeddable && (content instanceof EmbeddableAttribute || pasteService.isTreeLeaf(content)))
                    ||

                    (currentObject instanceof Procedure
                            && (content instanceof ProcedureParameter || pasteService.isTreeLeaf(content)) ||

                            (currentObject instanceof Query && pasteService.isTreeLeaf(content)));
        } catch (Exception ex) {
            return false;
        }
    }

    public void flavorsChanged(FlavorEvent e) {
        updateState();
    }
}
