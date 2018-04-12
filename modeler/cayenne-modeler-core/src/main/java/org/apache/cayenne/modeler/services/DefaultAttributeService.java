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
import org.apache.cayenne.configuration.event.DbAttributeEvent;
import org.apache.cayenne.configuration.event.ObjAttributeEvent;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.*;
import org.apache.cayenne.map.event.EmbeddableAttributeEvent;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.*;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;
import org.apache.cayenne.modeler.util.ProjectUtil;

import java.util.Collection;

/**
 * @since 4.1
 */
public class DefaultAttributeService implements AttributeService {

    @Inject
    protected ProjectController projectController;

    @Override
    public void createDbAttribute() {
        DbEntity dbEntity = projectController.getCurrentState().getDbEntity();

        DbAttribute attr = new DbAttribute();
        attr.setName(NameBuilder.builder(attr, dbEntity).name());
        attr.setType(TypesMapping.NOT_DEFINED);
        attr.setEntity(dbEntity);

        createDbAttribute(projectController.getCurrentState().getDataMap(), dbEntity, attr);

        projectController.fireEvent(new CreateAttributeEvent(this, dbEntity, attr));
    }

    @Override
    public void createDbAttribute(DataMap map, DbEntity dbEntity, DbAttribute attr) {
        dbEntity.addAttribute(attr);
        fireDbAttributeEvent(this, projectController, map, dbEntity, attr);
    }

    @Override
    public void fireDbAttributeEvent(Object src, ProjectController mediator, DataMap map, DbEntity dbEntity,
                                     DbAttribute attr) {
        mediator.fireEvent(new DbAttributeEvent(src, attr, dbEntity, MapEvent.ADD));

        mediator.fireEvent(new DbAttributeDisplayEvent(src, attr, dbEntity, map,
                (DataChannelDescriptor) mediator.getProject().getRootNode()));
    }

    @Override
    public void createObjAttribute() {
        ObjEntity objEntity = projectController.getCurrentState().getObjEntity();

        ObjAttribute attr = new ObjAttribute();
        attr.setName(NameBuilder.builder(attr, objEntity).name());

        createObjAttribute(projectController.getCurrentState().getDataMap(), objEntity, attr);

        projectController.fireEvent(new CreateAttributeEvent(this, objEntity, attr));
    }

    @Override
    public void createObjAttribute(DataMap map, ObjEntity objEntity, ObjAttribute attr) {

        objEntity.addAttribute(attr);
        fireObjAttributeEvent(this, projectController, map, objEntity, attr);
    }

    public void removeDbAttributes(DataMap dataMap, DbEntity entity, DbAttribute[] attribs) {

        for (DbAttribute attrib : attribs) {
            entity.removeAttribute(attrib.getName());

            DbAttributeEvent e = new DbAttributeEvent(
                    this,
                    attrib,
                    entity,
                    MapEvent.REMOVE);

            projectController.fireEvent(e);
        }

        ProjectUtil.cleanObjMappings(dataMap);
    }

    public void removeObjAttributes(ObjEntity entity, ObjAttribute[] attribs) {

        for (ObjAttribute attrib : attribs) {
            entity.removeAttribute(attrib.getName());
            ObjAttributeEvent e = new ObjAttributeEvent(
                    this,
                    attrib,
                    entity,
                    MapEvent.REMOVE);
            projectController.fireEvent(e);

            Collection<ObjEntity> objEntities = ProjectUtil.getCollectionOfChildren((ObjEntity) e.getEntity());
            for (ObjEntity objEntity: objEntities) {
                objEntity.removeAttributeOverride(e.getAttribute().getName());
            }
        }
    }

    @Override
    public void removeObjAttributes(ObjAttribute[] objAttrs) {
        for (ObjAttribute attrib : objAttrs) {
            projectController.getCurrentState().getObjEntity().removeAttribute(attrib.getName());
            ObjAttributeEvent e = new ObjAttributeEvent(this, attrib, projectController.getCurrentState().getObjEntity(), MapEvent.REMOVE);
            projectController.fireEvent(e);
        }

        ProjectUtil.cleanObjMappings(projectController.getCurrentState().getDataMap());
    }

    public void removeEmbeddableAttributes(Embeddable embeddable, EmbeddableAttribute[] attrs) {

        for (EmbeddableAttribute attrib : attrs) {
            embeddable.removeAttribute(attrib.getName());
            EmbeddableAttributeEvent e = new EmbeddableAttributeEvent(this, attrib, embeddable, MapEvent.REMOVE);
            projectController.fireEvent(e);
        }
    }

