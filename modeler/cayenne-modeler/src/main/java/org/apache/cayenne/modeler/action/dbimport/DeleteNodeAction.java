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
import org.apache.cayenne.dbsync.reverse.dbimport.Catalog;
import org.apache.cayenne.dbsync.reverse.dbimport.IncludeTable;
import org.apache.cayenne.dbsync.reverse.dbimport.ReverseEngineering;
import org.apache.cayenne.dbsync.reverse.dbimport.Schema;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.db.load.DbImportTreeNode;
import org.apache.cayenne.modeler.editor.dbimport.DbImportModel;
import org.apache.cayenne.modeler.editor.dbimport.DbImportView;
import org.apache.cayenne.modeler.editor.dbimport.DraggableTreePanel;
import org.apache.cayenne.modeler.event.ProjectDirtyEvent;
import org.apache.cayenne.modeler.services.ReverseEngineeringService;

import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * @since 4.1
 */
public class DeleteNodeAction extends TreeManipulationAction {

    private static final String ACTION_NAME = "Delete";
    private static final String ICON_NAME = "icon-trash.png";

    private DraggableTreePanel panel;

    @Inject
    private ReverseEngineeringService reverseEngineeringService;

    @Inject
    private ProjectController projectController;

    public DeleteNodeAction() {
        super(ACTION_NAME);
        setAlwaysOn(true);
    }

    public String getIconName() {
        return ICON_NAME;
    }

    private void deleteChilds(Catalog catalog) {
        Object selectedObject = this.selectedElement.getUserObject();
        reverseEngineeringService.deleteChilds(catalog, selectedObject);
    }

    private void deleteChilds(Schema schema) {
        Object selectedObject = this.selectedElement.getUserObject();
        reverseEngineeringService.deleteChilds(schema, selectedObject);
    }

    private void deleteChilds(IncludeTable includeTable) {
        Object selectedObject = this.selectedElement.getUserObject();
        reverseEngineeringService.deleteChilds(includeTable, selectedObject);
    }

    private void deleteChilds(ReverseEngineering reverseEngineering) {
        Object selectedObject = this.selectedElement.getUserObject();
        reverseEngineeringService.deleteChilds(reverseEngineering, selectedObject);
    }

    private void updateParentChilds() {
        DbImportModel model = (DbImportModel) tree.getModel();
        model.removeNodeFromParent(selectedElement);
        getProjectController().fireEvent(new ProjectDirtyEvent(this, true));
        model.reload(parentElement);
    }

    @Override
    public void performAction(ActionEvent e) {
        tree.stopEditing();
        final TreePath[] paths = tree.getSelectionPaths();
        final DbImportView rootParent = ((DbImportView) panel.getParent().getParent());
        rootParent.getLoadDbSchemaButton().setEnabled(false);
        rootParent.getReverseEngineeringProgress().setVisible(true);
        if (paths != null) {
            ReverseEngineering reverseEngineeringOldCopy = new ReverseEngineering(tree.getReverseEngineering());
            rootParent.lockToolbarButtons();
            for (TreePath path : paths) {
                selectedElement = (DbImportTreeNode) path.getLastPathComponent();
                parentElement = (DbImportTreeNode) selectedElement.getParent();
                if (parentElement != null) {
                    Object parentUserObject = parentElement.getUserObject();
                    if (parentUserObject instanceof ReverseEngineering) {
                        ReverseEngineering reverseEngineering = (ReverseEngineering) parentUserObject;
                        deleteChilds(reverseEngineering);
                    } else if (parentUserObject instanceof Catalog) {
                        Catalog catalog = (Catalog) parentUserObject;
                        deleteChilds(catalog);
                    } else if (parentUserObject instanceof Schema) {
                        Schema schema = (Schema) parentUserObject;
                        deleteChilds(schema);
                    } else if (parentUserObject instanceof IncludeTable) {
                        IncludeTable includeTable = (IncludeTable) parentUserObject;
                        deleteChilds(includeTable);
                    }
                }
            }
            if (paths.length > 1) {
                projectController.fireEvent(new ProjectDirtyEvent(this, true));
                ArrayList<DbImportTreeNode> expandList = tree.getTreeExpandList();
                tree.translateReverseEngineeringToTree(tree.getReverseEngineering(), false);
                tree.expandTree(expandList);
            } else {
                updateParentChilds();
            }
            putReverseEngineeringToUndoManager(reverseEngineeringOldCopy);
            rootParent.getLoadDbSchemaButton().setEnabled(true);
            rootParent.getReverseEngineeringProgress().setVisible(false);
        }
    }

    public void setPanel(DraggableTreePanel panel) {
        this.panel = panel;
    }
}
