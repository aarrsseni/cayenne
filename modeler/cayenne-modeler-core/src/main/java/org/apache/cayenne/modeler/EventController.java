package org.apache.cayenne.modeler;

import org.apache.cayenne.configuration.event.*;
import org.apache.cayenne.map.event.*;
import org.apache.cayenne.modeler.event.*;

/**
 * @since 4.1
 */
public class EventController {

    protected EventListenerMap listenerMap;

    public EventController(){
        this.listenerMap = new EventListenerMap();
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

    public void addEnableToSaveListener(SaveListener listener) {
        listenerMap.add(SaveListener.class, listener);
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
}
