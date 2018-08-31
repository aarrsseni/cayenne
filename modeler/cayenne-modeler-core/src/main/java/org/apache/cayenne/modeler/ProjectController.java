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
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.event.DataMapEvent;
import org.apache.cayenne.configuration.event.DataMapListener;
import org.apache.cayenne.configuration.event.DataNodeEvent;
import org.apache.cayenne.configuration.event.DataNodeListener;
import org.apache.cayenne.configuration.event.DomainEvent;
import org.apache.cayenne.configuration.event.DomainListener;
import org.apache.cayenne.configuration.event.ProcedureEvent;
import org.apache.cayenne.configuration.event.ProcedureListener;
import org.apache.cayenne.configuration.event.ProcedureParameterEvent;
import org.apache.cayenne.configuration.event.ProcedureParameterListener;
import org.apache.cayenne.configuration.event.QueryEvent;
import org.apache.cayenne.configuration.event.QueryListener;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.map.event.AttributeEvent;
import org.apache.cayenne.map.event.DbAttributeListener;
import org.apache.cayenne.map.event.DbEntityListener;
import org.apache.cayenne.map.event.DbRelationshipListener;
import org.apache.cayenne.map.event.EmbeddableAttributeEvent;
import org.apache.cayenne.map.event.EmbeddableAttributeListener;
import org.apache.cayenne.map.event.EmbeddableEvent;
import org.apache.cayenne.map.event.EmbeddableListener;
import org.apache.cayenne.map.event.EntityEvent;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.map.event.ObjAttributeListener;
import org.apache.cayenne.map.event.ObjEntityListener;
import org.apache.cayenne.map.event.ObjRelationshipListener;
import org.apache.cayenne.map.event.RelationshipEvent;
import org.apache.cayenne.modeler.event.AttributeDisplayEvent;
import org.apache.cayenne.modeler.event.CallbackMethodEvent;
import org.apache.cayenne.modeler.event.CallbackMethodListener;
import org.apache.cayenne.modeler.event.DataMapDisplayEvent;
import org.apache.cayenne.modeler.event.DataMapDisplayListener;
import org.apache.cayenne.modeler.event.DataNodeDisplayEvent;
import org.apache.cayenne.modeler.event.DataNodeDisplayListener;
import org.apache.cayenne.modeler.event.DataSourceModificationEvent;
import org.apache.cayenne.modeler.event.DataSourceModificationListener;
import org.apache.cayenne.modeler.event.DbAttributeDisplayListener;
import org.apache.cayenne.modeler.event.DbEntityDisplayListener;
import org.apache.cayenne.modeler.event.DbRelationshipDisplayListener;
import org.apache.cayenne.modeler.event.DisplayEvent;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.DomainDisplayListener;
import org.apache.cayenne.modeler.event.EmbeddableAttributeDisplayEvent;
import org.apache.cayenne.modeler.event.EmbeddableAttributeDisplayListener;
import org.apache.cayenne.modeler.event.EmbeddableDisplayEvent;
import org.apache.cayenne.modeler.event.EmbeddableDisplayListener;
import org.apache.cayenne.modeler.event.EntityDisplayEvent;
import org.apache.cayenne.modeler.event.EntityListenerEvent;
import org.apache.cayenne.modeler.event.EntityListenerListener;
import org.apache.cayenne.modeler.event.MultipleObjectsDisplayEvent;
import org.apache.cayenne.modeler.event.MultipleObjectsDisplayListener;
import org.apache.cayenne.modeler.event.ObjAttributeDisplayListener;
import org.apache.cayenne.modeler.event.ObjEntityDisplayListener;
import org.apache.cayenne.modeler.event.ObjRelationshipDisplayListener;
import org.apache.cayenne.modeler.event.ProcedureDisplayEvent;
import org.apache.cayenne.modeler.event.ProcedureDisplayListener;
import org.apache.cayenne.modeler.event.ProcedureParameterDisplayEvent;
import org.apache.cayenne.modeler.event.ProcedureParameterDisplayListener;
import org.apache.cayenne.modeler.event.ProjectDirtyEvent;
import org.apache.cayenne.modeler.event.ProjectDirtyEventListener;
import org.apache.cayenne.modeler.event.ProjectFileOnChangeTrackerEvent;
import org.apache.cayenne.modeler.event.ProjectFileOnChangeEventListener;
import org.apache.cayenne.modeler.event.ProjectOnSaveEvent;
import org.apache.cayenne.modeler.event.ProjectOnSaveListener;
import org.apache.cayenne.modeler.event.QueryDisplayEvent;
import org.apache.cayenne.modeler.event.QueryDisplayListener;
import org.apache.cayenne.modeler.event.RelationshipDisplayEvent;
import org.apache.cayenne.modeler.event.SaveListener;
import org.apache.cayenne.configuration.event.*;
import org.apache.cayenne.configuration.xml.DataChannelMetaData;
import org.apache.cayenne.di.Injector;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.event.EmbeddableEvent;
import org.apache.cayenne.map.event.EntityEvent;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.event.*;
import org.apache.cayenne.modeler.event.listener.ListenerDescriptor;
import org.apache.cayenne.modeler.pref.DataMapDefaults;
import org.apache.cayenne.modeler.pref.DataNodeDefaults;
import org.apache.cayenne.modeler.pref.ProjectStatePreferences;
import org.apache.cayenne.modeler.services.DbService;
import org.apache.cayenne.modeler.util.AdapterMapping;
import org.apache.cayenne.modeler.util.CircularArray;
import org.apache.cayenne.modeler.util.Comparators;
import org.apache.cayenne.pref.CayenneProjectPreferences;
import org.apache.cayenne.project.ConfigurationNodeParentGetter;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.util.IDUtil;

