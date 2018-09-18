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

package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.validator.ValidatorDialog;
import org.apache.cayenne.modeler.event.ProjectOnSaveEvent;
import org.apache.cayenne.modeler.services.ProjectService;
import org.apache.cayenne.modeler.services.SaveService;
import org.apache.cayenne.modeler.util.CayenneAction;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.project.validation.ProjectValidator;
import org.apache.cayenne.validation.ValidationResult;

import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * A "Save As" action that allows user to pick save location.
 * 
 */
public class SaveAsAction extends CayenneAction {

    @Inject
    public Application application;

    @Inject
    public ProjectController projectController;

    @Inject
    private ProjectOpener fileChooser;

    @Inject
    public ProjectService projectService;

    @Inject
    public SaveService saveService;

    public static String getActionName() {
        return "Save As...";
    }

    public SaveAsAction() {
        this(getActionName());
    }

    protected SaveAsAction(String name) {
        super(name);
        this.fileChooser = new ProjectOpener();
    }

    @Override
    public KeyStroke getAcceleratorKey() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                | ActionEvent.SHIFT_MASK);
    }

    /**
     * Saves project and related files. Saving is done to temporary files, and
     * only on successful save, master files are replaced with new versions.
     */
    protected boolean saveAll() throws Exception {

        File projectDir = fileChooser.newProjectDir(Application.getFrame(), projectController.getProject());
        if (projectDir == null) {
            return false;
        }

        return saveService.saveAll(projectDir);
    }

    /**
     * This method is synchronized to prevent problems on double-clicking
     * "save".
     */
    @Override
    public void performAction(ActionEvent e) {
        performAction();
    }

    public void performAction() {

        ProjectValidator projectValidator = projectController.getInjector().getInstance(ProjectValidator.class);
        ValidationResult validationResult = projectValidator.validate(getCurrentProject().getRootNode());
        
        projectController.fireEvent(new ProjectOnSaveEvent(SaveAsAction.class));
        try {
            if (!saveAll()) {
                return;
            }
        } catch (Exception ex) {
            throw new CayenneRuntimeException("Error on save", ex);
        }

        application.getFrameController().projectSavedAction();

        // If there were errors or warnings at validation, display them
        if (validationResult.getFailures().size() > 0) {
            ValidatorDialog.showDialog(Application.getFrame(), validationResult.getFailures());
        }
    }

    /**
     * Returns <code>true</code> if path contains a Project object and the
     * project is modified.
     */
    @Override
    public boolean enableForPath(ConfigurationNode object) {
        if (object == null) {
            return false;
        }

        Project project = application.getProject();
        return project != null && project.isModified();
    }
}
