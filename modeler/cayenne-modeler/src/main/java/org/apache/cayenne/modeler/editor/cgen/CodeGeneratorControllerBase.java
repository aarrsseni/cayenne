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

package org.apache.cayenne.modeler.editor.cgen;

import org.apache.cayenne.gen.CgenConfiguration;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.pref.GeneralPreferences;
import org.apache.cayenne.modeler.services.GenerateCodeService;
import org.apache.cayenne.modeler.util.CayenneController;
import org.apache.cayenne.modeler.util.CellRenderers;
import org.apache.cayenne.modeler.util.ModelerUtil;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

/**
 * @since 4.1
 * A base superclass of a top controller for the code generator. Defines all common model
 * parts used in class generation.
 *
 */
public abstract class CodeGeneratorControllerBase extends CayenneController {

    public static final String SELECTED_PROPERTY = "selected";

    protected DataMap dataMap;
    protected ValidationResult validation;
    protected List<Object> classes;
    protected transient Object currentClass;
    protected ProjectController projectController;

    protected GenerateCodeService generateCodeService;

    protected boolean initFromModel;

    public CodeGeneratorControllerBase(CayenneController parent, ProjectController projectController) {
        super(parent);
        this.projectController = projectController;
        this.classes = new ArrayList<>();
        this.generateCodeService = projectController.getGenerateCodeService();
        generateCodeService.initCollections();
    }

    public void startup(DataMap dataMap){
        initFromModel = true;
        this.dataMap = dataMap;
        this.classes = generateCodeService.prepareClasses(dataMap);
    }

    /**
     * Creates a class generator for provided selections.
     */
    public CgenConfiguration createConfiguration() {
        CgenConfiguration cgenConfiguration = generateCodeService.getConfiguration(classes);

        if(cgenConfiguration != null) {
            return cgenConfiguration;
        }

        Path basePath = Paths.get(ModelerUtil.initOutputFolder());
        // no destination folder
        if (basePath == null) {
            JOptionPane.showMessageDialog(this.getView(), "Select directory for source files.");
            return null;
        }

        // no such folder
        if (!Files.exists(basePath)) {
            try {
                Files.createDirectories(basePath);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this.getView(), "Can't create directory. " +
                        ". Select a different one.");
                return null;
            }
        }