import java.util.*;
import java.util.prefs.Preferences;

/**
 * A controller that works with the project tree, tracking selection and
 * dispatching project events.
 */
public class ProjectController {

    protected EventController eventController;

    protected boolean dirty;
    private int entityTabSelection;

    protected Project project;

    private Preferences projectControllerPreferences;

    private ControllerState currentState;
    private CircularArray<ControllerState> controllerStateHistory;
    private int maxHistorySize = 20;

    private EntityResolver entityResolver;

    private AdapterMapping adapterMapping;

    @com.google.inject.Inject
    protected CayenneProjectPreferences cayenneProjectPreferences;

    @com.google.inject.Inject
    protected Injector injector;

    public Injector getInjector() {
        return injector;
    }

    @com.google.inject.Inject
    protected com.google.inject.Injector bootiqueInjector;

    @com.google.inject.Inject
    protected DbService dbService;

    private String newProjectTemporaryName;

    private DataChannelMetaData metaData;

    /**
     * Project files watcher. When project file is changed, user will be asked
     * to confirm loading the changes
     */
    private ProjectFileChangeTracker fileChangeTracker;

    public ProjectController() {
        this.eventController = new EventController();
        controllerStateHistory = new CircularArray<>(maxHistorySize);
        currentState = new ControllerState(this);
        adapterMapping = new AdapterMapping();
    }

    public void setCurrentDataMap(DataMap dataMap) {
        currentState.map = dataMap;
    }

    public Project getProject() {
        return project;
    }

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setProject(Project currentProject) {
        if (this.project != currentProject) {

            this.project = currentProject;
            this.projectControllerPreferences = null;

            if (project == null) {
                this.entityResolver = null;

                if (fileChangeTracker != null) {
                    fileChangeTracker.interrupt();
                    fileChangeTracker = null;
                }
            } else {
                if (fileChangeTracker == null) {
                    fileChangeTracker = new ProjectFileChangeTracker(this);
                    fileChangeTracker.setDaemon(true);
                    fileChangeTracker.start();
                }

                fileChangeTracker.reconfigure();

                entityResolver = new EntityResolver(
                        ((DataChannelDescriptor) currentProject.getRootNode()).getDataMaps());

                updateEntityResolver();
            }
        }
    }

    void updateEntityResolver() {

        Collection<DataMap> dataMaps = ((DataChannelDescriptor) project.getRootNode()).getDataMaps();

        entityResolver.setDataMaps(dataMaps);

        for (DataMap dataMap : dataMaps) {
            dataMap.setNamespace(entityResolver);
        }
    }

