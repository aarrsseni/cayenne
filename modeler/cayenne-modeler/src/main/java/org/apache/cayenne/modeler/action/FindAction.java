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
import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.map.Relationship;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.CayenneModelerFrame;
import org.apache.cayenne.modeler.ProjectTreeModel;
import org.apache.cayenne.modeler.ProjectTreeView;
import org.apache.cayenne.modeler.dialog.FindDialog;
import org.apache.cayenne.modeler.editor.EditorView;
import org.apache.cayenne.modeler.event.*;
import org.apache.cayenne.modeler.services.DefaultFindService;
import org.apache.cayenne.modeler.util.CayenneAction;

import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;

public class FindAction extends CayenneAction {

    @Inject
    public Application application;

    @Inject
    public DefaultFindService findService;

    public static String getActionName() {
        return "Find";
    }

    public FindAction() {
        super(getActionName());
    }

    /**
     * All entities that contain a pattern substring (case-indifferent) in the name are produced.
     */
    public void performAction(ActionEvent e) {
        JTextField source = (JTextField) e.getSource();
        String searchStr = source.getText().trim();
        if (searchStr.startsWith("*")) {
            searchStr = searchStr.substring(1);
        }

        if(searchStr.isEmpty()) {
            markEmptySearch(source);
            return;
        }

        List<DefaultFindService.SearchResultEntry> searchResults = findService.search(searchStr);

        if(searchResults.isEmpty()){
            markEmptySearch(source);
        } else if(searchResults.size() == 1){
            jumpToResult(searchResults.iterator().next());
        } else {
            new FindDialog(application.getFrameController(), searchResults).startupAction();
        }
    }

    private void markEmptySearch(JTextField source) {
        source.setBackground(Color.pink);
    }

    /**
     * Navigate to search result
     * Used also in {@link org.apache.cayenne.modeler.graph.action.EntityDisplayAction}
     */
    public static void jumpToResult(DefaultFindService.SearchResultEntry searchResultEntry) {
        EditorView editor = ((CayenneModelerFrame) Application.getInstance().getFrameController().getView()).getView();
        DataChannelDescriptor domain = (DataChannelDescriptor) Application.getInstance().getProject().getRootNode();

        if (searchResultEntry.getObject() instanceof Entity) {
            jumpToEntityResult((Entity) searchResultEntry.getObject(), editor, domain);
        } else if (searchResultEntry.getObject() instanceof QueryDescriptor) {
            jumpToQueryResult((QueryDescriptor)searchResultEntry.getObject(), editor, domain);
        } else if (searchResultEntry.getObject() instanceof Embeddable) {
            jumpToEmbeddableResult((Embeddable)searchResultEntry.getObject(), editor, domain);
        } else if (searchResultEntry.getObject() instanceof EmbeddableAttribute) {
            jumpToEmbeddableAttributeResult((EmbeddableAttribute)searchResultEntry.getObject(), editor, domain);
        } else if (searchResultEntry.getObject() instanceof Attribute || searchResultEntry.getObject() instanceof Relationship) {
            jumpToAttributeResult(searchResultEntry, editor, domain);
        } else if (searchResultEntry.getObject() instanceof Procedure) {
            jumpToProcedureResult((Procedure)searchResultEntry.getObject(), editor, domain);
        } else if (searchResultEntry.getObject() instanceof ProcedureParameter) {
            jumpToProcedureResult((ProcedureParameter)searchResultEntry.getObject(), editor, domain);
        }
    }

    private static void jumpToAttributeResult(DefaultFindService.SearchResultEntry searchResultEntry, EditorView editor, DataChannelDescriptor domain) {
        DataMap map;
        Entity entity;
        if (searchResultEntry.getObject() instanceof Attribute) {
            map = ((Attribute) searchResultEntry.getObject()).getEntity().getDataMap();
            entity = ((Attribute) searchResultEntry.getObject()).getEntity();
        } else {
            map = ((Relationship) searchResultEntry.getObject()).getSourceEntity().getDataMap();
            entity = ((Relationship) searchResultEntry.getObject()).getSourceEntity();
        }
        buildAndSelectTreePath(map, entity, editor);

        if (searchResultEntry.getObject() instanceof Attribute) {
            if(searchResultEntry.getObject() instanceof DbAttribute) {
                DbAttributeDisplayEvent event = new DbAttributeDisplayEvent(editor.getProjectTreeView(),
                        (Attribute) searchResultEntry.getObject(), entity, map, domain);
                event.setMainTabFocus(true);
                editor.getDbDetailView().currentDbAttributeChanged(event);
                editor.getDbDetailView().repaint();
            } else {
                ObjAttributeDisplayEvent event = new ObjAttributeDisplayEvent(editor.getProjectTreeView(),
                        (Attribute) searchResultEntry.getObject(), entity, map, domain);
                event.setMainTabFocus(true);
                editor.getObjDetailView().currentObjAttributeChanged(event);
                editor.getObjDetailView().repaint();
            }
        } else if (searchResultEntry.getObject() instanceof Relationship) {
            if(searchResultEntry.getObject() instanceof DbRelationship) {
                DbRelationshipDisplayEvent event = new DbRelationshipDisplayEvent(editor.getProjectTreeView(),
                        (Relationship) searchResultEntry.getObject(), entity, map, domain);
                event.setMainTabFocus(true);
                editor.getDbDetailView().currentDbRelationshipChanged(event);
                editor.getDbDetailView().repaint();
            } else {
                ObjRelationshipDisplayEvent event = new ObjRelationshipDisplayEvent(editor.getProjectTreeView(),
                        (Relationship) searchResultEntry.getObject(), entity, map, domain);
                event.setMainTabFocus(true);
                editor.getObjDetailView().currentObjRelationshipChanged(event);
                editor.getObjDetailView().repaint();
            }
        }
    }

