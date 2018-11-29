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
import org.apache.cayenne.gen.CgenConfiguration;
import org.apache.cayenne.gen.ClassGenerationAction;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.GenerateCodeEvent;
import org.apache.cayenne.modeler.event.ProjectDirtyEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @since 4.1
 */
public class DefaultGenerateCodeService implements GenerateCodeService {

    @Inject
    public ProjectController projectController;

    @Inject
    private CgenValidationService validationService;

    private Set<String> selectedEntities;
    private Set<String> selectedEmbeddables;
    private Set<String> isDataMapSelected;
    private Map<DataMap, Set<String>> selectedEntitiesForDataMap;
    private Map<DataMap, Set<String>> selectedEmbeddablesForDataMap;
    private Map<DataMap, Set<String>> selectedDataMaps;

    @Override
    public void generateCode() {
        projectController.fireEvent(new GenerateCodeEvent(this, projectController.getCurrentState().getDataMap()));
    }

    @Override
    public boolean isDefaultConfig(CgenConfiguration cgenConfiguration) {
        return cgenConfiguration.isMakePairs() && cgenConfiguration.isUsePkgPath() && !cgenConfiguration.isOverwrite() &&
                !cgenConfiguration.isCreatePKProperties() && !cgenConfiguration.isCreatePropertyNames() &&
                cgenConfiguration.getOutputPattern().equals("*.java") &&
                cgenConfiguration.getTemplate().equals(ClassGenerationAction.SUBCLASS_TEMPLATE) &&
                cgenConfiguration.getSuperTemplate().equals(ClassGenerationAction.SUPERCLASS_TEMPLATE);
    }

    @Override
    public void updateArtifactGenerationMode(Object classObj, boolean selected) {
        DataMap dataMap = (DataMap) classObj;
        CgenConfiguration cgenConfiguration = projectController.getMetaData().get(dataMap, CgenConfiguration.class);
        if(selected) {
            cgenConfiguration.setArtifactsGenerationMode("all");
        } else {
            cgenConfiguration.setArtifactsGenerationMode("entity");
        }
    }

    @Override
    public CgenConfiguration getCurrentConfiguration() {
        DataMap map = projectController.getCurrentState().getDataMap();
        return projectController.getMetaData().get(map, CgenConfiguration.class);
    }

    @Override
    public void loadEntity(ObjEntity objEntity) {
        getSelectedEntities().add(objEntity.getName());
        CgenConfiguration cgenConfiguration = getCurrentConfiguration();
        if(cgenConfiguration != null) {
            cgenConfiguration.loadEntity(objEntity);
        }
    }

    @Override
    public void loadEmbeddable(String embeddable) {
        getSelectedEmbeddables().add(embeddable);
        CgenConfiguration cgenConfiguration = getCurrentConfiguration();
        if(cgenConfiguration != null) {
            cgenConfiguration.loadEmbeddable(embeddable);
        }
    }

    @Override
    public CgenValidationService getCgenValidationService() {
        return validationService;
    }

    @Override
    public void initCollections() {
        selectedEntitiesForDataMap = new HashMap<>();
        selectedEmbeddablesForDataMap = new HashMap<>();
        selectedDataMaps = new HashMap<>();
    }

    @Override
    public Set<String> getSelectedEntities() {
        return selectedEntities;
    }

    @Override
    public Set<String> getSelectedEmbeddables() {
        return selectedEmbeddables;
    }

    @Override
    public Set<String> isDataMapSelected() {
        return isDataMapSelected;
    }

    @Override
    public void removeEntity(ObjEntity objEntity) {
        initCollectionsForSelection(objEntity.getDataMap());
        getSelectedEntities().remove(objEntity.getName());

        DataMap map = objEntity.getDataMap();
        CgenConfiguration cgenConfiguration = projectController.getMetaData().get(map, CgenConfiguration.class);
        if(cgenConfiguration != null) {
            cgenConfiguration.getEntities().remove(objEntity.getName());
        }
    }

    @Override
    public void removeEmbeddable(Embeddable embeddable) {
        initCollectionsForSelection(embeddable.getDataMap());
        getSelectedEmbeddables().remove(embeddable.getClassName());

        CgenConfiguration cgenConfiguration = projectController.getMetaData().get(embeddable.getDataMap(), CgenConfiguration.class);
        if(cgenConfiguration != null) {
            cgenConfiguration.getEmbeddables().remove(embeddable.getClassName());
        }
    }

