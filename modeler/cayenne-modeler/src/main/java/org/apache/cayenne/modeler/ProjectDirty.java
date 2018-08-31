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

import org.apache.cayenne.configuration.event.*;
import org.apache.cayenne.map.event.*;
import org.apache.cayenne.modeler.action.RevertAction;
import org.apache.cayenne.modeler.action.SaveAction;
import org.apache.cayenne.modeler.action.SaveAsAction;
import org.apache.cayenne.modeler.event.*;
import org.apache.cayenne.modeler.event.listener.CallbackMethodListener;
import org.apache.cayenne.modeler.event.listener.EntityListenerListener;
import org.apache.cayenne.modeler.event.listener.ProjectDirtyEventListener;

/**
 * @since 4.1
 * Class used to listen all changes need to enable save button.
 */
public class ProjectDirty implements DomainListener, DataNodeListener, DataMapListener, ObjEntityListener,
        DbEntityListener, QueryListener, ProcedureListener, ProcedureParameterListener, DbAttributeListener,
        ObjAttributeListener, DbRelationshipListener, ObjRelationshipListener, CallbackMethodListener,
        EntityListenerListener, EmbeddableListener, EmbeddableAttributeListener, ProjectDirtyEventListener {

    protected CayenneModelerController modelerController;

    public ProjectDirty(CayenneModelerController modelerController){
        this.modelerController = modelerController;
    }

    @Override
    public void domainChanged(DomainEvent e) {
        setDirty(true);
    }

    public void setDirty(boolean dirty) {
        if (modelerController.getProjectController().isDirty() != dirty) {
            modelerController.getProjectController().dirty = dirty;

            enableSave(dirty);
            modelerController.getApplication().getActionManager().getAction(RevertAction.class).setEnabled(dirty);

            if (dirty) {
                modelerController.projectModifiedAction();
            }
        }
    }

    /**
     * If true, all save buttons become available.
     * @param enable or not save button
     */
    public void enableSave(boolean enable) {
        modelerController.getApplication().getActionManager().getAction(SaveAction.class).setEnabled(enable);
        modelerController.getApplication().getActionManager().getAction(SaveAsAction.class).setEnabled(enable);
    }

    @Override
    public void dataNodeChanged(DataNodeEvent e) {
        setDirty(true);
    }

    @Override
    public void dataNodeAdded(DataNodeEvent e) {
        setDirty(true);
    }

    @Override
    public void dataNodeRemoved(DataNodeEvent e) {
        setDirty(true);
    }

    @Override
    public void dataMapChanged(DataMapEvent e) {
        setDirty(true);
    }

    @Override
    public void dataMapAdded(DataMapEvent e) {
        setDirty(true);
    }

    @Override
    public void dataMapRemoved(DataMapEvent e) {
        setDirty(true);
    }

    @Override
    public void objEntityChanged(EntityEvent e) {
        setDirty(true);
    }

    @Override
    public void objEntityAdded(EntityEvent e) {
        setDirty(true);
    }

    @Override
    public void objEntityRemoved(EntityEvent e) {
        setDirty(true);
    }

    @Override
    public void dbEntityChanged(EntityEvent e) {
        setDirty(true);
    }

    @Override
    public void dbEntityAdded(EntityEvent e) {
        setDirty(true);
    }

    @Override
    public void dbEntityRemoved(EntityEvent e) {
        setDirty(true);
    }

    @Override
    public void queryChanged(QueryEvent e) {
        setDirty(true);
    }

    @Override
    public void queryAdded(QueryEvent e) {
        setDirty(true);
    }

    @Override
    public void queryRemoved(QueryEvent e) {
        setDirty(true);
    }

    @Override
    public void procedureChanged(ProcedureEvent e) {
        setDirty(true);
    }

    @Override
    public void procedureAdded(ProcedureEvent e) {
        setDirty(true);
    }

    @Override
    public void procedureRemoved(ProcedureEvent e) {
        setDirty(true);
    }

    @Override
    public void procedureParameterChanged(ProcedureParameterEvent e) {
        setDirty(true);
    }

    @Override
    public void procedureParameterAdded(ProcedureParameterEvent e) {
        setDirty(true);
    }

    @Override
    public void procedureParameterRemoved(ProcedureParameterEvent e) {
        setDirty(true);
    }

    @Override
    public void dbAttributeChanged(AttributeEvent e) {
        setDirty(true);
    }

    @Override
    public void dbAttributeAdded(AttributeEvent e) {
        setDirty(true);
    }

    @Override
    public void dbAttributeRemoved(AttributeEvent e) {
        setDirty(true);
    }

    @Override
    public void objAttributeChanged(AttributeEvent e) {
        setDirty(true);
    }

    @Override
    public void objAttributeAdded(AttributeEvent e) {
        setDirty(true);
    }

    @Override
    public void objAttributeRemoved(AttributeEvent e) {
        setDirty(true);
    }

    @Override
    public void dbRelationshipChanged(RelationshipEvent e) {
        setDirty(true);
    }

    @Override
    public void dbRelationshipAdded(RelationshipEvent e) {
        setDirty(true);
    }

    @Override
    public void dbRelationshipRemoved(RelationshipEvent e) {
        setDirty(true);
    }

    @Override
    public void objRelationshipChanged(RelationshipEvent e) {
        setDirty(true);
    }

    @Override
    public void objRelationshipAdded(RelationshipEvent e) {
        setDirty(true);
    }

    @Override
    public void objRelationshipRemoved(RelationshipEvent e) {
        setDirty(true);
    }

    @Override
    public void callbackMethodChanged(CallbackMethodEvent e) {
        setDirty(true);
    }

    @Override
    public void callbackMethodAdded(CallbackMethodEvent e) {
        setDirty(true);
    }

    @Override
    public void callbackMethodRemoved(CallbackMethodEvent e) {
        setDirty(true);
    }

    @Override
    public void entityListenerAdded(EntityListenerEvent e) {
        setDirty(true);
    }

    @Override
    public void entityListenerChanged(EntityListenerEvent e) {
        setDirty(true);
    }

    @Override
    public void entityListenerRemoved(EntityListenerEvent e) {
        setDirty(true);
    }

    @Override
    public void embeddableChanged(EmbeddableEvent e) {
        setDirty(true);
    }

    @Override
    public void embeddableAdded(EmbeddableEvent e) {
        setDirty(true);
    }

    @Override
    public void embeddableRemoved(EmbeddableEvent e) {
        setDirty(true);
    }

    @Override
    public void embeddableAttributeChanged(EmbeddableAttributeEvent e) {
        setDirty(true);
    }

    @Override
    public void embeddableAttributeAdded(EmbeddableAttributeEvent e) {
        setDirty(true);
    }

    @Override
    public void embeddableAttributeRemoved(EmbeddableAttributeEvent e) {
        setDirty(true);
    }

    @Override
    public void setProjectDirty(ProjectDirtyEvent e) {
        setDirty(e.getDirty());
    }

    /**
     * @since 4.1
     */
    void initAll(){
        modelerController.getProjectController().getEventController().addDataNodeListener(this);
        modelerController.getProjectController().getEventController().addDomainListener(this);
        modelerController.getProjectController().getEventController().addDataMapListener(this);
        modelerController.getProjectController().getEventController().addObjEntityListener(this);
        modelerController.getProjectController().getEventController().addDbEntityListener(this);
        modelerController.getProjectController().getEventController().addQueryListener(this);
        modelerController.getProjectController().getEventController().addProcedureListener(this);
        modelerController.getProjectController().getEventController().addProcedureParameterListener(this);
        modelerController.getProjectController().getEventController().addDbAttributeListener(this);
        modelerController.getProjectController().getEventController().addObjAttributeListener(this);
        modelerController.getProjectController().getEventController().addDbRelationshipListener(this);
        modelerController.getProjectController().getEventController().addObjRelationshipListener(this);
        modelerController.getProjectController().getEventController().addCallbackMethodListener(this);
        modelerController.getProjectController().getEventController().addEntityListenerListener(this);
        modelerController.getProjectController().getEventController().addEmbeddableListener(this);
        modelerController.getProjectController().getEventController().addEmbeddableAttributeListener(this);
        modelerController.getProjectController().getEventController().addEnableToSaveListener(this);
    }


}