    private static void jumpToEmbeddableAttributeResult(EmbeddableAttribute attribute, EditorView editor, DataChannelDescriptor domain) {
        Embeddable embeddable = attribute.getEmbeddable();
        DataMap map = embeddable.getDataMap();
        buildAndSelectTreePath(map, embeddable, editor);
        EmbeddableAttributeDisplayEvent event = new EmbeddableAttributeDisplayEvent(editor.getProjectTreeView(),
                embeddable, attribute, map, domain);
        event.setMainTabFocus(true);
        editor.getEmbeddableView().currentEmbeddableAttributeChanged(event);
        editor.getEmbeddableView().repaint();
    }

    private static void jumpToEmbeddableResult(Embeddable embeddable, EditorView editor, DataChannelDescriptor domain) {
        DataMap map = embeddable.getDataMap();
        buildAndSelectTreePath(map, embeddable, editor);
        EmbeddableDisplayEvent event = new EmbeddableDisplayEvent(editor.getProjectTreeView(), embeddable, map, domain);
        event.setMainTabFocus(true);
        editor.currentEmbeddableChanged(event);
    }

    private static void jumpToQueryResult(QueryDescriptor queryDescriptor, EditorView editor, DataChannelDescriptor domain) {
        DataMap map = queryDescriptor.getDataMap();
        buildAndSelectTreePath(map, queryDescriptor, editor);
        QueryDisplayEvent event = new QueryDisplayEvent(editor.getProjectTreeView(), queryDescriptor, map, domain);
        editor.currentQueryChanged(event);
    }

    private static void jumpToEntityResult(Entity entity, EditorView editor, DataChannelDescriptor domain) {
        DataMap map = entity.getDataMap();
        buildAndSelectTreePath(map, entity, editor);
        if (entity instanceof ObjEntity) {
            ObjEntityDisplayEvent event;
            event = new ObjEntityDisplayEvent(editor.getProjectTreeView(), entity, map, domain);
            event.setMainTabFocus(true);
            editor.getObjDetailView().currentObjEntityChanged(event);
        } else if (entity instanceof DbEntity) {
            DbEntityDisplayEvent event;
            event = new DbEntityDisplayEvent(editor.getProjectTreeView(), entity, map, domain);
            event.setMainTabFocus(true);
            editor.getDbDetailView().currentDbEntityChanged(event);
        }
    }

    private static void jumpToProcedureResult(Procedure procedure, EditorView editor, DataChannelDescriptor domain) {
        DataMap map = procedure.getDataMap();
        buildAndSelectTreePath(map, procedure, editor);
        ProcedureDisplayEvent event = new ProcedureDisplayEvent(editor.getProjectTreeView(), procedure, map, domain);
        editor.getProcedureView().currentProcedureChanged(event);
        editor.getProcedureView().repaint();
    }

    private static void jumpToProcedureResult(ProcedureParameter parameter, EditorView editor, DataChannelDescriptor domain) {
        Procedure procedure = parameter.getProcedure();
        DataMap map = procedure.getDataMap();
        buildAndSelectTreePath(map, procedure, editor);
        ProcedureParameterDisplayEvent event =
                new ProcedureParameterDisplayEvent(editor.getProjectTreeView(), parameter, procedure, map, domain);
        editor.getProcedureView().currentProcedureParameterChanged(event);
        editor.getProcedureView().repaint();
    }

    /**
     * Builds a tree path for a given path and make selection in it
     */
    private static TreePath buildAndSelectTreePath(DataMap map, Object object, EditorView editor) {
        ProjectTreeView projectTreeView = editor.getProjectTreeView();
        ProjectTreeModel treeModel = (ProjectTreeModel) projectTreeView.getModel();

        DefaultMutableTreeNode[] mutableTreeNodes = new DefaultMutableTreeNode[] {
            treeModel.getRootNode(),
            treeModel.getNodeForObjectPath(new Object[]{map}),
            treeModel.getNodeForObjectPath(new Object[]{map, object})
        };

        TreePath treePath = new TreePath(mutableTreeNodes);
        if (!projectTreeView.isExpanded(treePath.getParentPath())) {
            projectTreeView.expandPath(treePath.getParentPath());
        }
        projectTreeView.getSelectionModel().setSelectionPath(treePath);
        return treePath;
    }
}
