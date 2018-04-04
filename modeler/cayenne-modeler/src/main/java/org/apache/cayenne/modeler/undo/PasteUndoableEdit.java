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
package org.apache.cayenne.modeler.undo;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.action.RemoveAction;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.services.*;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class PasteUndoableEdit extends CayenneUndoableEdit {

    private DataChannelDescriptor domain;
    private DataMap map;
    private Object where;
    private Object content;

    public PasteUndoableEdit(DataChannelDescriptor domain, DataMap map, Object where,
            Object content) {
        this.domain = domain;
        this.map = map;
        this.where = where;
        this.content = content;
    }

    @Override
    public String getPresentationName() {

        String className = this.content.getClass().getName();
        int pos = className.lastIndexOf(".");
        String contentName = className.substring(pos + 1);

        return "Paste " + contentName;
    }

    @Override
    public void redo() throws CannotRedoException {
        PasteService pasteService = controller.getBootiqueInjector().getInstance(PasteService.class);
        pasteService.paste(where, content, domain, map);
    }

    @Override
    public void undo() throws CannotUndoException {
        AttributeService attributeService = controller.getBootiqueInjector().getInstance(AttributeService.class);

        RemoveAction rAction = actionManager.getAction(RemoveAction.class);

        DbEntityService dbEntityService = controller.getBootiqueInjector().getInstance(DbEntityService.class);

        ObjEntityService objEntityService = controller.getBootiqueInjector().getInstance(ObjEntityService.class);

        ProcedureService procedureService = controller.getBootiqueInjector().getInstance(ProcedureService.class);

        DataMapService dataMapService = controller.getBootiqueInjector().getInstance(DataMapService.class);

        EmbeddableService embeddableService = controller.getBootiqueInjector().getInstance(EmbeddableService.class);

        RelationshipService relationshipService = controller.getBootiqueInjector().getInstance(RelationshipService.class);

        CallbackMethodService callbackMethodService = controller.getBootiqueInjector().getInstance(CallbackMethodService.class);

        QueryService queryService = controller.getBootiqueInjector().getInstance(QueryService.class);

        ProcedureParameterService procedureParameterService = controller.getBootiqueInjector().getInstance(ProcedureParameterService.class);

        if (content instanceof DataMap) {
            if (where instanceof DataChannelDescriptor) {
                dataMapService.removeDataMap((DataMap) content);
            } else if (where instanceof DataNodeDescriptor) {
                dataMapService.removeDataMapFromDataNode(
                        (DataNodeDescriptor) where,
                        (DataMap) content);
            }
        } else if (where instanceof DataMap) {
            if (content instanceof DbEntity) {
                dbEntityService.removeDbEntity(map, (DbEntity) content);
            } else if (content instanceof ObjEntity) {
                objEntityService.removeObjEntity(map, (ObjEntity) content);
            } else if (content instanceof Embeddable) {
                embeddableService.removeEmbeddable(map, (Embeddable) content);
            } else if (content instanceof QueryDescriptor) {
                queryService.removeQuery(map, (QueryDescriptor) content);
            } else if (content instanceof Procedure) {
                procedureService.removeProcedure(map, (Procedure) content);
            }
        } else if (where instanceof DbEntity) {
            if (content instanceof DbEntity) {
                dbEntityService.removeDbEntity(map, (DbEntity) content);
            } else if (content instanceof DbAttribute) {
                attributeService.removeDbAttributes(
                        map,
                        (DbEntity) where,
                        new DbAttribute[] {
                            (DbAttribute) content
                        });
            } else if (content instanceof DbRelationship) {
                relationshipService.removeDbRelationships(
                        (DbEntity) where,
                        new DbRelationship[] {
                            (DbRelationship) content
                        });
            }
        } else if (where instanceof ObjEntity) {
            if (content instanceof ObjEntity) {
                objEntityService.removeObjEntity(map, (ObjEntity) content);
            } else if (content instanceof ObjAttribute) {
                attributeService.removeObjAttributes(
                        (ObjEntity) where,
                        new ObjAttribute[] {
                            (ObjAttribute) content
                        });
            } else if (content instanceof ObjRelationship) {
                relationshipService.removeObjRelationships(
                        (ObjEntity) where,
                        new ObjRelationship[] {
                            (ObjRelationship) content
                        });
            } else if (content instanceof ObjCallbackMethod) {
            		ObjCallbackMethod[] methods = new ObjCallbackMethod[] {
                            (ObjCallbackMethod) content };
            		for(ObjCallbackMethod callbackMethod : methods) {
                        callbackMethodService.removeCallbackMethod(
	                			methods[0].getCallbackType(), 
	                			callbackMethod.getName());
            		}
            }
        } else if (where instanceof Procedure) {
            final Procedure procedure = (Procedure) where;
            if (content instanceof ProcedureParameter) {
                procedureParameterService.removeProcedureParameters(
                        procedure,
                        new ProcedureParameter[] {
                            (ProcedureParameter) content
                        });
            }
        } else if (where instanceof Embeddable) {
            if (content instanceof Embeddable) {
                embeddableService.removeEmbeddable(map, (Embeddable) content);
            } else if (content instanceof EmbeddableAttribute) {
                attributeService.removeEmbeddableAttributes((Embeddable) where,
                        new EmbeddableAttribute[]{(EmbeddableAttribute) content});
            }
        }
    }
}