    @Override
    public RemoveServiceStatus isRemove() {
        EmbeddableAttribute[] embAttrs = projectController.getCurrentState().getEmbAttrs();
        ObjAttribute[] objAttrs = projectController.getCurrentState().getObjAttrs();
        DbAttribute[] dbAttrs = projectController.getCurrentState().getDbAttrs();

        if (embAttrs != null && embAttrs.length > 0) {
            if (embAttrs.length == 1) {
                return new RemoveServiceStatus("Embeddable Attribute",
                        embAttrs[0].getName());
            } else {
                return new RemoveServiceStatus(null, "selected EmbAttributes");
            }
        } else if(objAttrs != null && objAttrs.length > 0) {
            if(objAttrs.length == 1) {
                return new RemoveServiceStatus("ObjAttribute", objAttrs[0]
                        .getName());
            } else {
                return new RemoveServiceStatus(null ,"selected ObjAttributes");
            }
        } else if(dbAttrs != null && dbAttrs.length > 0) {
            if(dbAttrs.length == 1) {
                return new RemoveServiceStatus("DbAttribute", dbAttrs[0]
                        .getName());
            } else {
                return new RemoveServiceStatus(null, "selected DbAttributes");
            }
        }

        return null;
    }

    @Override
    public void remove() {
        EmbeddableAttribute[] embAttrs = projectController.getCurrentState().getEmbAttrs();
        ObjAttribute[] objAttrs = projectController.getCurrentState().getObjAttrs();
        DbAttribute[] dbAttrs = projectController.getCurrentState().getDbAttrs();

        if (embAttrs != null && embAttrs.length > 0) {
            Embeddable embeddable = projectController.getCurrentState().getEmbeddable();
            removeEmbeddableAttributes(embeddable, embAttrs);
            projectController.fireEvent(new RemoveEmbeddableAttributeEvent(this, embeddable, embAttrs));
        } else if (objAttrs != null && objAttrs.length > 0) {
            ObjEntity entity = projectController.getCurrentState().getObjEntity();
            removeObjAttributes(entity, objAttrs);
            projectController.fireEvent(new RemoveObjAttributesEvent(this, entity, objAttrs));
        } else if(dbAttrs != null && dbAttrs.length > 0) {
            DbEntity entity = projectController.getCurrentState().getDbEntity();
            removeDbAttributes(projectController.getCurrentState().getDataMap(), entity, dbAttrs);
            projectController.fireEvent(new RemoveDbAttributesEvent(this, entity, dbAttrs));
        }
    }

    @Override
    public void fireObjAttributeEvent(Object src, ProjectController mediator, DataMap map, ObjEntity objEntity,
                                      ObjAttribute attr) {

        mediator.fireEvent(new ObjAttributeEvent(src, attr, objEntity, MapEvent.ADD));

        DataChannelDescriptor domain = (DataChannelDescriptor) mediator.getProject().getRootNode();

        mediator.fireEvent(new ObjAttributeDisplayEvent(src, attr, objEntity, map, domain));
    }

    @Override
    public void createEmbAttribute() {
        Embeddable embeddable = projectController.getCurrentState().getEmbeddable();

        EmbeddableAttribute attr = new EmbeddableAttribute();
        attr.setName(NameBuilder
                .builder(attr, embeddable)
                .name());

        createEmbAttribute(embeddable, attr);

        projectController.fireEvent(new CreateAttributeEvent(this, embeddable, attr));
    }

    @Override
    public void createEmbAttribute(Embeddable embeddable, EmbeddableAttribute attr) {
        embeddable.addAttribute(attr);
        fireEmbeddableAttributeEvent(this, projectController, embeddable, attr);
    }

    @Override
    public void fireEmbeddableAttributeEvent(Object src, ProjectController mediator, Embeddable embeddable,
                                      EmbeddableAttribute attr) {

        mediator.fireEvent(new EmbeddableAttributeEvent(src, attr, embeddable, MapEvent.ADD));

        EmbeddableAttributeDisplayEvent e = new EmbeddableAttributeDisplayEvent(src, embeddable, attr,
                mediator.getCurrentState().getDataMap(), (DataChannelDescriptor) mediator.getProject().getRootNode());

        mediator.fireEvent(e);
    }
}
