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
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.CayenneModelerController;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.UnsavedChangesDialog;
import org.apache.cayenne.modeler.util.CayenneAction;

import java.awt.event.ActionEvent;

/**
 */
public class ProjectAction extends CayenneAction {

    @Inject
    Application application;

    @Inject
    public SaveAction saveAction;

    public static String getActionName() {
        return "Close Project";
    }

    public ProjectAction() {
        super(getActionName());
    }

    /**
     * Constructor for ProjectAction.
     * 
     * @param name
     */
    public ProjectAction(String name) {
        super(name);
    }

    /**
     * Closes current project.
     */
    public void performAction(ActionEvent e) {
        closeProject(true);
    }

    /** Returns true if successfully closed project, false otherwise. */
    public boolean closeProject(boolean checkUnsaved) {
        // check if there is a project...
        if (getProjectController() == null || getProjectController().getProject() == null) {
            return true;
        }

        if (checkUnsaved && !checkSaveOnClose()) {
            return false;
        }

        CayenneModelerController controller = Application
                .getInstance()
                .getFrameController();

        application.getUndoManager().discardAllEdits();

        controller.projectClosedAction();

        return true;
    }

    /**
     * Returns false if cancel closing the window, true otherwise.
     */
    public boolean checkSaveOnClose() {
        ProjectController projectController = getProjectController();
        if (projectController != null && projectController.isDirty()) {
            UnsavedChangesDialog dialog = new UnsavedChangesDialog(Application.getFrame());
            dialog.show();

            if (dialog.shouldCancel()) {
                // discard changes and DO NOT close
                return false;
            }
            else if (dialog.shouldSave()) {
                // save changes and close
                ActionEvent e = new ActionEvent(
                        this,
                        ActionEvent.ACTION_PERFORMED,
                        "SaveAll");
                saveAction.actionPerformed(e);
                if (projectController.isDirty()) {
                    // save was canceled... do not close
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Always returns true.
     */
    public boolean enableForPath(Object object) {
        return true;
    }
}
