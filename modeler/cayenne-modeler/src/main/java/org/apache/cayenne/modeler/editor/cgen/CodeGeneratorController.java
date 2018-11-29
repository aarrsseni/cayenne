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

import org.apache.cayenne.configuration.event.DataMapEvent;
import org.apache.cayenne.configuration.event.DataMapListener;
import org.apache.cayenne.gen.CgenConfiguration;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.event.EmbeddableEvent;
import org.apache.cayenne.map.event.EmbeddableListener;
import org.apache.cayenne.map.event.EntityEvent;
import org.apache.cayenne.map.event.ObjEntityListener;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.action.CgenAction;
import org.apache.cayenne.modeler.editor.DbImportController;
import org.apache.cayenne.modeler.event.ProjectDirtyEvent;
import org.apache.cayenne.modeler.util.CayenneController;
import org.apache.cayenne.swing.BindingBuilder;

import java.awt.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

/**
 * @since 4.1
 * A controller for the class generator dialog.
 */
public class CodeGeneratorController extends CodeGeneratorControllerBase implements ObjEntityListener, EmbeddableListener, DataMapListener {

    protected CodeGeneratorPane view;

    protected ClassesTabController classesSelector;
    protected GeneratorTabController generatorSelector;
    private ConcurrentMap<DataMap, GeneratorController> prevGeneratorController;

    public CodeGeneratorController(CayenneController parent, ProjectController projectController) {
        super(parent, projectController);
        this.classesSelector = new ClassesTabController(this);
        this.generatorSelector = new GeneratorTabController(this);
        view = new CodeGeneratorPane(generatorSelector.getView(), classesSelector.getView());
        this.prevGeneratorController = new ConcurrentHashMap<>();
        initBindings();
        initListeners();
    }

    public void startup(DataMap dataMap) {
        super.startup(dataMap);
        classesSelectedAction();
        CgenConfiguration cgenConfiguration = createConfiguration();
        GeneratorController modeController = prevGeneratorController.get(dataMap) != null ? prevGeneratorController.get(dataMap) : cgenConfiguration.isClient() ?
                generatorSelector.getClientGeneratorController() : generateCodeService.isDefaultConfig(cgenConfiguration) ?
                generatorSelector.getStandartController() : generatorSelector.getCustomModeController();
        prevGeneratorController.put(dataMap, modeController);
        generatorSelector.setSelectedController(modeController);
        classesSelector.startup();
        initFromModel = false;
        validate(modeController);
    }

    private void initListeners(){
        projectController.getEventController().addListener(ObjEntityListener.class, this);
        projectController.getEventController().addListener(EmbeddableListener.class, this);
        projectController.getEventController().addListener(DataMapListener.class, this);
    }

    @Override
    public Component getView() {
        return view;
    }

    protected void initBindings() {
        BindingBuilder builder = new BindingBuilder(
                getApplication().getBindingFactory(),
                this);

        builder.bindToAction(view.getGenerateButton(), "generateAction()");
        builder.bindToAction(this, "classesSelectedAction()", SELECTED_PROPERTY);
        builder.bindToAction(generatorSelector, "generatorSelectedAction()",
                GeneratorTabController.GENERATOR_PROPERTY);

        generatorSelectedAction();
    }

    public void generatorSelectedAction() {
        GeneratorController controller = generatorSelector.getGeneratorController();
        validate(controller);

        Predicate<Object> predicate = controller != null
                ? controller.getDefaultClassFilter()
                : o -> false;

        updateSelection(predicate);
        classesSelector.classSelectedAction();
    }

    public void classesSelectedAction() {
        if(!isInitFromModel()) {
            projectController.fireEvent(new ProjectDirtyEvent(this, true));
        }
    }

    public void generateAction() {
        CgenConfiguration cgenConfiguration = createConfiguration();
        Application.getInstance().getActionManager().getAction(CgenAction.class).performAction(cgenConfiguration);
    }

    public ConcurrentMap<DataMap, GeneratorController> getPrevGeneratorController() {
        return prevGeneratorController;
    }

    public void enableGenerateButton(boolean enable) {
        view.getGenerateButton().setEnabled(enable);
    }

    @Override
    public void objEntityChanged(EntityEvent e) {}

    @Override
    public void objEntityAdded(EntityEvent e) {
        super.addEntity(e.getEntity().getDataMap(), (ObjEntity) e.getEntity());
    }

    @Override
    public void objEntityRemoved(EntityEvent e) {
       generateCodeService.removeEntity((ObjEntity) e.getEntity());
    }

    @Override
    public void embeddableChanged(EmbeddableEvent e) {}

    @Override
    public void embeddableAdded(EmbeddableEvent e) {
        super.addEmbeddable(e.getEmbeddable().getDataMap(), e.getEmbeddable());
    }

    @Override
    public void embeddableRemoved(EmbeddableEvent e) {
        generateCodeService.removeEmbeddable(e.getEmbeddable());
    }

    @Override
    public void dataMapChanged(DataMapEvent e) {
        if(e.getSource() instanceof DbImportController) {
            CgenConfiguration cgenConfiguration = projectController
                    .getGenerateCodeService()
                    .getCurrentConfiguration();
            if(cgenConfiguration != null) {
                for(ObjEntity objEntity : e.getDataMap().getObjEntities()) {
                    if(!cgenConfiguration.getExcludeEntityArtifacts().contains(objEntity.getName())) {
                        addEntity(cgenConfiguration.getDataMap(), objEntity);
                    }
                }
            }
        }
    }

    @Override
    public void dataMapAdded(DataMapEvent e) {}

    @Override
    public void dataMapRemoved(DataMapEvent e) {}
}
