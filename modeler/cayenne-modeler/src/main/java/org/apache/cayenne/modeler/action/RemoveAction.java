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

import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.event.*;
import org.apache.cayenne.map.*;
import org.apache.cayenne.map.event.*;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.ConfirmRemoveDialog;
import org.apache.cayenne.modeler.editor.CallbackType;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.event.CallbackMethodEvent;
import org.apache.cayenne.modeler.undo.*;
import org.apache.cayenne.modeler.util.CayenneAction;
import org.apache.cayenne.modeler.util.ProjectUtil;

import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Removes currently selected object from the project. This can be Domain, DataNode,
 * Entity, Attribute or Relationship.
 */
public class RemoveAction extends CayenneAction {

    public static String getActionName() {
        return "Remove";
    }

    public RemoveAction(Application application) {
        super(getActionName(), application);
    }

    protected RemoveAction(String actionName, Application application) {
        super(actionName, application);
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
                removeObjEntity(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getObjEntity());
            }
        } else if (mediator.getCurrentState().getDbEntity() != null) {
            if (dialog.shouldDelete("DbEntity", mediator.getCurrentState().getDbEntity().getName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getDbEntity()));
                removeDbEntity(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getDbEntity());
            }
        } else if (mediator.getCurrentState().getQuery() != null) {
            if (dialog.shouldDelete("query", mediator.getCurrentState().getQuery().getName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getQuery()));
                removeQuery(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getQuery());
            }
        } else if (mediator.getCurrentState().getProcedure() != null) {
            if (dialog.shouldDelete("procedure", mediator.getCurrentState().getProcedure().getName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getProcedure()));
                removeProcedure(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getProcedure());
            }
        } else if (mediator.getCurrentState().getEmbeddable() != null) {
            if (dialog.shouldDelete("embeddable", mediator.getCurrentState().getEmbeddable().getClassName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getEmbeddable()));
                removeEmbeddable(mediator.getCurrentState().getDataMap(), mediator.getCurrentState().getEmbeddable());
            }
        } else if (mediator.getCurrentState().getDataMap() != null) {
            if (dialog.shouldDelete("data map", mediator.getCurrentState().getDataMap().getName())) {

                // In context of Data node just remove from Data Node
                if (mediator.getCurrentState().getNode() != null) {
                    application.getUndoManager()
                            .addEdit(new RemoveUndoableEdit(application, mediator.getCurrentState().getNode(),
                                    mediator.getCurrentState().getDataMap()));
                    removeDataMapFromDataNode(mediator.getCurrentState().getNode(), mediator.getCurrentState().getDataMap());
                } else {
                    // Not under Data Node, remove completely
                    application.getUndoManager()
                            .addEdit(new RemoveUndoableEdit(application, mediator.getCurrentState().getDataMap()));
                    removeDataMap(mediator.getCurrentState().getDataMap());
                }
            }
        } else if (mediator.getCurrentState().getNode() != null) {
            if (dialog.shouldDelete("data node", mediator.getCurrentState().getNode().getName())) {

                application.getUndoManager()
                        .addEdit(new RemoveUndoableEdit(application, mediator.getCurrentState().getNode()));
                removeDataNode(mediator.getCurrentState().getNode());
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
        	removeProcedureParameters(mediator.getCurrentState().getProcedure(), mediator.getCurrentState().getProcedureParameters());
        }

    }

    private void removeProcedureParameters(Procedure procedure, ProcedureParameter[] parameters) {
        ProjectController mediator = getProjectController();
        for (ProcedureParameter parameter : parameters) {
            procedure.removeCallParameter(parameter.getName());
            ProcedureParameterEvent e = new ProcedureParameterEvent(Application.getFrame(), parameter, MapEvent.REMOVE);
            mediator.fireEvent(e);
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

                for (EmbeddableAttribute attrib : embAttrs) {
                    embeddable.removeAttribute(attrib.getName());
                    EmbeddableAttributeEvent e = new EmbeddableAttributeEvent(Application.getFrame(),
                            attrib, embeddable, MapEvent.REMOVE);
                    mediator.fireEvent(e);
                }

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

                for (ObjAttribute attrib : objAttrs) {
                    entity.removeAttribute(attrib.getName());
                    ObjAttributeEvent e = new ObjAttributeEvent(Application.getFrame(), attrib, entity, MapEvent.REMOVE);
                    mediator.fireEvent(e);
                }

                ProjectUtil.cleanObjMappings(mediator.getCurrentState().getDataMap());
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

                for (DbAttribute attrib : dbAttrs) {
                    entity.removeAttribute(attrib.getName());
                    DbAttributeEvent e = new DbAttributeEvent(Application.getFrame(), attrib, entity, MapEvent.REMOVE);
                    mediator.fireEvent(e);
                }

                ProjectUtil.cleanObjMappings(mediator.getCurrentState().getDataMap());
        	}
    	}
    }
    
    private void removeDBRelationships(ProjectController mediator, ConfirmRemoveDialog dialog,
                                       DbRelationship[] dbRels) {
		if (dbRels != null && dbRels.length > 0) {
			if ((dbRels.length == 1 && dialog.shouldDelete("DbRelationship", dbRels[0].getName()))
					|| (dbRels.length > 1 && dialog.shouldDelete("selected DbRelationships"))) {
				DbEntity entity = mediator.getCurrentState().getDbEntity();
				
				for (DbRelationship rel : dbRels) {
					entity.removeRelationship(rel.getName());
					DbRelationshipEvent e = new DbRelationshipEvent(Application.getFrame(), rel, entity, MapEvent.REMOVE);
					mediator.fireEvent(e);
				}

				ProjectUtil.cleanObjMappings(mediator.getCurrentState().getDataMap());
				Application.getInstance().getUndoManager().addEdit(new RemoveRelationshipUndoableEdit(entity, dbRels));
			}
		}
	}

	private void removeObjRelationships(ProjectController mediator, ConfirmRemoveDialog dialog,
                                        ObjRelationship[] rels) {
		if ((rels.length == 1 && dialog.shouldDelete("ObjRelationship", rels[0].getName()))
				|| (rels.length > 1 && dialog.shouldDelete("selected ObjRelationships"))) {
			ObjEntity entity = mediator.getCurrentState().getObjEntity();
			for (ObjRelationship rel : rels) {
				entity.removeRelationship(rel.getName());
				ObjRelationshipEvent e = new ObjRelationshipEvent(Application.getFrame(), rel, entity, MapEvent.REMOVE);
				mediator.fireEvent(e);
			}
			Application.getInstance().getUndoManager().addEdit(new RemoveRelationshipUndoableEdit(entity, rels));
		}		
	}

	private void removeMethods(ProjectController mediator, ConfirmRemoveDialog dialog, ObjCallbackMethod[] methods) {
    	CallbackMap callbackMap = mediator.getCurrentState().getObjEntity().getCallbackMap();
    	CallbackType callbackType = mediator.getCurrentState().getCallbackType();

        if ((methods.length == 1 && dialog.shouldDelete("callback method", methods[0].getName()))
        	|| (methods.length > 1 && dialog.shouldDelete("selected callback methods"))) {
            for (ObjCallbackMethod callbackMethod : methods) {
            	callbackMap.getCallbackDescriptor(callbackType.getType()).removeCallbackMethod(callbackMethod.getName());
                    
                CallbackMethodEvent ce = new CallbackMethodEvent(this, null,
                        callbackMethod.getName(),
                        MapEvent.REMOVE);
                    
                mediator.fireEvent(ce);
            }
            
            Application.getInstance().getUndoManager()
                    .addEdit(new RemoveCallbackMethodUndoableEdit(callbackType, methods));
        }		
	}

	public void removeDataMap(DataMap map) {
        ProjectController mediator = getProjectController();
        DataChannelDescriptor domain = (DataChannelDescriptor) mediator.getProject().getRootNode();
        DataMapEvent e = new DataMapEvent(Application.getFrame(), map, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) mediator.getProject().getRootNode());

        domain.getDataMaps().remove(map);
        if (map.getConfigurationSource() != null) {
            URL mapURL = map.getConfigurationSource().getURL();
            Collection<URL> unusedResources = getCurrentProject().getUnusedResources();
            unusedResources.add(mapURL);
        }

        for (DataNodeDescriptor node : domain.getNodeDescriptors()) {
            if (node.getDataMapNames().contains(map.getName())) {
                removeDataMapFromDataNode(node, map);
            }
        }
       
        mediator.fireEvent(e);
    }

    public void removeDataNode(DataNodeDescriptor node) {
        ProjectController mediator = getProjectController();
        DataChannelDescriptor domain = (DataChannelDescriptor) mediator.getProject().getRootNode();
        DataNodeEvent e = new DataNodeEvent(Application.getFrame(), node, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) mediator.getProject().getRootNode());

        domain.getNodeDescriptors().remove(node);
        mediator.fireEvent(e);
    }

    /**
     * Removes current DbEntity from its DataMap and fires "remove" EntityEvent.
     */
    public void removeDbEntity(DataMap map, DbEntity ent) {
        ProjectController mediator = getProjectController();

        DbEntityEvent e = new DbEntityEvent(Application.getFrame(), ent, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) mediator.getProject().getRootNode());

        map.removeDbEntity(ent.getName(), true);
        mediator.fireEvent(e);
    }

    /**
     * Removes current Query from its DataMap and fires "remove" QueryEvent.
     */
    public void removeQuery(DataMap map, QueryDescriptor query) {
        ProjectController mediator = getProjectController();

        QueryEvent e = new QueryEvent(Application.getFrame(), query, MapEvent.REMOVE, map);
        e.setDomain((DataChannelDescriptor) mediator.getProject().getRootNode());

        map.removeQueryDescriptor(query.getName());
        mediator.fireEvent(e);
    }

    /**
     * Removes current Procedure from its DataMap and fires "remove" ProcedureEvent.
     */
    public void removeProcedure(DataMap map, Procedure procedure) {
        ProjectController mediator = getProjectController();

        ProcedureEvent e = new ProcedureEvent(Application.getFrame(), procedure, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) mediator.getProject().getRootNode());

        map.removeProcedure(procedure.getName());
        mediator.fireEvent(e);
    }

    /**
     * Removes current object entity from its DataMap.
     */
    public void removeObjEntity(DataMap map, ObjEntity entity) {
        ProjectController mediator = getProjectController();

        ObjEntityEvent e = new ObjEntityEvent(Application.getFrame(), entity, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) mediator.getProject().getRootNode());

        map.removeObjEntity(entity.getName(), true);
        mediator.fireEvent(e);

        // remove queries that depend on entity
        // TODO: (Andrus, 09/09/2005) show warning dialog?

        // clone to be able to remove within iterator...
        for (QueryDescriptor query : new ArrayList<>(map.getQueryDescriptors())) {
            if (!QueryDescriptor.EJBQL_QUERY.equals(query.getType())) {
                Object root = query.getRoot();

                if (root == entity || (root instanceof String && root.toString().equals(entity.getName()))) {
                    removeQuery(map, query);
                }
            }
        }
    }

    public void removeEmbeddable(DataMap map, Embeddable embeddable) {
        ProjectController mediator = getProjectController();

        EmbeddableEvent e = new EmbeddableEvent(Application.getFrame(), embeddable, MapEvent.REMOVE, map);
        e.setDomain((DataChannelDescriptor) mediator.getProject().getRootNode());

        map.removeEmbeddable(embeddable.getClassName());
        mediator.fireEvent(e);
    }

    public void removeDataMapFromDataNode(DataNodeDescriptor node, DataMap map) {
        ProjectController mediator = getProjectController();

        DataNodeEvent e = new DataNodeEvent(Application.getFrame(), node);
        e.setDomain((DataChannelDescriptor) mediator.getProject().getRootNode());

        node.getDataMapNames().remove(map.getName());

        // Force reloading of the data node in the browse view
        mediator.fireEvent(e);
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
                removeDataMapFromDataNode((DataNodeDescriptor) parentObject, (DataMap) object);
            } else {
                // Not under Data Node, remove completely
                undo = new RemoveUndoableEdit(application, (DataMap) object);
                removeDataMap((DataMap) object);
            }
        } else if (object instanceof DataNodeDescriptor) {
            undo = new RemoveUndoableEdit(application, (DataNodeDescriptor) object);
            removeDataNode((DataNodeDescriptor) object);
        } else if (object instanceof DbEntity) {
            undo = new RemoveUndoableEdit(((DbEntity) object).getDataMap(), (DbEntity) object);
            removeDbEntity(((DbEntity) object).getDataMap(), (DbEntity) object);
        } else if (object instanceof ObjEntity) {
            undo = new RemoveUndoableEdit(((ObjEntity) object).getDataMap(), (ObjEntity) object);
            removeObjEntity(((ObjEntity) object).getDataMap(), (ObjEntity) object);
        } else if (object instanceof QueryDescriptor) {
            undo = new RemoveUndoableEdit(((QueryDescriptor) object).getDataMap(), (QueryDescriptor) object);
            removeQuery(((QueryDescriptor) object).getDataMap(), (QueryDescriptor) object);
        } else if (object instanceof Procedure) {
            undo = new RemoveUndoableEdit(((Procedure) object).getDataMap(), (Procedure) object);
            removeProcedure(((Procedure) object).getDataMap(), (Procedure) object);
        } else if (object instanceof Embeddable) {
            undo = new RemoveUndoableEdit(((Embeddable) object).getDataMap(), (Embeddable) object);
            removeEmbeddable(((Embeddable) object).getDataMap(), (Embeddable) object);
        }

        return undo;
    }
}