    @Override
    public CgenConfiguration getConfiguration(List<Object> classes) {
        DataMap map = projectController.getCurrentState().getDataMap();
        CgenConfiguration cgenConfiguration = projectController.getMetaData().get(map, CgenConfiguration.class);
        if(cgenConfiguration != null){
            addToSelectedEntities(cgenConfiguration.getEntities(), classes);
            addToSelectedEmbeddables(cgenConfiguration.getEmbeddables(), classes);
            cgenConfiguration.setForce(true);
            return cgenConfiguration;
        }
        return null;
    }

    @Override
    public CgenConfiguration createConfiguration(Path basePath, List<Object> classes) {
        DataMap map = projectController.getCurrentState().getDataMap();
        CgenConfiguration cgenConfiguration = new CgenConfiguration();
        cgenConfiguration.setForce(true);
        cgenConfiguration.setDataMap(map);
        cgenConfiguration.setRootPath(basePath);
        addToSelectedEntities(map.getObjEntities()
                .stream()
                .map(Entity::getName)
                .collect(Collectors.toList()), classes);
        addToSelectedEmbeddables(map.getEmbeddables()
                .stream()
                .map(Embeddable::getClassName)
                .collect(Collectors.toList()), classes);
        projectController.getMetaData().add(map, cgenConfiguration);
        projectController.fireEvent(new ProjectDirtyEvent(this, true));
        return cgenConfiguration;
    }

    @Override
    public void updateSelectedEntities(List<Object> classes) {
        updateEntities(getSelectedEntities(), classes);
        updateEmbeddables(getSelectedEmbeddables(), classes);
    }

    @Override
    public List<Object> prepareClasses(DataMap dataMap) {
        List<Object> classes = new ArrayList<>();
        classes.add(dataMap);
        classes.addAll(dataMap.getObjEntities());
        classes.addAll(dataMap.getEmbeddables());
        initCollectionsForSelection(dataMap);
        return classes;
    }

    private void updateEntities(Set<String> selectedEntities, List<Object> classes) {
        CgenConfiguration cgenConfiguration = getCurrentConfiguration();
        if(cgenConfiguration != null) {
            cgenConfiguration.getEntities().clear();
            for(ObjEntity entity: getSelectedEntities(selectedEntities, classes)) {
                cgenConfiguration.loadEntity(entity);
            }
        }
    }

    private void updateEmbeddables(Set<String> selectedEmbeddables, List<Object> classes) {
        CgenConfiguration cgenConfiguration = getCurrentConfiguration();
        if(cgenConfiguration != null) {
            cgenConfiguration.getEmbeddables().clear();
            for(Embeddable embeddable : getSelectedEmbeddables(selectedEmbeddables, classes)) {
                cgenConfiguration.loadEmbeddable(embeddable.getClassName());
            }
        }
    }

    private List<ObjEntity> getSelectedEntities(Set<String> selectedEntities, List<Object> classes) {
        List<ObjEntity> selected = new ArrayList<>(selectedEntities.size());
        for (Object classObj : classes) {
            if(classObj instanceof ObjEntity) {
                String name = ((ObjEntity) classObj).getName();
                if(selectedEntities.contains(name)) {
                    selected.add(((ObjEntity) classObj));
                }
            }
        }

        return selected;
    }

    private List<Embeddable> getSelectedEmbeddables(Set<String> selectedEmbeddables, List<Object> classes) {
        List<Embeddable> selected = new ArrayList<>(selectedEmbeddables.size());
        for (Object classObj : classes) {
            if(classObj instanceof Embeddable) {
                String name = ((Embeddable) classObj).getClassName();
                if(selectedEmbeddables.contains(name)) {
                    selected.add((Embeddable) classObj);
                }
            }
        }

        return selected;
    }

    private void addToSelectedEmbeddables(Collection<String> embeddables, List<Object> classes) {
        getSelectedEmbeddables().addAll(embeddables);
        updateEmbeddables(getSelectedEmbeddables(), classes);
    }

    private void addToSelectedEntities(Collection<String> entities, List<Object> classes) {
        getSelectedEntities().addAll(entities);
        updateEntities(getSelectedEntities(), classes);
    }

    private void initCollectionsForSelection(DataMap dataMap) {
        selectedEntities = selectedEntitiesForDataMap.compute(dataMap, (key,value) ->
                value == null ? new HashSet<>() : value);
        selectedEmbeddables = selectedEmbeddablesForDataMap.compute(dataMap, (key, value) ->
                value == null ? new HashSet<>() : value);
        isDataMapSelected = selectedDataMaps.compute(dataMap, (key, value) ->
                value == null ? new HashSet<>() : value);
    }
}
