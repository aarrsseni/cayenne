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

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.event.*;
import org.apache.cayenne.map.event.*;
import org.apache.cayenne.modeler.event.*;

import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 4.1
 */
public class EventController {

    protected EventListenerMap listenerMap;

    protected Map<Class<? extends EventObject>, ListenerDescriptor> listenerDescriptorMap;

    protected ListenerDescriptorCreator listenerDescriptorCreator;

    public EventController(){
        this.listenerMap = new EventListenerMap();
        this.listenerDescriptorMap = new HashMap<>();
        this.listenerDescriptorCreator = new ListenerDescriptorCreator();
    }

    public void reset(){
        this.listenerMap = new EventListenerMap();
    }

    public EventListenerMap getListenerMap() {
        return listenerMap;
    }

    public void addDomainDisplayListener(DomainDisplayListener listener) {
        listenerMap.add(DomainDisplayListener.class, listener);
    }

    public void addDomainListener(DomainListener listener) {
        listenerMap.add(DomainListener.class, listener);
    }

    public void removeDomainListener(DomainListener listener) {
        listenerMap.remove(DomainListener.class, listener);
    }

    public void addDataNodeDisplayListener(DataNodeDisplayListener listener) {
        listenerMap.add(DataNodeDisplayListener.class, listener);
    }

    public void addDataNodeListener(DataNodeListener listener) {
        listenerMap.add(DataNodeListener.class, listener);
    }

    public void addDataMapDisplayListener(DataMapDisplayListener listener) {
        listenerMap.add(DataMapDisplayListener.class, listener);
    }

    public void addDataMapListener(DataMapListener listener) {
        listenerMap.add(DataMapListener.class, listener);
    }

    public void removeDataMapListener(DataMapListener listener) {
        listenerMap.remove(DataMapListener.class, listener);
    }

    public void addDbEntityListener(DbEntityListener listener) {
        listenerMap.add(DbEntityListener.class, listener);
    }

    public void removeDbEntityListener(DbEntityListener listener) {
        listenerMap.remove(DbEntityListener.class, listener);
    }

    public void addProjectOnSaveListener(ProjectOnSaveListener listener) {
        listenerMap.add(ProjectOnSaveListener.class, listener);
    }

    public void removeProjectOnSaveListener(ProjectOnSaveListener listener) {
        listenerMap.remove(ProjectOnSaveListener.class, listener);
    }

    public void addObjEntityListener(ObjEntityListener listener) {
        listenerMap.add(ObjEntityListener.class, listener);
    }

    public void removeObjEntityListener(ObjEntityListener listener) {
        listenerMap.remove(ObjEntityListener.class, listener);
    }

    public void addDbEntityDisplayListener(DbEntityDisplayListener listener) {
        listenerMap.add(DbEntityDisplayListener.class, listener);
    }

    public void addObjEntityDisplayListener(ObjEntityDisplayListener listener) {
        listenerMap.add(ObjEntityDisplayListener.class, listener);
    }

    public void addEmbeddableDisplayListener(EmbeddableDisplayListener listener) {
        listenerMap.add(EmbeddableDisplayListener.class, listener);
    }

    public void addEmbeddableAttributeDisplayListener(EmbeddableAttributeDisplayListener listener) {
        listenerMap.add(EmbeddableAttributeDisplayListener.class, listener);
    }

    public void addDbAttributeListener(DbAttributeListener listener) {
        listenerMap.add(DbAttributeListener.class, listener);
    }

    public void removeDbAttributeListener(DbAttributeListener listener) {
        listenerMap.remove(DbAttributeListener.class, listener);
    }

    public void addDbAttributeDisplayListener(DbAttributeDisplayListener listener) {
        listenerMap.add(DbAttributeDisplayListener.class, listener);
    }

    public void addObjAttributeListener(ObjAttributeListener listener) {
        listenerMap.add(ObjAttributeListener.class, listener);
    }

    public void removeObjAttributeListener(ObjAttributeListener listener) {
        listenerMap.remove(ObjAttributeListener.class, listener);
    }

    public void addObjAttributeDisplayListener(ObjAttributeDisplayListener listener) {
        listenerMap.add(ObjAttributeDisplayListener.class, listener);
    }

    public void addDbRelationshipListener(DbRelationshipListener listener) {
        listenerMap.add(DbRelationshipListener.class, listener);
    }

    public void removeDbRelationshipListener(DbRelationshipListener listener) {
        listenerMap.add(DbRelationshipListener.class, listener);
    }

    public void addDbRelationshipDisplayListener(DbRelationshipDisplayListener listener) {
        listenerMap.add(DbRelationshipDisplayListener.class, listener);
    }

    public void addObjRelationshipListener(ObjRelationshipListener listener) {
        listenerMap.add(ObjRelationshipListener.class, listener);
    }

    public void removeObjRelationshipListener(ObjRelationshipListener listener) {
        listenerMap.remove(ObjRelationshipListener.class, listener);
    }