    public Preferences getPreferenceForProject() {
        if (getProject() == null) {
            throw new CayenneRuntimeException("No Project selected");
        }
        if (projectControllerPreferences == null) {
            updateProjectControllerPreferences();
        }

        return projectControllerPreferences;
    }

    /**
     * Returns top preferences for the current project, throwing an exception if
     * no project is selected.
     */
    public Preferences getPreferenceForDataDomain() {

        DataChannelDescriptor dataDomain = (DataChannelDescriptor) getProject().getRootNode();
        if (dataDomain == null) {
            throw new CayenneRuntimeException("No DataDomain selected");
        }

        return getPreferenceForProject().node(dataDomain.getName());
    }

    /**
     * Returns preferences object for the current DataMap. If no preferences
     * exist for the current DataMap, a new Preferences object is created. If no
     * DataMap is currently selected, an exception is thrown. An optional
     * nameSuffix allows to address more than one defaults instance for a single
     * DataMap.
     */
    public DataMapDefaults getDataMapPreferences(String nameSuffix) {
        DataMap map = currentState.getDataMap();
        if (map == null) {
            throw new CayenneRuntimeException("No DataMap selected");
        }

        Preferences pref;
        if (nameSuffix == null || nameSuffix.length() == 0) {
            pref = getPreferenceForDataDomain().node("DataMap").node(map.getName());
        } else {
            pref = getPreferenceForDataDomain().node("DataMap").node(map.getName()).node(nameSuffix);
        }
        return (DataMapDefaults) cayenneProjectPreferences.getProjectDetailObject(
                DataMapDefaults.class, pref);
    }

    public DataMapDefaults getDataMapPreferences(DataMap dataMap) {
        Preferences pref;
        pref = getPreferenceForDataDomain().node("DataMap").node(dataMap.getName());

        return (DataMapDefaults) cayenneProjectPreferences.getProjectDetailObject(DataMapDefaults.class, pref);
    }

    public DataMapDefaults getDataMapPreferences(String nameSuffix, DataMap map) {
        Preferences pref;

        if (nameSuffix == null || nameSuffix.length() == 0) {
            pref = getPreferenceForDataDomain().node("DataMap").node(map.getName());
        } else {
            pref = getPreferenceForDataDomain().node("DataMap").node(map.getName()).node(nameSuffix);
        }
        return (DataMapDefaults) cayenneProjectPreferences.getProjectDetailObject(DataMapDefaults.class, pref);
    }

    /**
     * Returns preferences object for the current DataMap, throwing an exception
     * if no DataMap is selected.
     */
    public DataNodeDefaults getDataNodePreferences() {
        DataNodeDescriptor node = currentState.getNode();
        if (node == null) {
            throw new CayenneRuntimeException("No DataNode selected");
        }

        return (DataNodeDefaults) cayenneProjectPreferences.getProjectDetailObject(
                DataNodeDefaults.class, getPreferenceForDataDomain().node("DataNode").node(node.getName()));

    }

    public ProjectStatePreferences getProjectStatePreferences() {
        return (ProjectStatePreferences) cayenneProjectPreferences.getProjectDetailObject(
                ProjectStatePreferences.class, getPreferenceForDataDomain());
    }

    public void reset() {
        clearState();
        setEntityTabSelection(0);
        eventController.reset();
        controllerStateHistory.clear();
    }

    /*
     * Allow the user to change the default history size. TODO When a user
     * changes their preferences it should call this method. I don't know how
     * the preferences work, so I will leave this to someone else to do. Garry
     */
    public void setHistorySize(int newSize) {
        controllerStateHistory.resize(newSize);
    }

    public boolean isDirty() {
        return dirty;
    }

    /** Resets all current models to null. */
    void clearState() {
        // don't clear if we are refiring events for history navigation
        if (currentState.isRefiring()) {
            return;
        }

        currentState = new ControllerState(this);
    }

    void saveState(DisplayEvent e) {
        if (!controllerStateHistory.contains(currentState)) {
            currentState.setEvent(e);
            controllerStateHistory.add(currentState);
        }
    }

