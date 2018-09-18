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
package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.CayenneModelerController;
import org.apache.cayenne.modeler.event.RemoveCallbackMethodEvent;
import org.apache.cayenne.modeler.event.RemoveDataMapEvent;
import org.apache.cayenne.modeler.event.RemoveDataMapFromDataNodeEvent;
import org.apache.cayenne.modeler.event.RemoveDataNodeEvent;
import org.apache.cayenne.modeler.event.RemoveDbAttributesEvent;
import org.apache.cayenne.modeler.event.RemoveDbEntityEvent;
import org.apache.cayenne.modeler.event.RemoveDbRelationshipsEvent;
import org.apache.cayenne.modeler.event.RemoveEmbeddableAttributeEvent;
import org.apache.cayenne.modeler.event.RemoveEmbeddableEvent;
import org.apache.cayenne.modeler.event.RemoveObjAttributesEvent;
import org.apache.cayenne.modeler.event.RemoveObjEntityEvent;
import org.apache.cayenne.modeler.event.RemoveObjRelationshipsEvent;
import org.apache.cayenne.modeler.event.RemovePathsEvent;
import org.apache.cayenne.modeler.event.RemoveProcedureEvent;
import org.apache.cayenne.modeler.event.RemoveQueryEvent;
import org.apache.cayenne.modeler.event.listener.RemoveCallbackMethodListener;
import org.apache.cayenne.modeler.event.listener.RemoveDataMapFromDataNodeListener;
import org.apache.cayenne.modeler.event.listener.RemoveDataMapListener;
import org.apache.cayenne.modeler.event.listener.RemoveDataNodeListener;
import org.apache.cayenne.modeler.event.listener.RemoveDbAttributesListener;
import org.apache.cayenne.modeler.event.listener.RemoveDbEntityListener;
import org.apache.cayenne.modeler.event.listener.RemoveDbRelationshipsListener;
import org.apache.cayenne.modeler.event.listener.RemoveEmbeddableAttributeListener;
import org.apache.cayenne.modeler.event.listener.RemoveEmbeddableListener;
import org.apache.cayenne.modeler.event.listener.RemoveObjAttributesListener;
import org.apache.cayenne.modeler.event.listener.RemoveObjEntityListener;
import org.apache.cayenne.modeler.event.listener.RemoveObjRelationshipsListener;
import org.apache.cayenne.modeler.event.listener.RemovePathsListener;
import org.apache.cayenne.modeler.event.listener.RemoveProcedureListener;
import org.apache.cayenne.modeler.event.listener.RemoveQueryListener;
import org.apache.cayenne.modeler.services.DataMapService;
import org.apache.cayenne.modeler.services.DbEntityService;
import org.apache.cayenne.modeler.services.EmbeddableService;
import org.apache.cayenne.modeler.services.NodeService;
import org.apache.cayenne.modeler.services.ObjEntityService;
import org.apache.cayenne.modeler.services.ProcedureService;
import org.apache.cayenne.modeler.services.QueryService;
import org.apache.cayenne.modeler.undo.RemoveAttributeUndoableEdit;
import org.apache.cayenne.modeler.undo.RemoveCallbackMethodUndoableEdit;
import org.apache.cayenne.modeler.undo.RemoveCompoundUndoableEdit;
import org.apache.cayenne.modeler.undo.RemoveRelationshipUndoableEdit;
import org.apache.cayenne.modeler.undo.RemoveUndoableEdit;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 * @since 4.1
 */
