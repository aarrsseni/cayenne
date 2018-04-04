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
import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.ConfirmRemoveDialog;
import org.apache.cayenne.modeler.editor.CallbackType;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.services.*;
import org.apache.cayenne.modeler.undo.*;
import org.apache.cayenne.modeler.util.CayenneAction;
import org.apache.cayenne.modeler.util.ProjectUtil;

import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Removes currently selected object from the project. This can be Domain, DataNode,
 * Entity, Attribute or Relationship.
 */
public class RemoveAction extends CayenneAction {

    @Inject
    public Application application;

    @Inject
    public ProcedureService procedureService;

    @Inject
    public AttributeService attributeService;

    @Inject
    public RelationshipService relationshipService;

    @Inject
    public DataMapService dataMapService;

    @Inject
    public EmbeddableService embeddableService;

    @Inject
    public ObjEntityService objEntityService;

    @Inject
    public QueryService queryService;

    @Inject
    public NodeService nodeService;

    @Inject
    public DbEntityService dbEntityService;

    @Inject
    public CallbackMethodService callbackMethodService;

    public static String getActionName() {
        return "Remove";
    }

    public RemoveAction() {
        super(getActionName());
    }

    protected RemoveAction(String actionName) {
        super(actionName);
    }

    @Override
    public String getIconName() {
        return "icon-trash.png";
    }