    void removeFromHistory(EventObject e) {

        int count = controllerStateHistory.size();
        List<ControllerState> removeList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            ControllerState cs = controllerStateHistory.get(i);
            if (cs == null || cs.getEvent() == null) {
                continue;
            }
            EventObject csEvent = cs.getEvent();

            if (e instanceof EntityEvent && csEvent instanceof EntityDisplayEvent) {
                if (((EntityEvent) e).getEntity() == ((EntityDisplayEvent) csEvent).getEntity()) {
                    removeList.add(cs);
                }
            } else if (e instanceof EmbeddableEvent && csEvent instanceof EmbeddableDisplayEvent) {
                if (((EmbeddableEvent) e).getEmbeddable() == ((EmbeddableDisplayEvent) csEvent).getEmbeddable()) {
                    removeList.add(cs);
                }
            } else if (e instanceof ProcedureEvent && csEvent instanceof ProcedureDisplayEvent) {
                if (((ProcedureEvent) e).getProcedure() == ((ProcedureDisplayEvent) csEvent).getProcedure()) {
                    removeList.add(cs);
                }
            } else if (e instanceof QueryEvent && csEvent instanceof QueryDisplayEvent) {
                if (((QueryEvent) e).getQuery() == ((QueryDisplayEvent) csEvent).getQuery()) {
                    removeList.add(cs);
                }
            } else if (e instanceof DataMapEvent && csEvent instanceof DataMapDisplayEvent) {
                if (((DataMapEvent) e).getDataMap() == ((DataMapDisplayEvent) csEvent).getDataMap()) {
                    removeList.add(cs);
                }
            } else if (e instanceof DataNodeEvent && csEvent instanceof DataNodeDisplayEvent) {
                if (((DataNodeEvent) e).getDataNode() == ((DataNodeDisplayEvent) csEvent).getDataNode()) {
                    removeList.add(cs);
                }
            } else if (e instanceof DomainEvent && csEvent instanceof DomainDisplayEvent) {
                if (((DomainEvent) e).getDomain() == ((DomainDisplayEvent) csEvent).getDomain()) {
                    removeList.add(cs);
                }
            }
        }

