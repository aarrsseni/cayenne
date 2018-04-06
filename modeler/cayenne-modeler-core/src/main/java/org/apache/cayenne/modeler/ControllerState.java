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

package org.apache.cayenne.modeler;

import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.event.*;
import org.apache.cayenne.map.*;
import org.apache.cayenne.map.event.*;
import org.apache.cayenne.modeler.editor.CallbackType;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.event.*;
import org.apache.cayenne.modeler.event.listener.*;

import java.util.Arrays;

/*
 * @since 4.1
 * A snapshot of the current state of the project controller. This was added
 * so that we could support history of recent objects.
 */
public class ControllerState implements DomainDisplayListener, DataNodeDisplayListener, DataMapDisplayListener, ObjEntityDisplayListener, EmbeddableDisplayListener, QueryDisplayListener,
                                        ProcedureDisplayListener, ProcedureParameterDisplayListener, DbEntityDisplayListener, DbAttributeDisplayListener, ObjAttributeDisplayListener,
                                        EmbeddableAttributeDisplayListener, DbRelationshipDisplayListener, MultipleObjectsDisplayListener, ObjRelationshipDisplayListener, DataMapListener,
                                        ObjEntityListener, DbEntityListener, QueryListener, DataNodeListener, DomainListener, ProcedureListener, DbRelationshipListener{

    private boolean isRefiring;
    private DisplayEvent event;
    private DataChannelDescriptor domain;
    private DataNodeDescriptor node;
    private DataMap map;
    private ObjEntity objEntity;
    private DbEntity dbEntity;
    private Embeddable embeddable;

    private EmbeddableAttribute[] embAttrs;

    private ObjAttribute[] objAttrs;
    private DbAttribute[] dbAttrs;
    private ObjRelationship[] objRels;
    private DbRelationship[] dbRels;

    private Procedure procedure;
    private ProcedureParameter[] procedureParameters;
    private QueryDescriptor query;

    /**
     * Paths of multiple selection
     */
    private ConfigurationNode[] paths;

    /**
     * Parent path of multiple selection
     */
    private ConfigurationNode parentPath;

    /**
     * currently selecte entity listener class
     */
    private String listenerClass;
    /**
     * currently selected callback type
     */
    private CallbackType callbackType;
    /**
     * currently selected callback methods
     */
    private ObjCallbackMethod[] callbackMethods;

    private ProjectController projectController;

    public ControllerState(ProjectController projectController) {

        // life is much easier if these guys are never null
        embAttrs = new EmbeddableAttribute[0];
        dbAttrs = new DbAttribute[0];
        dbRels = new DbRelationship[0];
        procedureParameters = new ProcedureParameter[0];
        objAttrs = new ObjAttribute[0];
        objRels = new ObjRelationship[0];

        callbackMethods = new ObjCallbackMethod[0];

        this.projectController = projectController;
    }

    public void resetState(){
        embAttrs = new EmbeddableAttribute[0];
        dbAttrs = new DbAttribute[0];
        dbRels = new DbRelationship[0];
        procedureParameters = new ProcedureParameter[0];
        objAttrs = new ObjAttribute[0];
        objRels = new ObjRelationship[0];

        callbackMethods = new ObjCallbackMethod[0];
    }

    /*
     * Used to determine if the val ControllerState is equivalent, which
     * means if the event is refired again, will it end up in the same place
     * on the screen. This get's a bit messy at the end, because of
     * inheritance heirarchy issues.
     */
    public boolean isEquivalent(ControllerState val) {

        if (val == null)
            return false;

        if (event instanceof EntityDisplayEvent && val.event instanceof EntityDisplayEvent) {
            if (((EntityDisplayEvent) val.event).getEntity() instanceof ObjEntity) {
                return objEntity == val.objEntity;
            } else {
                return dbEntity == val.dbEntity;
            }
        } else if (event instanceof ProcedureDisplayEvent && val.event instanceof ProcedureDisplayEvent) {
            return procedure == val.procedure;
        } else if (event instanceof QueryDisplayEvent && val.event instanceof QueryDisplayEvent) {
            return query == val.query;
        } else if (event instanceof EmbeddableDisplayEvent && val.event instanceof EmbeddableDisplayEvent) {
            return embeddable == val.embeddable;
        } else if (event.getClass() == DataMapDisplayEvent.class && event.getClass() == val.event.getClass()) {
            return map == val.map;
        } else if (event.getClass() == DataNodeDisplayEvent.class && event.getClass() == val.event.getClass()) {
            return node == val.node;
        } else if (event.getClass() == DomainDisplayEvent.class && event.getClass() == val.event.getClass()) {
            return domain == val.domain;
        }

        return false;
    }

    public boolean isRefiring() {
        return isRefiring;
    }

    public void setEvent(DisplayEvent event) {
        this.event = event;
    }

    public DisplayEvent getEvent() {
        return event;
    }

    public DataChannelDescriptor getDomain() {
        return domain;
    }

    public DataNodeDescriptor getNode() {
        return node;
    }

    public DataMap getDataMap() {
        return map;
    }

    public ObjEntity getObjEntity() {
        return objEntity;
    }

    public DbEntity getDbEntity() {
        return dbEntity;
    }

    public Embeddable getEmbeddable() {
        return embeddable;
    }

    public EmbeddableAttribute[] getEmbAttrs() {
        return embAttrs;
    }

    public ObjAttribute[] getObjAttrs() {
        return objAttrs;
    }

    public DbAttribute[] getDbAttrs() {
        return dbAttrs;
    }

    public ObjRelationship[] getObjRels() {
        return objRels;
    }

    public DbRelationship[] getDbRels() {
        return dbRels;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public ProcedureParameter[] getProcedureParameters() {
        return procedureParameters;
    }

    public QueryDescriptor getQuery() {
        return query;
    }

    public ConfigurationNode[] getPaths() {
        return paths;
    }

    public ConfigurationNode getParentPath() {
        return parentPath;
    }

    public String getListenerClass() {
        return listenerClass;
    }

    public CallbackType getCallbackType() {
        return callbackType;
    }

    public ObjCallbackMethod[] getCallbackMethods() {
        return callbackMethods;
    }

    public void setDomain(DataChannelDescriptor domain) {
        this.domain = domain;
    }

    public void setNode(DataNodeDescriptor node) {
        this.node = node;
    }

    public void setMap(DataMap map) {
        this.map = map;
    }

    public void setRefiring(boolean refiring) {
        isRefiring = refiring;
    }

    public void setObjEntity(ObjEntity objEntity) {
        this.objEntity = objEntity;
    }

    public void setEmbeddable(Embeddable embeddable) {
        this.embeddable = embeddable;
    }

    public void setQuery(QueryDescriptor query) {
        this.query = query;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public void setProcedureParameters(ProcedureParameter[] procedureParameters){
        this.procedureParameters = procedureParameters;
    }

    public void setDbEntity(DbEntity dbEntity) {
        this.dbEntity = dbEntity;
    }

    public void setDbAttrs(DbAttribute[] dbAttrs){
        this.dbAttrs = dbAttrs;
    }

    public void setObjAttrs(ObjAttribute[] objAttrs){
        this.objAttrs = objAttrs;
    }

    public void setEmbAttrs(EmbeddableAttribute[] embAttrs){
        this.embAttrs = embAttrs;
    }

    public void setDbRels(DbRelationship[] dbRels){
        this.dbRels = dbRels;
    }

    public void setObjRels(ObjRelationship[] objRels){
        this.objRels = objRels;
    }

    public void setPaths(ConfigurationNode[] paths) {
        this.paths = paths;
    }

    public void setParentPath(ConfigurationNode parentPath) {
        this.parentPath = parentPath;
    }

    public void setListenerClass(String listenerClass) {
        this.listenerClass = listenerClass;
    }

    public void setCallbackType(CallbackType callbackType) {
        this.callbackType = callbackType;
    }

    public void setCallbackMethods(ObjCallbackMethod[] callbackMethods){
        this.callbackMethods = callbackMethods;
    }

    @Override
    public void currentDomainChanged(DomainDisplayEvent e) {
        boolean changed = e.getDomain() != projectController.getCurrentState().getDomain();
        if (!changed) {
            changed = projectController.getCurrentState().getNode() != null || projectController.getCurrentState().getDataMap() != null || projectController.getCurrentState().getDbEntity() != null
                    || projectController.getCurrentState().getObjEntity() != null || projectController.getCurrentState().getProcedure() != null || projectController.getCurrentState().getQuery() != null
                    || projectController.getCurrentState().getEmbeddable() != null;
        }

        if (!e.isRefired()) {
            e.setDomainChanged(changed);
            if (changed) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
            }
        }

        if (changed) {
            projectController.saveState(e);
        }
    }

    @Override
    public void currentDataNodeChanged(DataNodeDisplayEvent e) {
        boolean changed = e.getDataNode() != projectController.getCurrentState().getNode();

        if (!changed) {
            changed = projectController.getCurrentState().getDataMap() != null || projectController.getCurrentState().getDbEntity() != null || projectController.getCurrentState().getObjEntity() != null
                    || projectController.getCurrentState().getProcedure() != null || projectController.getCurrentState().getQuery() != null || projectController.getCurrentState().getEmbeddable() != null;
        }

        if (!e.isRefired()) {
            e.setDataNodeChanged(changed);

            if (changed) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setNode(e.getDataNode());
            }
        }

        if (changed) {
            projectController.saveState(e);
        }
    }

    @Override
    public void currentDataMapChanged(DataMapDisplayEvent e) {
        boolean changed = e.getDataMap() != projectController.getCurrentState().getDataMap();
        if (!changed) {
            changed = projectController.getCurrentState().getDbEntity() != null || projectController.getCurrentState().getObjEntity() != null || projectController.getCurrentState().getProcedure() != null
                    || projectController.getCurrentState().getQuery() != null || projectController.getCurrentState().getEmbeddable() != null;
        }

        if (!e.isRefired()) {
            e.setDataMapChanged(changed);

            if (changed) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setNode(e.getDataNode());
                projectController.getCurrentState().setMap(e.getDataMap());
            }
        }

        if (changed) {
            projectController.saveState(e);
        }
    }

    @Override
    public void currentObjEntityChanged(ObjEntityDisplayEvent e) {
        boolean changed = e.getEntity() != projectController.getCurrentState().getObjEntity();

        if (!e.isRefired()) {
            e.setEntityChanged(changed);

            if (changed) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setNode(e.getDataNode());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setObjEntity((ObjEntity)e.getEntity());
            }
        }

        if (changed) {
            projectController.saveState(e);
        }
    }

    @Override
    public void currentEmbeddableChanged(EmbeddableDisplayEvent e) {
        boolean changed = e.getEmbeddable() != projectController.getCurrentState().getEmbeddable();

        if (!e.isRefired()) {
            e.setEmbeddableChanged(changed);

            if (changed) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setNode(e.getDataNode());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setEmbeddable(e.getEmbeddable());
            }
        }

        if (changed) {
            projectController.saveState(e);
        }
    }

    @Override
    public void currentQueryChanged(QueryDisplayEvent e) {
        boolean changed = e.getQuery() != projectController.getCurrentState().getQuery();

        if (!e.isRefired()) {
            e.setQueryChanged(changed);

            if (changed) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setQuery(e.getQuery());
            }
        }

        if (changed) {
            projectController.saveState(e);
        }
    }

    @Override
    public void currentProcedureChanged(ProcedureDisplayEvent e) {
        boolean changed = e.getProcedure() != projectController.getCurrentState().getProcedure();

        if (!e.isRefired()) {
            e.setProcedureChanged(changed);

            if (changed) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setProcedure(e.getProcedure());
            }
        }

        if (changed) {
            projectController.saveState(e);
        }
    }

    @Override
    public void currentProcedureParameterChanged(ProcedureParameterDisplayEvent e) {
        boolean changed = !Arrays.equals(e.getProcedureParameters(), projectController.getCurrentState().getProcedureParameters());

        if (changed) {
            if (projectController.getCurrentState().getProcedure() != e.getProcedure()) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setProcedure(e.getProcedure());
            }
            projectController.getCurrentState().setProcedureParameters(e.getProcedureParameters());
        }
    }

    @Override
    public void currentDbEntityChanged(DbEntityDisplayEvent e) {
        boolean changed = e.getEntity() != projectController.getCurrentState().getDbEntity();
        if (!e.isRefired()) {
            e.setEntityChanged(changed);

            if (changed) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setNode(e.getDataNode());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setDbEntity((DbEntity)e.getEntity());
            }
        }

        if (changed) {
            projectController.saveState(e);
        }
    }

    @Override
    public void currentDbAttributeChanged(DbAttributeDisplayEvent e) {
        boolean changed = !Arrays.equals(e.getAttributes(), projectController.getCurrentState().getDbAttrs());

        if (changed) {
            if (e.getEntity() != projectController.getCurrentState().getDbEntity()) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setDbEntity((DbEntity)e.getEntity());
            }
            projectController.getCurrentState().setDbAttrs(new DbAttribute[e.getAttributes().length]);
            System.arraycopy(e.getAttributes(), 0, projectController.getCurrentState().getDbAttrs(), 0, projectController.getCurrentState().getDbAttrs().length);
        }
    }

    @Override
    public void currentObjAttributeChanged(ObjAttributeDisplayEvent e) {
        boolean changed = !Arrays.equals(e.getAttributes(), projectController.getCurrentState().getObjAttrs());

        if (changed) {
            if (e.getEntity() != projectController.getCurrentState().getObjEntity()) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setObjEntity((ObjEntity)e.getEntity());
            }
            projectController.getCurrentState().setObjAttrs(new ObjAttribute[e.getAttributes().length]);
            System.arraycopy(e.getAttributes(), 0, projectController.getCurrentState().getObjAttrs(), 0, projectController.getCurrentState().getObjAttrs().length);
        }
    }

    @Override
    public void currentEmbeddableAttributeChanged(EmbeddableAttributeDisplayEvent e) {
        boolean changed = !Arrays.equals(e.getEmbeddableAttributes(), projectController.getCurrentState().getEmbAttrs());

        if (changed) {
            if (e.getEmbeddable() != projectController.getCurrentState().getEmbeddable()) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setEmbeddable(e.getEmbeddable());
            }
            projectController.getCurrentState().setEmbAttrs(new EmbeddableAttribute[e.getEmbeddableAttributes().length]);
            System.arraycopy(e.getEmbeddableAttributes(), 0, projectController.getCurrentState().getEmbAttrs(), 0, projectController.getCurrentState().getEmbAttrs().length);
        }
    }

    @Override
    public void currentDbRelationshipChanged(DbRelationshipDisplayEvent e) {
        boolean changed = !Arrays.equals(e.getRelationships(), projectController.getCurrentState().getDbRels());

        if (changed) {
            if (e.getEntity() != projectController.getCurrentState().getDbEntity()) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setDbEntity((DbEntity)e.getEntity());
            }
            projectController.getCurrentState().setDbRels(new DbRelationship[e.getRelationships().length]);
            System.arraycopy(e.getRelationships(), 0, projectController.getCurrentState().getDbRels(), 0, projectController.getCurrentState().getDbRels().length);
        }
    }

    @Override
    public void currentObjectsChanged(MultipleObjectsDisplayEvent e) {
        projectController.clearState();
        projectController.getCurrentState().setPaths(e.getNodes());
        projectController.getCurrentState().setParentPath(e.getParentNode());
    }

    @Override
    public void currentObjRelationshipChanged(ObjRelationshipDisplayEvent e) {
        boolean changed = !Arrays.equals(e.getRelationships(), projectController.getCurrentState().getObjRels());
        e.setRelationshipChanged(changed);

        if (changed) {
            if (e.getEntity() != projectController.getCurrentState().getObjEntity()) {
                projectController.clearState();
                projectController.getCurrentState().setDomain(e.getDomain());
                projectController.getCurrentState().setMap(e.getDataMap());
                projectController.getCurrentState().setObjEntity((ObjEntity) e.getEntity());
            }
            projectController.getCurrentState().setObjRels(new ObjRelationship[e.getRelationships().length]);
            System.arraycopy(e.getRelationships(), 0, projectController.getCurrentState().getObjRels(), 0, projectController.getCurrentState().getObjRels().length);
        }
    }

    public void initControllerStateListeners() {
        projectController.getEventController().addDomainDisplayListener(this);
        projectController.getEventController().addDataNodeDisplayListener(this);
        projectController.getEventController().addDataMapDisplayListener(this);
        projectController.getEventController().addObjEntityDisplayListener(this);
        projectController.getEventController().addEmbeddableDisplayListener(this);
        projectController.getEventController().addQueryDisplayListener(this);
        projectController.getEventController().addProcedureDisplayListener(this);
        projectController.getEventController().addProcedureParameterDisplayListener(this);
        projectController.getEventController().addDbEntityDisplayListener(this);
        projectController.getEventController().addDbAttributeDisplayListener(this);
        projectController.getEventController().addObjAttributeDisplayListener(this);
        projectController.getEventController().addEmbeddableAttributeDisplayListener(this);
        projectController.getEventController().addDbRelationshipDisplayListener(this);
        projectController.getEventController().addMultipleObjectsDisplayListener(this);
        projectController.getEventController().addObjRelationshipDisplayListener(this);

        projectController.getEventController().addDataMapListener(this);
        projectController.getEventController().addObjEntityListener(this);
        projectController.getEventController().addDbEntityListener(this);
        projectController.getEventController().addQueryListener(this);
        projectController.getEventController().addDataNodeListener(this);
        projectController.getEventController().addDomainListener(this);
        projectController.getEventController().addProcedureListener(this);
        projectController.getEventController().addDbRelationshipListener(this);
    }

    @Override
    public void dataMapChanged(DataMapEvent e) {

    }

    @Override
    public void dataMapAdded(DataMapEvent e) {

    }

    @Override
    public void dataMapRemoved(DataMapEvent e) {
        projectController.removeFromHistory(e);
    }


    @Override
    public void objEntityChanged(EntityEvent e) {
        if (e.getEntity().getDataMap() != null) {
            e.getEntity().getDataMap().objEntityChanged(e);
        }
    }

    @Override
    public void objEntityAdded(EntityEvent e) {

    }

    @Override
    public void objEntityRemoved(EntityEvent e) {
        projectController.removeFromHistory(e);
    }

    @Override
    public void dbEntityChanged(EntityEvent e) {
        if (e.getEntity().getDataMap() != null) {
            e.getEntity().getDataMap().dbEntityChanged(e);
        }
    }

    @Override
    public void dbEntityAdded(EntityEvent e) {

    }

    @Override
    public void dbEntityRemoved(EntityEvent e) {
        projectController.removeFromHistory(e);
    }

    @Override
    public void queryChanged(QueryEvent e) {

    }

    @Override
    public void queryAdded(QueryEvent e) {

    }

    @Override
    public void queryRemoved(QueryEvent e) {
        projectController.removeFromHistory(e);
    }

    @Override
    public void dataNodeChanged(DataNodeEvent e) {

    }

    @Override
    public void dataNodeAdded(DataNodeEvent e) {

    }

    @Override
    public void dataNodeRemoved(DataNodeEvent e) {
        projectController.removeFromHistory(e);
    }

    @Override
    public void domainChanged(DomainEvent e) {
        projectController.removeFromHistory(e);
    }

    @Override
    public void procedureChanged(ProcedureEvent e) {

    }

    @Override
    public void procedureAdded(ProcedureEvent e) {

    }

    @Override
    public void procedureRemoved(ProcedureEvent e) {
        projectController.removeFromHistory(e);
    }

    @Override
    public void dbRelationshipChanged(RelationshipEvent e) {
        if (e.getEntity() instanceof DbEntity) {
            ((DbEntity) e.getEntity()).dbRelationshipChanged(e);
        }
    }

    @Override
    public void dbRelationshipAdded(RelationshipEvent e) {

    }

    @Override
    public void dbRelationshipRemoved(RelationshipEvent e) {

    }
}
