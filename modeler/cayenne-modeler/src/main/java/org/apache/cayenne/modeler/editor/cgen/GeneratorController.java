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

import org.apache.cayenne.gen.ArtifactsGenerationMode;
import org.apache.cayenne.gen.CgenConfiguration;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.pref.BaseFileChooser;
import org.apache.cayenne.modeler.pref.FSPath;
import org.apache.cayenne.modeler.pref.adapter.JFileChooserAdapter;
import org.apache.cayenne.modeler.util.CayenneController;
import org.apache.cayenne.modeler.util.TextAdapter;
import org.apache.cayenne.swing.BindingBuilder;
import org.apache.cayenne.util.Util;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.io.File;
import java.util.function.Predicate;

/**
 * @since 4.1
 * A mode-specific part of the code generation dialog.
 */
public abstract class GeneratorController extends CayenneController {

    protected String mode = ArtifactsGenerationMode.ENTITY.getLabel();
    protected CgenConfiguration cgenConfiguration;

    public GeneratorController(CodeGeneratorControllerBase parent) {
        super(parent);

        createView();
        initBindings(new BindingBuilder(getApplication().getBindingFactory(), this));
    }

    protected void initBindings(BindingBuilder bindingBuilder) {
        JButton outputSelect = ((GeneratorControllerPanel) getView()).getSelectOutputFolder();
        bindingBuilder.bindToAction(outputSelect, "selectOutputFolderAction()");
    }

    protected CodeGeneratorControllerBase getParentController() {
        return (CodeGeneratorControllerBase) getParent();
    }

    protected abstract GeneratorControllerPanel createView();

    protected void initForm(CgenConfiguration cgenConfiguration) {
        this.cgenConfiguration = cgenConfiguration;
        ((GeneratorControllerPanel)getView()).getOutputFolder().setText(cgenConfiguration.buildPath().toString());
        if(cgenConfiguration.getArtifactsGenerationMode().equalsIgnoreCase("all")) {
            ((CodeGeneratorControllerBase) parent).setCurrentClass(cgenConfiguration.getDataMap());
            ((CodeGeneratorControllerBase) parent).setSelected(true);
        }
    }

    public abstract void updateConfiguration(CgenConfiguration cgenConfiguration);

    /**
     * Returns a predicate for default entity selection in a given mode.
     */
    Predicate getDefaultClassFilter() {
        return object -> {
            if (object instanceof ObjEntity) {
                return getParentController().getProblem(((ObjEntity) object).getName()) == null;
            }

            if (object instanceof Embeddable) {
                return getParentController().getProblem(((Embeddable) object).getClassName()) == null;
            }

            return false;
        };
    }

    /**
     * An action method that pops up a file chooser dialog to pick the
     * generation directory.
     */
    public void selectOutputFolderAction() {

        TextAdapter outputFolder = ((GeneratorControllerPanel) getView()).getOutputFolder();

        String currentDir = outputFolder.getComponent().getText();

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);

        // guess start directory
        if (!Util.isEmptyString(currentDir)) {
            chooser.setCurrentDirectory(new File(currentDir));
        } else {
            FSPath lastDir = Application.getInstance().getFrameController().getLastDirectory();
            BaseFileChooser baseFileChooser = new JFileChooserAdapter(chooser);
            lastDir.updateChooser(baseFileChooser);
        }

        int result = chooser.showOpenDialog(getView());
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();

            // update model
            String path = selected.getAbsolutePath();
            ((GeneratorControllerPanel) getView()).getOutputFolder().setText(path);
            ((GeneratorControllerPanel) getView()).getOutputFolder().updateModel();
        }
    }
}
