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
import org.apache.cayenne.configuration.event.DbEntityEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateDbEntityEvent;
import org.apache.cayenne.modeler.event.DbEntityDisplayEvent;

import java.util.Collection;

/**
 * @since 4.1
 */
public class DefaultDbEntityService implements DbEntityService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createDbEntity() {
        DataMap map = projectController.getCurrentState().getDataMap();
        DbEntity entity = new DbEntity();
        entity.setName(NameBuilder.builder(entity, map).name());

        entity.setCatalog(map.getDefaultCatalog());
        entity.setSchema(map.getDefaultSchema());
        map.addDbEntity(entity);

        projectController.fireEvent(new DbEntityEvent(this, entity, MapEvent.ADD));
        DbEntityDisplayEvent displayEvent = new DbEntityDisplayEvent(this, entity, projectController.getCurrentState().getDataMap(),
                projectController.getCurrentState().getNode(), (DataChannelDescriptor) projectController.getProject().getRootNode());
        displayEvent.setMainTabFocus(true);
        projectController.fireEvent(displayEvent);

        projectController.fireEvent(new CreateDbEntityEvent(this, map, entity));
    }

    public void createEntity(DataMap map, DbEntity entity) {
        entity.setCatalog(map.getDefaultCatalog());
        entity.setSchema(map.getDefaultSchema());
        map.addDbEntity(entity);
        fireDbEntityEvent(this, projectController, entity);
    }

    /**
     * Removes current DbEntity from its DataMap and fires "remove" EntityEvent.
     */
    public void removeDbEntity(DataMap map, DbEntity ent) {

        DbEntityEvent e = new DbEntityEvent(this, ent, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        map.removeDbEntity(ent.getName(), true);
        projectController.fireEvent(e);
    }

    @Override
    public void fireDbEntityEvent(Object src, ProjectController mediator, DbEntity entity) {
        mediator.fireEvent(new DbEntityEvent(src, entity, MapEvent.ADD));
        DbEntityDisplayEvent displayEvent = new DbEntityDisplayEvent(src, entity, mediator.getCurrentState().getDataMap(),
                mediator.getCurrentState().getNode(), (DataChannelDescriptor) mediator.getProject().getRootNode());
        displayEvent.setMainTabFocus(true);
        mediator.fireEvent(displayEvent);
    }

    @Override
    public void syncDbEntity() {

    }

    @Override
    public void filterInheritedEntities(Collection<ObjEntity> entities) {
        entities.removeIf(objEntity -> objEntity.getSuperEntity() != null);
    }
}

