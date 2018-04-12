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
import org.apache.cayenne.dbsync.filter.NamePatternMatcher;
import org.apache.cayenne.dbsync.merge.context.EntityMergeSupport;
import org.apache.cayenne.dbsync.naming.ObjectNameGenerator;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.ConfirmMeaningfulFKsEvent;
import org.apache.cayenne.modeler.event.DbEntitySyncEvent;
import org.apache.cayenne.modeler.services.util.PreserveRelationshipNameGenerator;

import java.util.Collection;
import java.util.Collections;

/**
 * @since 4.1
 */
public class EntitySyncService {

    @Inject
    private NameGeneratorService nameGeneratorService;

    @Inject
    public ProjectController projectController;

    @Inject
    private DbEntityService dbEntityService;

    @Inject
    private ObjEntityService objEntityService;

    private DbEntity dbEntity;
    private ObjEntity objEntity;

    public EntityMergeSupport createMerger() {
        Collection<ObjEntity> entities = getObjEntities();
        if (entities.isEmpty()) {
            return null;
        }

        ObjectNameGenerator namingStrategy;
        try {
            namingStrategy = nameGeneratorService.createNamingStrategy();
        } catch (Throwable e) {
            namingStrategy = nameGeneratorService.defaultNameGenerator();
        }

        // TODO: Modeler-controlled defaults for all the hardcoded boolean flags here.
        EntityMergeSupport merger = new EntityMergeSupport(namingStrategy, NamePatternMatcher.EXCLUDE_ALL, true, true, false);

        ConfirmMeaningfulFKsEvent confirmMeaningfulFKsEvent = new ConfirmMeaningfulFKsEvent(this, namingStrategy, merger);

        // see if we need to remove meaningful attributes...
        for (ObjEntity entity : entities) {
            if (!merger.getMeaningfulFKs(entity).isEmpty()) {
                projectController.fireEvent(confirmMeaningfulFKsEvent);
                return confirmMeaningfulFKsEvent.getMerger();
            }
        }

        return merger;
    }

    protected Collection<ObjEntity> getObjEntities() {
        return objEntity == null ? dbEntity.getDataMap().getMappedEntities(dbEntity)
                : Collections.singleton(objEntity);
    }

    public void setDbEntity(DbEntity dbEntity) {
        this.dbEntity = dbEntity;
    }

    public void syncDbEntity() {
        DbEntity dbEntity = projectController.getCurrentState().getDbEntity();

        if (dbEntity != null) {

            Collection<ObjEntity> entities = dbEntity.getDataMap().getMappedEntities(dbEntity);
            if (entities.isEmpty()) {
                return;
            }

            setDbEntity(dbEntity);
            EntityMergeSupport merger = createMerger();

            if (merger == null) {
                return;
            }

            merger.setNameGenerator(new PreserveRelationshipNameGenerator());

            // filter out inherited entities, as we need to add attributes only to the roots
            dbEntityService.filterInheritedEntities(entities);

            projectController.fireEvent(new DbEntitySyncEvent(this, entities, merger));
        }
    }

    public void syncObjEntity() {
        ObjEntity entity = projectController.getCurrentState().getObjEntity();

        if (entity != null && entity.getDbEntity() != null) {
            setObjEntity(entity);
            EntityMergeSupport merger = createMerger();

            if (merger == null) {
                return;
            }

            merger.setNameGenerator(new PreserveRelationshipNameGenerator());

            if (merger.synchronizeWithDbEntity(entity)) {
                objEntityService.syncObjEntity(entity);
            }
        }
    }

    public void setObjEntity(ObjEntity objEntity) {
        this.objEntity = objEntity;
    }
}