    @Override
    public KeyStroke getAcceleratorKey() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }

    /**
     * Creates and returns dialog for delete prompt
     * 
     * @param allowAsking If false, no question will be asked no matter what settings are
     */
    public ConfirmRemoveDialog getConfirmDeleteDialog(boolean allowAsking) {
        return new ConfirmRemoveDialog(allowAsking);
    }

    @Override
    public void performAction(ActionEvent e) {
        performAction(e, true);
    }

    /**
     * Performs delete action
     * 
     * @param allowAsking If false, no question will be asked no matter what settings are
     */
    public void performAction(ActionEvent e, boolean allowAsking) {

        ProjectController mediator = getProjectController();
        ConfirmRemoveDialog dialog = getConfirmDeleteDialog(allowAsking);
        
        if (mediator.getCurrentState().getObjEntity() != null) {
            if (dialog.shouldDelete("ObjEntity", mediator.getCurrentState().getObjEntity().getName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getObjEntity()));
                objEntityService.removeObjEntity(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getObjEntity());
            }
        } else if (mediator.getCurrentState().getDbEntity() != null) {
            if (dialog.shouldDelete("DbEntity", mediator.getCurrentState().getDbEntity().getName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getDbEntity()));
                dbEntityService.removeDbEntity(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getDbEntity());
            }
        } else if (mediator.getCurrentState().getQuery() != null) {
            if (dialog.shouldDelete("query", mediator.getCurrentState().getQuery().getName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getQuery()));
                queryService.removeQuery(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getQuery());
            }
        } else if (mediator.getCurrentState().getProcedure() != null) {
            if (dialog.shouldDelete("procedure", mediator.getCurrentState().getProcedure().getName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getProcedure()));
                procedureService.removeProcedure(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getProcedure());
            }
        } else if (mediator.getCurrentState().getEmbeddable() != null) {
            if (dialog.shouldDelete("embeddable", mediator.getCurrentState().getEmbeddable().getClassName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getEmbeddable()));
                embeddableService.removeEmbeddable(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getEmbeddable());
            }
        } else if (mediator.getCurrentState().getDataMap() != null) {
            if (dialog.shouldDelete("data map", mediator.getCurrentState().getDataMap().getName())) {

                // In context of Data node just remove from Data Node
                if (mediator.getCurrentState().getNode() != null) {
                    application.getUndoManager()
                            .addEdit(new RemoveUndoableEdit(application, mediator.getCurrentState().getNode(),
                                    mediator.getCurrentState().getDataMap()));
                    dataMapService.removeDataMapFromDataNode(mediator.getCurrentState().getNode(), mediator.getCurrentState().getDataMap());
                } else {
                    // Not under Data Node, remove completely
                    application.getUndoManager()
                            .addEdit(new RemoveUndoableEdit(application, mediator.getCurrentState().getDataMap()));
                    dataMapService.removeDataMap(mediator.getCurrentState().getDataMap());
                }
            }
        } else if (mediator.getCurrentState().getNode() != null) {
            if (dialog.shouldDelete("data node", mediator.getCurrentState().getNode().getName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(application, mediator.getCurrentState().getNode()));
                nodeService.removeDataNode(mediator.getCurrentState().getNode());
            }
        } else if (mediator.getCurrentState().getPaths() != null) { // multiple deletion
            if (dialog.shouldDelete("selected objects")) {

                ConfigurationNode[] paths = mediator.getCurrentState().getPaths();
                ConfigurationNode parentPath = mediator.getCurrentState().getParentPath();

                CompoundEdit compoundEdit = new RemoveCompoundUndoableEdit();
                for (ConfigurationNode path : paths) {
                    compoundEdit.addEdit(removeLastPathComponent(path, parentPath));
                }
                compoundEdit.end();

                application.getUndoManager().addEdit(compoundEdit);
            }
        } else if(mediator.getCurrentState().getCallbackMethods().length > 0) {
            removeMethods(mediator, dialog, getProjectController().getCurrentState().getCallbackMethods());
        } else if(mediator.getCurrentState().getObjRels().length > 0) {
      		removeObjRelationships(mediator, dialog, getProjectController().getCurrentState().getObjRels());
        } else if(mediator.getCurrentState().getDbRels().length > 0) {
      		removeDBRelationships(mediator, dialog, getProjectController().getCurrentState().getDbRels());
        } else if(mediator.getCurrentState().getObjAttrs().length > 0) {
      		removeObjAttributes(mediator, dialog, getProjectController().getCurrentState().getObjAttrs());
        } else if(mediator.getCurrentState().getEmbAttrs().length > 0) {
      		removeEmbAttributes(mediator, dialog, getProjectController().getCurrentState().getEmbAttrs());
        } else if(mediator.getCurrentState().getDbAttrs().length > 0) {
        	removeDbAttributes(mediator, dialog, getProjectController().getCurrentState().getDbAttrs());
        } else if(mediator.getCurrentState().getProcedureParameters().length > 0) {
        	procedureService.removeProcedureParameters(mediator.getCurrentState().getProcedure(), mediator.getCurrentState().getProcedureParameters());
        }

    }
    
    private void removeEmbAttributes(ProjectController mediator, ConfirmRemoveDialog dialog,
                                     EmbeddableAttribute[] embAttrs) {
    	if (embAttrs != null && embAttrs.length > 0) {
        	if ((embAttrs.length == 1 && dialog.shouldDelete("DbAttribute", embAttrs[0].getName()))
                    || (embAttrs.length > 1 && dialog.shouldDelete("selected DbAttributes"))) {

        		Embeddable embeddable = mediator.getCurrentState().getEmbeddable();

                application.getUndoManager()
                        .addEdit(new RemoveAttributeUndoableEdit(embeddable, embAttrs));

                attributeService.removeEmbeddableAttributes(embeddable, embAttrs);

                ProjectUtil.cleanObjMappings(mediator.getCurrentState().getDataMap());
        	}
    	}
	}

	private void removeObjAttributes(ProjectController mediator,
			ConfirmRemoveDialog dialog, ObjAttribute[] objAttrs) {
    	if (objAttrs != null && objAttrs.length > 0) {
        	if ((objAttrs.length == 1 && dialog.shouldDelete("DbAttribute", objAttrs[0].getName()))
                    || (objAttrs.length > 1 && dialog.shouldDelete("selected DbAttributes"))) {

        		ObjEntity entity = mediator.getCurrentState().getObjEntity();

                application.getUndoManager()
                        .addEdit(new RemoveAttributeUndoableEdit(entity, objAttrs));

                attributeService.removeObjAttributes(objAttrs);
        	}
    	}
	}

	private void removeDbAttributes(ProjectController mediator, ConfirmRemoveDialog dialog, DbAttribute[] dbAttrs) {
    	if (dbAttrs != null && dbAttrs.length > 0) {
        	if ((dbAttrs.length == 1 && dialog.shouldDelete("DbAttribute", dbAttrs[0].getName()))
                    || (dbAttrs.length > 1 && dialog.shouldDelete("selected DbAttributes"))) {

        		DbEntity entity = mediator.getCurrentState().getDbEntity();

                application.getUndoManager()
                        .addEdit(new RemoveAttributeUndoableEdit(entity, dbAttrs));

                attributeService.removeDbAttributes(mediator.getCurrentState().getDataMap(), entity, dbAttrs);
        	}
    	}
    }
    
    private void removeDBRelationships(ProjectController mediator, ConfirmRemoveDialog dialog,
                                       DbRelationship[] dbRels) {
		if (dbRels != null && dbRels.length > 0) {
			if ((dbRels.length == 1 && dialog.shouldDelete("DbRelationship", dbRels[0].getName()))
					|| (dbRels.length > 1 && dialog.shouldDelete("selected DbRelationships"))) {
				DbEntity entity = mediator.getCurrentState().getDbEntity();
				
				relationshipService.removeDbRelationships(entity, dbRels);

				application.getUndoManager().addEdit(new RemoveRelationshipUndoableEdit(entity, dbRels));
			}
		}
	}

	private void removeObjRelationships(ProjectController mediator, ConfirmRemoveDialog dialog,
                                        ObjRelationship[] rels) {
		if ((rels.length == 1 && dialog.shouldDelete("ObjRelationship", rels[0].getName()))
				|| (rels.length > 1 && dialog.shouldDelete("selected ObjRelationships"))) {
			ObjEntity entity = mediator.getCurrentState().getObjEntity();

			relationshipService.removeObjRelationships(entity, rels);

			application.getUndoManager().addEdit(new RemoveRelationshipUndoableEdit(entity, rels));
		}		
	}

	private void removeMethods(ProjectController mediator, ConfirmRemoveDialog dialog, ObjCallbackMethod[] methods) {
    	CallbackMap callbackMap = mediator.getCurrentState().getObjEntity().getCallbackMap();
    	CallbackType callbackType = mediator.getCurrentState().getCallbackType();

        if ((methods.length == 1 && dialog.shouldDelete("callback method", methods[0].getName()))
        	|| (methods.length > 1 && dialog.shouldDelete("selected callback methods"))) {
            for (ObjCallbackMethod callbackMethod : methods) {
            	callbackMethodService.removeCallbackMethod(callbackType, callbackMethod.getName());
            }
            
            application.getUndoManager()
                    .addEdit(new RemoveCallbackMethodUndoableEdit(callbackType, methods));
        }		
	}

    /**
     * Returns <code>true</code> if last object in the path contains a removable object.
     */
    @Override
    public boolean enableForPath(ConfigurationNode object) {
        return (object instanceof DataChannelDescriptor)
                || (object instanceof DataMap)
                || (object instanceof DataNodeDescriptor)
                || (object instanceof Entity)
                || (object instanceof Attribute)
                || (object instanceof Relationship)
                || (object instanceof Procedure)
                || (object instanceof ProcedureParameter)
                || (object instanceof QueryDescriptor)
                || (object instanceof Embeddable)
                || (object instanceof EmbeddableAttribute);
    }

    /**
     * Removes an object, depending on its type
     */
    private UndoableEdit removeLastPathComponent(ConfigurationNode object, ConfigurationNode parentObject) {

        UndoableEdit undo = null;

        if (object instanceof DataMap) {
            if (parentObject != null && parentObject instanceof DataNodeDescriptor) {
                undo = new RemoveUndoableEdit(application, (DataNodeDescriptor) parentObject, (DataMap) object);
                dataMapService.removeDataMapFromDataNode((DataNodeDescriptor) parentObject, (DataMap) object);
            } else {
                // Not under Data Node, remove completely
                undo = new RemoveUndoableEdit(application, (DataMap) object);
                dataMapService.removeDataMap((DataMap) object);
            }
        } else if (object instanceof DataNodeDescriptor) {
            undo = new RemoveUndoableEdit(application, (DataNodeDescriptor) object);
            nodeService.removeDataNode((DataNodeDescriptor) object);
        } else if (object instanceof DbEntity) {
            undo = new RemoveUndoableEdit(((DbEntity) object).getDataMap(), (DbEntity) object);
            dbEntityService.removeDbEntity(((DbEntity) object).getDataMap(), (DbEntity) object);
        } else if (object instanceof ObjEntity) {
            undo = new RemoveUndoableEdit(((ObjEntity) object).getDataMap(), (ObjEntity) object);
            objEntityService.removeObjEntity(((ObjEntity) object).getDataMap(), (ObjEntity) object);
        } else if (object instanceof QueryDescriptor) {
            undo = new RemoveUndoableEdit(((QueryDescriptor) object).getDataMap(), (QueryDescriptor) object);
            queryService.removeQuery(((QueryDescriptor) object).getDataMap(), (QueryDescriptor) object);
        } else if (object instanceof Procedure) {
            undo = new RemoveUndoableEdit(((Procedure) object).getDataMap(), (Procedure) object);
            procedureService.removeProcedure(((Procedure) object).getDataMap(), (Procedure) object);
        } else if (object instanceof Embeddable) {
            undo = new RemoveUndoableEdit(((Embeddable) object).getDataMap(), (Embeddable) object);
            embeddableService.removeEmbeddable(((Embeddable) object).getDataMap(), (Embeddable) object);
        }

        return undo;
    }
}