public class RemoveActionListener implements RemoveObjEntityListener, RemoveDbEntityListener, RemoveQueryListener, RemoveProcedureListener, RemoveEmbeddableListener,
                                            RemoveDataMapListener, RemoveDataMapFromDataNodeListener, RemoveDataNodeListener, RemovePathsListener, RemoveCallbackMethodListener,
                                            RemoveObjRelationshipsListener, RemoveDbRelationshipsListener, RemoveEmbeddableAttributeListener, RemoveDbAttributesListener, RemoveObjAttributesListener{

    private CayenneModelerController cayenneModelerController;

    public RemoveActionListener(CayenneModelerController cayenneModelerController) {
        this.cayenneModelerController = cayenneModelerController;
    }

    @Override
    public void removeObjEntity(RemoveObjEntityEvent e) {
        Application.getInstance().getUndoManager()
                .addEdit(new RemoveUndoableEdit(e.getRoot(), e.getObjEntity()));
    }

    @Override
    public void removeDbEntity(RemoveDbEntityEvent e) {
        Application.getInstance().getUndoManager()
                .addEdit(new RemoveUndoableEdit(e.getRoot(), e.getDbEntity()));
    }

    @Override
    public void removeQuery(RemoveQueryEvent e) {
        Application.getInstance().getUndoManager()
                .addEdit(new RemoveUndoableEdit(e.getDataMap(), e.getQuery()));
    }

    @Override
    public void removeProcedure(RemoveProcedureEvent e) {
        Application.getInstance().getUndoManager()
                .addEdit(new RemoveUndoableEdit(e.getDataMap(), e.getProcedure()));
    }

    @Override
    public void removeEmbeddable(RemoveEmbeddableEvent e) {
        Application.getInstance().getUndoManager()
                .addEdit(new RemoveUndoableEdit(e.getDataMap(), e.getEmbeddable()));
    }

    @Override
    public void removeDataMap(RemoveDataMapEvent e) {
        Application.getInstance().getUndoManager()
                .addEdit(new RemoveUndoableEdit(Application.getInstance(), e.getDataMap()));
    }

    @Override
    public void removeDataMapFromDataNodeListener(RemoveDataMapFromDataNodeEvent e) {
        Application.getInstance().getUndoManager()
                .addEdit(new RemoveUndoableEdit(Application.getInstance(), e.getDataNode(),
                        e.getDataMap()));
    }

    @Override
    public void removeDataNode(RemoveDataNodeEvent e) {
        Application.getInstance().getUndoManager()
                .addEdit(new RemoveUndoableEdit(Application.getInstance(), e.getDataNode()));
    }

    @Override
    public void removePaths(RemovePathsEvent e) {
        ConfigurationNode[] paths = e.getProjectController().getCurrentState().getPaths();
        ConfigurationNode parentPath = e.getProjectController().getCurrentState().getParentPath();

        CompoundEdit compoundEdit = new RemoveCompoundUndoableEdit();
        for (ConfigurationNode path : paths) {
            compoundEdit.addEdit(removeLastPathComponent(path, parentPath));
        }
        compoundEdit.end();

        Application.getInstance().getUndoManager().addEdit(compoundEdit);
    }

    /**
     * Removes an object, depending on its type
     */
    private UndoableEdit removeLastPathComponent(ConfigurationNode object, ConfigurationNode parentObject) {

        UndoableEdit undo = null;

        if (object instanceof DataMap) {
            if (parentObject instanceof DataNodeDescriptor) {
                undo = new RemoveUndoableEdit(Application.getInstance(), (DataNodeDescriptor) parentObject, (DataMap) object);
                cayenneModelerController.getProjectController().getBootiqueInjector()
                        .getInstance(DataMapService.class).removeDataMapFromDataNode((DataNodeDescriptor) parentObject, (DataMap) object);
            } else {
                // Not under Data Node, remove completely
                undo = new RemoveUndoableEdit(cayenneModelerController.getApplication(), (DataMap) object);
                cayenneModelerController.getProjectController().getBootiqueInjector()
                        .getInstance(DataMapService.class).removeDataMap((DataMap) object);
            }
        } else if (object instanceof DataNodeDescriptor) {
            undo = new RemoveUndoableEdit(cayenneModelerController.getApplication(), (DataNodeDescriptor) object);
            cayenneModelerController.getProjectController().getBootiqueInjector()
                    .getInstance(NodeService.class).removeDataNode((DataNodeDescriptor) object);
        } else if (object instanceof DbEntity) {
            undo = new RemoveUndoableEdit(((DbEntity) object).getDataMap(), (DbEntity) object);
            cayenneModelerController.getProjectController().getBootiqueInjector()
                    .getInstance(DbEntityService.class).removeDbEntity(((DbEntity) object).getDataMap(), (DbEntity) object);
        } else if (object instanceof ObjEntity) {
            undo = new RemoveUndoableEdit(((ObjEntity) object).getDataMap(), (ObjEntity) object);
            cayenneModelerController.getProjectController().getBootiqueInjector()
                    .getInstance(ObjEntityService.class).removeObjEntity(((ObjEntity) object).getDataMap(), (ObjEntity) object);
        } else if (object instanceof QueryDescriptor) {
            undo = new RemoveUndoableEdit(((QueryDescriptor) object).getDataMap(), (QueryDescriptor) object);
            cayenneModelerController.getProjectController().getBootiqueInjector()
                    .getInstance(QueryService.class).removeQuery(((QueryDescriptor) object).getDataMap(), (QueryDescriptor) object);
        } else if (object instanceof Procedure) {
            undo = new RemoveUndoableEdit(((Procedure) object).getDataMap(), (Procedure) object);
            cayenneModelerController.getProjectController().getBootiqueInjector()
                    .getInstance(ProcedureService.class).removeProcedure(((Procedure) object).getDataMap(), (Procedure) object);
        } else if (object instanceof Embeddable) {
            undo = new RemoveUndoableEdit(((Embeddable) object).getDataMap(), (Embeddable) object);
            cayenneModelerController.getProjectController().getBootiqueInjector()
                    .getInstance(EmbeddableService.class).removeEmbeddable(((Embeddable) object).getDataMap(), (Embeddable) object);
        }

        return undo;
    }

    @Override
    public void removeCallbackMethod(RemoveCallbackMethodEvent e) {
        Application.getInstance().getUndoManager()
                .addEdit(new RemoveCallbackMethodUndoableEdit(e.getCallbackType(), e.getMethods()));
    }

    @Override
    public void removeObjRelationships(RemoveObjRelationshipsEvent e) {
        Application.getInstance().getUndoManager().addEdit(new RemoveRelationshipUndoableEdit(e.getEntity(), e.getRels()));
    }

    @Override
    public void removeDbRels(RemoveDbRelationshipsEvent e) {
        Application.getInstance().getUndoManager().addEdit(new RemoveRelationshipUndoableEdit(e.getEntity(), e.getDbRels()));
    }

    @Override
    public void removeEmbeddableAttribute(RemoveEmbeddableAttributeEvent e) {
        Application.getInstance().getUndoManager().addEdit(
                new RemoveAttributeUndoableEdit(e.getEmbeddable(), e.getEmbAttr()));
    }

    @Override
    public void removeDbAttributes(RemoveDbAttributesEvent e) {
        Application.getInstance().getUndoManager().addEdit(new RemoveAttributeUndoableEdit(e.getEntity(), e.getDbAttrs()));
    }

    @Override
    public void removeObjAttributes(RemoveObjAttributesEvent e) {
        Application.getInstance().getUndoManager().addEdit(new RemoveAttributeUndoableEdit(e.getEntity(), e.getObjAttrs()));
    }
}