        // not a directory
        if (!Files.isDirectory(basePath)) {
            JOptionPane.showMessageDialog(this.getView(), basePath + " is not a valid directory.");
            return null;
        }
        cgenConfiguration = generateCodeService.createConfiguration(basePath, classes);
        Preferences preferences = application.getPreferencesNode(GeneralPreferences.class, "");
        if (preferences != null) {
            cgenConfiguration.setEncoding(preferences.get(GeneralPreferences.ENCODING_PREFERENCE, null));
        }
        return generateCodeService.createConfiguration(basePath, classes);
    }

    public List<Object> getClasses() {
        return classes;
    }

    public abstract Component getView();

    public void validate(GeneratorController validator) {

        ValidationResult validationBuffer = new ValidationResult();

        if (validator != null) {
            for (Object classObj : classes) {
                if (classObj instanceof ObjEntity) {
                    generateCodeService
                            .getCgenValidationService()
                            .validateEntity(
                            validationBuffer,
                            (ObjEntity) classObj,
                            false);
                }
                else if (classObj instanceof Embeddable) {
                    generateCodeService
                            .getCgenValidationService()
                            .validateEmbeddable(validationBuffer, (Embeddable) classObj);
                }
            }

        }

        this.validation = validationBuffer;
    }

    public boolean updateSelection(Predicate<Object> predicate) {
        boolean modified = false;

        for (Object classObj : classes) {
            boolean select = predicate.test(classObj);
            if (classObj instanceof ObjEntity) {

                if (select) {
                    if(generateCodeService.getSelectedEntities().add(((ObjEntity) classObj).getName())) {
                        modified = true;
                    }
                }
                else {
                    if(generateCodeService.getSelectedEntities().remove(((ObjEntity) classObj).getName())) {
                        modified = true;
                    }
                }
            }
            else if (classObj instanceof Embeddable) {
                if (select) {
                    if(generateCodeService.getSelectedEmbeddables().add(((Embeddable) classObj).getClassName())) {
                        modified = true;
                    }
                }
                else {
                    if(generateCodeService.getSelectedEmbeddables().remove(((Embeddable) classObj).getClassName())) {
                        modified = true;
                    }
                }
            } else if (classObj instanceof DataMap) {
                generateCodeService.updateArtifactGenerationMode(classObj, select);
                if(select) {
                    if(generateCodeService.isDataMapSelected().add(((DataMap) classObj).getName())) {
                        modified = true;
                    }
                } else {
                    if(generateCodeService.isDataMapSelected().remove(((DataMap) classObj).getName())) {
                        modified = true;
                    }
                }
            }

        }

        if (modified) {
            firePropertyChange(SELECTED_PROPERTY, null, null);
        }

        return modified;
    }

    /**
     * Returns the first encountered validation problem for an antity matching the name or
     * null if the entity is valid or the entity is not present.
     */
    public String getProblem(Object obj) {

        String name = null;

        if (obj instanceof ObjEntity) {
            name = ((ObjEntity) obj).getName();
        }
        else if (obj instanceof Embeddable) {
            name = ((Embeddable) obj).getClassName();
        }
        
        if (validation == null) {
            return null;
        }

        List failures = validation.getFailures(name);
        if (failures.isEmpty()) {
            return null;
        }

        return ((ValidationFailure) failures.get(0)).getDescription();
    }

    public boolean isSelected() {
        if (currentClass instanceof ObjEntity) {
            return generateCodeService.getSelectedEntities()
                    .contains(((ObjEntity) currentClass).getName());
        }
        if (currentClass instanceof Embeddable) {
            return generateCodeService.getSelectedEmbeddables()
                    .contains(((Embeddable) currentClass).getClassName());
        }
        if(currentClass instanceof DataMap) {
            return generateCodeService.isDataMapSelected()
                    .contains(((DataMap) currentClass).getName());
        }
        return false;
    }

    public void setSelected(boolean selectedFlag) {
        if (currentClass == null) {
            return;
        }
        if (currentClass instanceof ObjEntity) {
            if (selectedFlag) {
                if (generateCodeService.getSelectedEntities().add(((ObjEntity) currentClass).getName())) {
                    firePropertyChange(SELECTED_PROPERTY, null, null);
                }
            } else {
                if (generateCodeService.getSelectedEntities().remove(((ObjEntity) currentClass).getName())) {
                    firePropertyChange(SELECTED_PROPERTY, null, null);
                }
            }
        }
        if (currentClass instanceof Embeddable) {
            if (selectedFlag) {
                if (generateCodeService.getSelectedEmbeddables().add(((Embeddable) currentClass).getClassName())) {
                    firePropertyChange(SELECTED_PROPERTY, null, null);
                }
            } else {
                if (generateCodeService.getSelectedEmbeddables()
                        .remove(((Embeddable) currentClass).getClassName())) {
                    firePropertyChange(SELECTED_PROPERTY, null, null);
                }
            }
        }
        if(currentClass instanceof DataMap) {
            generateCodeService.updateArtifactGenerationMode(currentClass, selectedFlag);
            if(selectedFlag) {
                if(generateCodeService.isDataMapSelected().add(dataMap.getName())) {
                    firePropertyChange(SELECTED_PROPERTY, null, null);
                }
            } else {
                if(generateCodeService.isDataMapSelected()
                        .remove(((DataMap) currentClass).getName())) {
                    firePropertyChange(SELECTED_PROPERTY, null, null);
                }
            }
        }
    }

    public JLabel getItemName(Object obj) {
        String className;
        Icon icon;
        if (obj instanceof Embeddable) {
            className = ((Embeddable) obj).getClassName();
            icon = CellRenderers.iconForObject(new Embeddable());
        } else if(obj instanceof ObjEntity) {
            className = ((ObjEntity) obj).getName();
            icon = CellRenderers.iconForObject(new ObjEntity());
        } else {
            className = ((DataMap) obj).getName();
            icon = CellRenderers.iconForObject(new DataMap());
        }
        JLabel labelIcon = new JLabel();
        labelIcon.setIcon(icon);
        labelIcon.setVisible(true);
        labelIcon.setText(className);
        return labelIcon;
    }

    void addEntity(DataMap dataMap, ObjEntity objEntity) {
        classes = generateCodeService.prepareClasses(dataMap);
        generateCodeService.loadEntity(objEntity);
    }

    void addEmbeddable(DataMap dataMap, Embeddable embeddable) {
        this.classes = generateCodeService.prepareClasses(dataMap);
        generateCodeService.loadEmbeddable(embeddable.getClassName());
    }

    public int getSelectedEntitiesSize() {
        return generateCodeService.getSelectedEntities() != null ? generateCodeService.getSelectedEntities().size() : 0;
    }

    public int getSelectedEmbeddablesSize() {
        return generateCodeService.getSelectedEmbeddables() != null ? generateCodeService.getSelectedEmbeddables().size() : 0;
    }

    public boolean isDataMapSelected() {
        return generateCodeService.isDataMapSelected() != null && generateCodeService.isDataMapSelected().size() == 1;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public ProjectController getProjectController() {
        return projectController;
    }

    public Object getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(Object currentClass) {
        this.currentClass = currentClass;
    }

    public boolean isInitFromModel() {
        return initFromModel;
    }

    public void setInitFromModel(boolean initFromModel) {
        this.initFromModel = initFromModel;
    }

    public abstract void enableGenerateButton(boolean enabled);
}
