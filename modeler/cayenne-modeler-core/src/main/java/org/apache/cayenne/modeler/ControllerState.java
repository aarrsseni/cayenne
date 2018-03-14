package org.apache.cayenne.modeler;

import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.editor.CallbackType;
import org.apache.cayenne.modeler.editor.ObjCallbackMethod;
import org.apache.cayenne.modeler.event.*;

/*
 * @since 4.1
 * A snapshot of the current state of the project controller. This was added
 * so that we could support history of recent objects.
 */
public class ControllerState {
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

    public ControllerState() {

        // life is much easier if these guys are never null
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
}
