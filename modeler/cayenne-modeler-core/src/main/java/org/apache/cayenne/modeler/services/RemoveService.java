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
package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.RemoveDataMapEvent;
import org.apache.cayenne.modeler.event.RemoveDataMapFromDataNodeEvent;
import org.apache.cayenne.modeler.event.RemoveDataNodeEvent;
import org.apache.cayenne.modeler.event.RemoveDbEntityEvent;
import org.apache.cayenne.modeler.event.RemoveEmbeddableEvent;
import org.apache.cayenne.modeler.event.RemoveObjEntityEvent;
import org.apache.cayenne.modeler.event.RemovePathsEvent;
import org.apache.cayenne.modeler.event.RemoveProcedureEvent;
import org.apache.cayenne.modeler.event.RemoveQueryEvent;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;

/**
 * @since 4.1
 */
public class RemoveService {

    @Inject
    public ProjectController projectController;

    @Inject
    public ObjEntityService objEntityService;

    @Inject
    public DbEntityService dbEntityService;

    @Inject
    public QueryService queryService;

    @Inject
    public ProcedureService procedureService;

    @Inject
    public EmbeddableService embeddableService;

    @Inject
    public DataMapService dataMapService;

    @Inject
    public NodeService nodeService;

    @Inject
    public CallbackMethodService callbackMethodService;

    @Inject
    public RelationshipService relationshipService;

    public RemoveServiceStatus isRemove() {
        if (projectController.getCurrentState().getObjEntity() != null) {
            return new RemoveServiceStatus("ObjEntity", projectController.getCurrentState().getObjEntity().getName());
        } else if (projectController.getCurrentState().getDbEntity() != null) {
            return new RemoveServiceStatus("DbEntity", projectController.getCurrentState().getDbEntity().getName());
        } else if (projectController.getCurrentState().getQuery() != null) {
            return new RemoveServiceStatus("query", projectController.getCurrentState().getQuery().getName());
        } else if (projectController.getCurrentState().getProcedure() != null) {
            return new RemoveServiceStatus("procedure", projectController.getCurrentState().getProcedure().getName());
        } else if (projectController.getCurrentState().getEmbeddable() != null) {
            return new RemoveServiceStatus("embeddable", projectController.getCurrentState().getEmbeddable().getClassName());
        } else if (projectController.getCurrentState().getDataMap() != null) {
            return new RemoveServiceStatus("data map", projectController.getCurrentState().getDataMap().getName());
        } else if (projectController.getCurrentState().getNode() != null) {
            return new RemoveServiceStatus("data node", projectController.getCurrentState().getNode().getName());
        } else if (projectController.getCurrentState().getPaths() != null) {
            return new RemoveServiceStatus(null, "selected object");
        }

        return null;
    }

    public void remove() {
        if(projectController.getCurrentState().getObjEntity() != null) {
            projectController.fireEvent(new RemoveObjEntityEvent(this, projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getObjEntity()));
            objEntityService.removeObjEntity(projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getObjEntity());
        } else if (projectController.getCurrentState().getDbEntity() != null) {
            projectController.fireEvent(new RemoveDbEntityEvent(this, projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getDbEntity()));
            dbEntityService.removeDbEntity(projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getDbEntity());
        } else if (projectController.getCurrentState().getQuery() != null) {
            projectController.fireEvent(new RemoveQueryEvent(this, projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getQuery()));
            queryService.removeQuery(projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getQuery());
        } else if (projectController.getCurrentState().getProcedure() != null) {
            projectController.fireEvent(new RemoveProcedureEvent(this, projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getProcedure()));
            procedureService.removeProcedure(projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getProcedure());
        } else if (projectController.getCurrentState().getEmbeddable() != null) {
            projectController.fireEvent(new RemoveEmbeddableEvent(this, projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getEmbeddable()));
            embeddableService.removeEmbeddable(projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getEmbeddable());
        } else if (projectController.getCurrentState().getDataMap() != null) {
            if (projectController.getCurrentState().getNode() != null) {
                projectController.fireEvent(new RemoveDataMapFromDataNodeEvent(this, projectController.getCurrentState().getDataMap(), projectController.getCurrentState().getNode()));
                dataMapService.removeDataMapFromDataNode(projectController.getCurrentState().getNode(), projectController.getCurrentState().getDataMap());
            } else {
                // Not under Data Node, remove completely
                projectController.fireEvent(new RemoveDataMapEvent(this, projectController.getCurrentState().getDataMap()));
                dataMapService.removeDataMap(projectController.getCurrentState().getDataMap());
            }
        } else if (projectController.getCurrentState().getNode() != null) {
            projectController.fireEvent(new RemoveDataNodeEvent(this, projectController.getCurrentState().getNode()));
            nodeService.removeDataNode(projectController.getCurrentState().getNode());
        } else if (projectController.getCurrentState().getPaths() != null) {
            projectController.fireEvent(new RemovePathsEvent(this, projectController));
        }
    }

}