    public void addObjRelationshipDisplayListener(ObjRelationshipDisplayListener listener) {
        listenerMap.add(ObjRelationshipDisplayListener.class, listener);
    }

    public void addQueryDisplayListener(QueryDisplayListener listener) {
        listenerMap.add(QueryDisplayListener.class, listener);
    }

    public void addQueryListener(QueryListener listener) {
        listenerMap.add(QueryListener.class, listener);
    }

    public void addProcedureDisplayListener(ProcedureDisplayListener listener) {
        listenerMap.add(ProcedureDisplayListener.class, listener);
    }

    public void addProcedureListener(ProcedureListener listener) {
        listenerMap.add(ProcedureListener.class, listener);
    }

    public void addProcedureParameterListener(ProcedureParameterListener listener) {
        listenerMap.add(ProcedureParameterListener.class, listener);
    }

    public void addProcedureParameterDisplayListener(ProcedureParameterDisplayListener listener) {
        listenerMap.add(ProcedureParameterDisplayListener.class, listener);
    }

    public void addMultipleObjectsDisplayListener(MultipleObjectsDisplayListener listener) {
        listenerMap.add(MultipleObjectsDisplayListener.class, listener);
    }

    public void addEnableToSaveListener(ProjectDirtyEventListener listener) {
        listenerMap.add(ProjectDirtyEventListener.class, listener);
    }

    /**
     * adds callback method manipulation listener
     *
     * @param listener
     *            listener
     */
    public void addCallbackMethodListener(CallbackMethodListener listener) {
        listenerMap.add(CallbackMethodListener.class, listener);
    }

    /**
     * adds listener class manipulation listener
     *
     * @param listener
     *            listener
     */
    public void addEntityListenerListener(EntityListenerListener listener) {
        listenerMap.add(EntityListenerListener.class, listener);
    }

    public void addEmbeddableAttributeListener(EmbeddableAttributeListener listener) {
        listenerMap.add(EmbeddableAttributeListener.class, listener);
    }

    public void addEmbeddableListener(EmbeddableListener listener) {
        listenerMap.add(EmbeddableListener.class, listener);
    }

    public void addDataSourceModificationListener(DataSourceModificationListener listener) {
        listenerMap.add(DataSourceModificationListener.class, listener);
    }

    public void removeDataSourceModificationListener(DataSourceModificationListener listener) {
        listenerMap.remove(DataSourceModificationListener.class, listener);
    }

    public void addProjectFileOnChangeEventListener(ProjectFileOnChangeEventListener listener) {
        listenerMap.add(ProjectFileOnChangeEventListener.class, listener);
    }



    public void addConsoleStopLoggingListener(ConsoleStopLoggingListener listener) {
        listenerMap.add(ConsoleStopLoggingListener.class, listener);
    }

    public <T extends EventListener> void addListener(Class<T> listenerClass, T listener) {
        listenerMap.add(listenerClass, listener);
    }

    public ListenerDescriptor getListenerDescriptor(EventObject key) {
        try {
            listenerDescriptorMap.putIfAbsent(key.getClass(), listenerDescriptorCreator.create((Class<? extends EventListener>) key.getClass().getMethod("getEventListener").invoke(key)));
        } catch (Exception e) {
            throw new CayenneRuntimeException("Can't create listener descriptor. Add getEventListener method to " + key.getClass());
        }
        return listenerDescriptorMap.get(key.getClass());
    }