        for (ControllerState o : removeList) {
            controllerStateHistory.remove(o);
        }
    }

    /**
     * @since 4.1
     * Used to fire all types of events.
     * @param event
     */
    public void fireEvent(EventObject event) {
        if(event == null){
            return;
        }

        ListenerDescriptor listenerDescriptor = getEventController().getListenerDescriptor(event);
        EventListener[] list = getEventController()
                .getListenerMap()
                .getListeners(listenerDescriptor.getListenerClass());
        for(EventListener eventListener : list) {
            listenerDescriptor.callEvent(eventListener, event);
        }
    }

    public void addDataMap(Object src, DataMap map) {
        addDataMap(src, map, true);
    }

    private void addDataMap(Object src, DataMap map, boolean makeCurrent) {
        map.setDataChannelDescriptor(currentState.getDomain());
        // new map was added.. link it to domain (and node if possible)
        currentState.getDomain().getDataMaps().add(map);

        if (currentState.getNode() != null && !currentState.getNode().getDataMapNames().contains(map.getName())) {
            currentState.getNode().getDataMapNames().add(map.getName());
            fireEvent(new DataNodeEvent(this, currentState.getNode()));
        }

        fireEvent(new DataMapEvent(src, map, MapEvent.ADD));
        if (makeCurrent) {
            fireEvent(new DataMapDisplayEvent(src, map, currentState.getDomain(), currentState.getNode()));
        }
    }

    public ProjectFileChangeTracker getFileChangeTracker() {
        return fileChangeTracker;
    }

    /**
     * Returns currently selected object, null if there are none, List if there
     * are several
     */
    public Object getCurrentObject() {
        if (currentState.getObjEntity() != null) {
            return currentState.getObjEntity();
        } else if (currentState.getDbEntity() != null) {
            return currentState.getDbEntity();
        } else if (currentState.getEmbeddable() != null) {
            return currentState.getEmbeddable();
        } else if (currentState.getQuery() != null) {
            return currentState.getQuery();
        } else if (currentState.getProcedure() != null) {
            return currentState.getProcedure();
        } else if (currentState.getDataMap() != null) {
            return currentState.getDataMap();
        } else if (currentState.getNode() != null) {
            return currentState.getNode();
        } else if (currentState.getDomain() != null) {
            return currentState.getDomain();
        } else if (currentState.getPaths() != null) { // multiple objects
            ConfigurationNode[] paths = currentState.getPaths();

            ConfigurationNodeParentGetter parentGetter = injector.getInstance(ConfigurationNodeParentGetter.class);
            Object parent = parentGetter.getParent(paths[0]);

            List<ConfigurationNode> result = new ArrayList<>(Arrays.asList(paths));

            /*
             * Here we sort the list of objects to minimize the risk that
             * objects will be pasted incorrectly. For instance, ObjEntity
             * should go before Query, to increase chances that Query's root
             * would be set.
             */
            result.sort(parent instanceof DataMap
                    ? Comparators.getDataMapChildrenComparator()
                    : Comparators.getDataDomainChildrenComparator());

            return result;
        }

        return null;
    }




    public ArrayList<Embeddable> getEmbeddablesInCurrentDataDomain() {
        DataChannelDescriptor dataChannelDescriptor = (DataChannelDescriptor) getProject().getRootNode();
        Collection<DataMap> maps = dataChannelDescriptor.getDataMaps();
        Iterator<DataMap> it = maps.iterator();
        ArrayList<Embeddable> embs = new ArrayList<>();
        while (it.hasNext()) {
            embs.addAll(it.next().getEmbeddables());
        }
        return embs;
    }

    public Set<String> getEmbeddableNamesInCurrentDataDomain() {
        ArrayList<Embeddable> embs = getEmbeddablesInCurrentDataDomain();
        Set<String> embNames = new HashSet<>(embs.size());
        for (Embeddable emb : embs) {
            embNames.add(emb.getClassName());
        }
        return embNames;
    }

    void updateProjectControllerPreferences() {
        String key = getProject().getConfigurationResource() == null ? new String(IDUtil.pseudoUniqueByteSequence16())
                : project.getConfigurationResource().getURL().getPath();

        projectControllerPreferences = Preferences.userNodeForPackage(Project.class);

        if (key.trim().length() > 0) {
            if (key.contains(".xml")) {
                projectControllerPreferences = projectControllerPreferences.node(projectControllerPreferences
                        .absolutePath() + key.replace(".xml", ""));
            }
        }
    }

    /**
     * @since 4.1
     */
    public EventController getEventController() {
        return eventController;
    }

    /**
     * @since 4.1
     */
    public ControllerState getCurrentState() {
        return currentState;
    }

    /**
     * @since 4.1
     */
    public void setCurrentState(ControllerState cs) {
        this.currentState = cs;
    }

    /**
     * @since 4.1
     */
    public CircularArray<ControllerState> getControllerStateHistory() {
        return controllerStateHistory;
    }

    /**
     * @since 4.1
     */
    public void addControllerState(ControllerState controllerState) {
        controllerStateHistory.add(controllerState);
    }

    /**
     * @since 4.0
     */
    public int getEntityTabSelection() {
        return entityTabSelection;
    }

    /**
     * @since 4.0
     */
    public void setEntityTabSelection(int entityTabSelection) {
        this.entityTabSelection = entityTabSelection;
    }

    public DataChannelMetaData getMetaData() {
        return metaData;
    }

    public DbService getDbService() {
        return dbService;
    }

    /**
     * @since 4.1
     */
    public com.google.inject.Injector getBootiqueInjector() {
        return bootiqueInjector;
    }

    public AdapterMapping getAdapterMapping() {
        return adapterMapping;
    }

    public String getNewProjectTemporaryName() {

        // TODO: andrus 4/4/2010 - should that be reset every time a new project is opened
        if (newProjectTemporaryName == null) {
            StringBuffer buffer = new StringBuffer("new_project_");
            for (byte aKey : IDUtil.pseudoUniqueByteSequence(16)) {
                IDUtil.appendFormattedByte(buffer, aKey);
            }
            newProjectTemporaryName = buffer.toString();
        }

        return newProjectTemporaryName;
    }

    void setMetaData(DataChannelMetaData metaData) {
        this.metaData = metaData;
    }
}
