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
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.*;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.event.CallbackMethodEvent;
import org.apache.cayenne.query.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 4.1
 */
public class DefaultPasteService implements PasteService {

    private static final String COPY_PATTERN = "Copy of %s (%d)";

    @Inject
    public ProjectController projectController;

    @Inject
    public DbEntityService dbEntityService;
    @Inject
    public ObjEntityService objEntityService;
    @Inject
    public EmbeddableService embeddableService;
    @Inject
    public ProcedureService procedureService;
    @Inject
    public AttributeService attributeService;
    @Inject
    public RelationshipService relationshipService;
    @Inject
    public ProcedureParameterService procedureParameterService;
    @Inject
    public QueryService queryService;
    @Override
    public void paste(Object where, Object content) {
        paste(where, content, (DataChannelDescriptor) projectController
                .getProject()
                .getRootNode(), projectController.getCurrentState().getDataMap());
    }


    /**
     * Pastes single object
     */
    @Override
    public void paste(
            Object where,
            Object content,
            DataChannelDescriptor dataChannelDescriptor,
            DataMap map) {
        final ProjectController mediator = projectController;

        /**
         * Add a little intelligence - if a tree leaf is selected, we can paste to a
         * parent datamap
         */
        if (isTreeLeaf(where) && isTreeLeaf(content)) {
            where = mediator.getCurrentState().getDataMap();
        }

        if ((where instanceof DataChannelDescriptor || where instanceof DataNodeDescriptor)
                && content instanceof DataMap) {
            // paste DataMap to DataDomain or DataNode
            DataMap dataMap = ((DataMap) content);

            dataMap.setName(NameBuilder
                    .builder(dataMap, dataChannelDescriptor)
                    .baseName(dataMap.getName())
                    .dupesPattern(COPY_PATTERN)
                    .name());

            /**
             * Update all names in the new DataMap, so that they would not conflict with
             * names from other datamaps of this domain
             */

            // add some intelligence - if we rename an entity, we should rename all links
            // to it as well
            Map<String, String> renamedDbEntities = new HashMap<>();
            Map<String, String> renamedObjEntities = new HashMap<>();
            Map<String, String> renamedEmbeddables = new HashMap<>();

            for (DbEntity dbEntity : dataMap.getDbEntities()) {
                String oldName = dbEntity.getName();
                dbEntity.setName(NameBuilder
                        .builder(dbEntity, dataMap)
                        .baseName(dbEntity.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                if (!oldName.equals(dbEntity.getName())) {
                    renamedDbEntities.put(oldName, dbEntity.getName());
                }
            }

            for (ObjEntity objEntity : dataMap.getObjEntities()) {
                String oldName = objEntity.getName();
                objEntity.setName(NameBuilder
                        .builder(objEntity, dataMap)
                        .baseName(objEntity.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                if (!oldName.equals(objEntity.getName())) {
                    renamedObjEntities.put(oldName, objEntity.getName());
                }
            }

            for (Embeddable embeddable : dataMap.getEmbeddables()) {
                String oldName = embeddable.getClassName();
                embeddable.setClassName(NameBuilder
                        .builder(embeddable, dataMap)
                        .baseName(embeddable.getClassName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                if (!oldName.equals(embeddable.getClassName())) {
                    renamedEmbeddables.put(oldName, embeddable.getClassName());
                }
            }

            for (Procedure procedure : dataMap.getProcedures()) {
                procedure.setName(NameBuilder
                        .builder(procedure, dataMap)
                        .baseName(procedure.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());
            }

            for (QueryDescriptor query : dataMap.getQueryDescriptors()) {
                query.setName(NameBuilder.builder(query, dataMap)
                        .baseName(query.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());
            }

            // if an entity was renamed, we rename all links to it too
            for (DbEntity dbEntity : dataMap.getDbEntities()) {
                for (DbRelationship rel : dbEntity.getRelationships()) {
                    if (renamedDbEntities.containsKey(rel.getTargetEntityName())) {
                        rel.setTargetEntityName(renamedDbEntities.get(rel
                                .getTargetEntityName()));
                    }
                }
            }
            for (ObjEntity objEntity : dataMap.getObjEntities()) {
                if (renamedDbEntities.containsKey(objEntity.getDbEntityName())) {
                    objEntity.setDbEntityName(renamedDbEntities.get(objEntity
                            .getDbEntityName()));
                }

                if (renamedObjEntities.containsKey(objEntity.getSuperEntityName())) {
                    objEntity.setSuperEntityName(renamedDbEntities.get(objEntity
                            .getSuperEntityName()));
                }

                for (ObjRelationship rel : objEntity.getRelationships()) {
                    if (renamedObjEntities.containsKey(rel.getTargetEntityName())) {
                        rel.setTargetEntityName(renamedObjEntities.get(rel
                                .getTargetEntityName()));
                    }
                }
            }

            mediator.addDataMap(this, dataMap);
        } else if (where instanceof DataMap) {
            // paste DbEntity to DataMap
            final DataMap dataMap = ((DataMap) where);

            // clear data map parent cache
            clearDataMapCache(dataMap);

            if (content instanceof DbEntity) {
                DbEntity dbEntity = (DbEntity) content;
                dbEntity.setName(NameBuilder
                        .builder(dbEntity, dataMap)
                        .baseName(dbEntity.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                dataMap.addDbEntity(dbEntity);
                dbEntityService.fireDbEntityEvent(this, mediator, dbEntity);
            } else if (content instanceof ObjEntity) {
                // paste ObjEntity to DataMap
                ObjEntity objEntity = (ObjEntity) content;
                objEntity.setName(NameBuilder.builder(objEntity, dataMap)
                        .baseName(objEntity.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                dataMap.addObjEntity(objEntity);
                objEntityService.fireObjEntityEvent(
                        this,
                        mediator,
                        dataMap,
                        objEntity);
            } else if (content instanceof Embeddable) {
                // paste Embeddable to DataMap
                Embeddable embeddable = (Embeddable) content;
                embeddable.setClassName(NameBuilder
                        .builder(embeddable, dataMap)
                        .baseName(embeddable.getClassName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                dataMap.addEmbeddable(embeddable);
                embeddableService.fireEmbeddableEvent(
                        this,
                        mediator,
                        dataMap,
                        embeddable);
            } else if (content instanceof QueryDescriptor) {
                QueryDescriptor query = (QueryDescriptor) content;

                query.setName(NameBuilder
                        .builder(query, dataMap)
                        .dupesPattern(COPY_PATTERN)
                        .baseName(query.getName())
                        .name());
                query.setDataMap(dataMap);

                dataMap.addQueryDescriptor(query);
                queryService.fireQueryEvent(this, mediator, dataChannelDescriptor, dataMap, query);
            } else if (content instanceof Procedure) {
                // paste Procedure to DataMap
                Procedure procedure = (Procedure) content;
                procedure.setName(NameBuilder
                        .builder(procedure, dataMap)
                        .dupesPattern(COPY_PATTERN)
                        .baseName(procedure.getName())
                        .name());

                dataMap.addProcedure(procedure);
                procedureService.fireProcedureEvent(
                        this,
                        mediator,
                        dataMap,
                        procedure);
            }
        } else if (where instanceof DbEntity) {
            final DbEntity dbEntity = (DbEntity) where;

            if (content instanceof DbAttribute) {
                DbAttribute attr = (DbAttribute) content;
                attr.setName(NameBuilder
                        .builder(attr, dbEntity)
                        .dupesPattern(COPY_PATTERN)
                        .baseName(attr.getName())
                        .name());

                dbEntity.addAttribute(attr);
                attributeService.fireDbAttributeEvent(this, mediator, mediator
                        .getCurrentState()
                        .getDataMap(), dbEntity, attr);
            } else if (content instanceof DbRelationship) {
                DbRelationship rel = (DbRelationship) content;
                rel.setName(NameBuilder
                        .builder(rel, dbEntity)
                        .baseName(rel.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                dbEntity.addRelationship(rel);
                relationshipService.fireDbRelationshipEvent(
                        this,
                        mediator,
                        dbEntity,
                        rel);
            }
        } else if (where instanceof ObjEntity) {
            ObjEntity objEntity = (ObjEntity) where;

            if (content instanceof ObjAttribute) {
                ObjAttribute attr = (ObjAttribute) content;
                attr.setName(NameBuilder
                        .builder(attr, objEntity)
                        .baseName(attr.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                objEntity.addAttribute(attr);
                attributeService.fireObjAttributeEvent(this, mediator, mediator
                        .getCurrentState()
                        .getDataMap(), objEntity, attr);
            } else if (content instanceof ObjRelationship) {
                ObjRelationship rel = (ObjRelationship) content;
                rel.setName(NameBuilder
                        .builder(rel, objEntity)
                        .baseName(rel.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                objEntity.addRelationship(rel);
                relationshipService.fireObjRelationshipEvent(
                        this,
                        mediator,
                        objEntity,
                        rel);
            } else if (content instanceof ObjCallbackMethod) {
                ObjCallbackMethod method = (ObjCallbackMethod) content;

                method.setName(NameBuilder
                        .builderForCallbackMethod(objEntity)
                        .baseName(method.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                objEntity.getCallbackMap()
                        .getCallbackDescriptor(mediator.getCurrentState().getCallbackType().getType())
                        .addCallbackMethod(method.getName());

                CallbackMethodEvent ce = new CallbackMethodEvent(
                        this,
                        null,
                        method.getName(),
                        MapEvent.ADD);

                projectController.fireEvent(ce);
            }
        } else if (where instanceof Embeddable) {
            final Embeddable embeddable = (Embeddable) where;

            if (content instanceof EmbeddableAttribute) {
                EmbeddableAttribute attr = (EmbeddableAttribute) content;
                attr.setName(NameBuilder
                        .builder(attr, embeddable)
                        .baseName(attr.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                embeddable.addAttribute(attr);
                attributeService.fireEmbeddableAttributeEvent(
                        this,
                        mediator,
                        embeddable,
                        attr);
            }

        } else if (where instanceof Procedure) {
            // paste param to procedure
            final Procedure procedure = (Procedure) where;

            if (content instanceof ProcedureParameter) {
                ProcedureParameter param = (ProcedureParameter) content;

                param.setName(NameBuilder
                        .builder(param, procedure)
                        .baseName(param.getName())
                        .dupesPattern(COPY_PATTERN)
                        .name());

                procedure.addCallParameter(param);
                procedureParameterService.fireProcedureParameterEvent(
                        this,
                        mediator,
                        procedure,
                        param);
            }

        }
    }

    /**
     * @return true if the object is in a lowest level of the tree
     */
    public boolean isTreeLeaf(Object content) {
        return content instanceof DbEntity
                || content instanceof ObjEntity
                || content instanceof Embeddable
                || content instanceof Procedure
                || content instanceof Query;
    }

    private void clearDataMapCache(DataMap dataMap) {
        MappingNamespace ns = dataMap.getNamespace();
        if (ns instanceof EntityResolver) {
            ((EntityResolver) ns).refreshMappingCache();
        }
    }
}