    private void initListenerDescriptorMap(){
//        listenerDescriptorMap.put(DataMapEvent.class, listenerDescriptorCreator.create(DataMapListener.class));
//        listenerDescriptorMap.put(ProcedureEvent.class, listenerDescriptorCreator.create(ProcedureListener.class));
//        listenerDescriptorMap.put(QueryEvent.class, listenerDescriptorCreator.create(QueryListener.class));
//        listenerDescriptorMap.put(DomainEvent.class, listenerDescriptorCreator.create(DomainListener.class));
//        listenerDescriptorMap.put(DataNodeEvent.class, listenerDescriptorCreator.create(DataNodeListener.class));
//        listenerDescriptorMap.put(ObjEntityEvent.class, listenerDescriptorCreator.create(ObjEntityListener.class));
//        listenerDescriptorMap.put(DbEntityEvent.class, listenerDescriptorCreator.create(DbEntityListener.class));
//        listenerDescriptorMap.put(ObjRelationshipEvent.class, listenerDescriptorCreator.create(ObjRelationshipListener.class));
//        listenerDescriptorMap.put(DbRelationshipEvent.class, listenerDescriptorCreator.create(DbRelationshipListener.class));
//        listenerDescriptorMap.put(DbAttributeEvent.class, listenerDescriptorCreator.create(DbAttributeListener.class));
//        listenerDescriptorMap.put(ObjAttributeEvent.class, listenerDescriptorCreator.create(ObjAttributeListener.class));
//        listenerDescriptorMap.put(CallbackMethodDisplayEvent.class, listenerDescriptorCreator.create(CallbackMethodDisplayListener.class));
//        listenerDescriptorMap.put(CallbackMethodEvent.class, listenerDescriptorCreator.create(CallbackMethodListener.class));
//        listenerDescriptorMap.put(CallbackTypeSelectionEvent.class, listenerDescriptorCreator.create(CallbackTypeSelectionListener.class));
//        listenerDescriptorMap.put(DataMapDisplayEvent.class, listenerDescriptorCreator.create(DataMapDisplayListener.class));
//        listenerDescriptorMap.put(DataNodeDisplayEvent.class, listenerDescriptorCreator.create(DataNodeDisplayListener.class));
//        listenerDescriptorMap.put(DataSourceModificationEvent.class, listenerDescriptorCreator.create(DataSourceModificationListener.class));
//        listenerDescriptorMap.put(DbAttributeDisplayEvent.class, listenerDescriptorCreator.create(DbAttributeDisplayListener.class));
//        listenerDescriptorMap.put(DbEntityDisplayEvent.class, listenerDescriptorCreator.create(DbEntityDisplayListener.class));
//        listenerDescriptorMap.put(DbRelationshipDisplayEvent.class, listenerDescriptorCreator.create(DbRelationshipDisplayListener.class));
//        listenerDescriptorMap.put(DomainDisplayEvent.class, listenerDescriptorCreator.create(DomainDisplayListener.class));
//        listenerDescriptorMap.put(EmbeddableAttributeDisplayEvent.class, listenerDescriptorCreator.create(EmbeddableAttributeDisplayListener.class));
//        listenerDescriptorMap.put(EmbeddableDisplayEvent.class, listenerDescriptorCreator.create(EmbeddableDisplayListener.class));
//        listenerDescriptorMap.put(EntityListenerEvent.class, listenerDescriptorCreator.create(EntityListenerListener.class));
//        listenerDescriptorMap.put(MultipleObjectsDisplayEvent.class, listenerDescriptorCreator.create(MultipleObjectsDisplayListener.class));
//        listenerDescriptorMap.put(ObjAttributeDisplayEvent.class, listenerDescriptorCreator.create(ObjAttributeDisplayListener.class));
//        listenerDescriptorMap.put(ObjEntityDisplayEvent.class, listenerDescriptorCreator.create(ObjEntityDisplayListener.class));
//        listenerDescriptorMap.put(ObjRelationshipDisplayEvent.class, listenerDescriptorCreator.create(ObjRelationshipDisplayListener.class));
//        listenerDescriptorMap.put(ProcedureDisplayEvent.class, listenerDescriptorCreator.create(ProcedureDisplayListener.class));
//        listenerDescriptorMap.put(ProcedureParameterDisplayEvent.class, listenerDescriptorCreator.create(ProcedureParameterDisplayListener.class));
//        listenerDescriptorMap.put(ProcedureParameterEvent.class, listenerDescriptorCreator.create(ProcedureParameterListener.class));
//        listenerDescriptorMap.put(ProjectDirtyEvent.class, listenerDescriptorCreator.create(ProjectDirtyEventListener.class));
//        listenerDescriptorMap.put(ProjectFileOnChangeTrackerEvent.class, listenerDescriptorCreator.create(ProjectFileOnChangeEventListener.class));
//        listenerDescriptorMap.put(ProjectOnSaveEvent.class, listenerDescriptorCreator.create(ProjectOnSaveListener.class));
//        listenerDescriptorMap.put(QueryDisplayEvent.class, listenerDescriptorCreator.create(QueryDisplayListener.class));
//        listenerDescriptorMap.put(RecentFileListEvent.class, listenerDescriptorCreator.create(RecentFileListListener.class));
//        listenerDescriptorMap.put(EmbeddableAttributeEvent.class, listenerDescriptorCreator.create(EmbeddableAttributeListener.class));
//        listenerDescriptorMap.put(EmbeddableEvent.class, listenerDescriptorCreator.create(EmbeddableListener.class));

//        listenerDescriptorMap.put(ConsoleStopLoggingEvent.class, listenerDescriptorCreator.create(ConsoleStopLoggingListener.class));
//        listenerDescriptorMap.put(ShowLogConsoleEvent.class, listenerDescriptorCreator.create(ShowLogConsoleListener.class));
//        listenerDescriptorMap.put(ProjectOpenEvent.class, listenerDescriptorCreator.create(ProjectOpenListener.class));
//        listenerDescriptorMap.put(CreateAttributeEvent.class, listenerDescriptorCreator.create(CreateAttributeListener.class));
//        listenerDescriptorMap.put(CreateDataMapEvent.class, listenerDescriptorCreator.create(CreateDataMapListener.class));
//        listenerDescriptorMap.put(CreateDbEntityEvent.class, listenerDescriptorCreator.create(CreateDbEntityListener.class));
//        listenerDescriptorMap.put(CreateEmbeddableEvent.class, listenerDescriptorCreator.create(CreateEmbeddableListener.class));
    }
}
