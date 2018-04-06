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

import org.apache.cayenne.modeler.action.OpenProjectAction;
import org.apache.cayenne.modeler.action.SaveAction;
import org.apache.cayenne.modeler.dialog.FileDeletedDialog;
import org.apache.cayenne.modeler.event.ProjectDirtyEvent;
import org.apache.cayenne.modeler.event.ProjectFileOnChangeTrackerEvent;
import org.apache.cayenne.modeler.event.listener.ProjectFileOnChangeEventListener;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.File;

/**
 * @since 4.1
 * Class is used to show file change dialog.
 */
public class ProjectFileChangeTrackerDisplay implements ProjectFileOnChangeEventListener {

    protected ProjectController projectController;

    public ProjectFileChangeTrackerDisplay(ProjectController projectController){
        this.projectController = projectController;
    }

    /**
     *
     * @since 4.1
     */
    @Override
    public void onChange(ProjectFileOnChangeTrackerEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                e.getProjectFileChangeTracker().setShownChangeDialog(true);
                if("Change".equals(e.getType())) {
                    onChangeDisplay();
                }
                else {
                    onRemoveDisplay();
                }
                e.getProjectFileChangeTracker().setShownChangeDialog(false);
            }
        });
    }

    /**
     * Shows confirmation dialog
     */
    private boolean showConfirmation(String message) {
        return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Application.getFrame(), message, "File changed",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    private void onChangeDisplay() {
        if (showConfirmation("One or more project files were changed by external program. "
                + "Do you want to load the changes?")) {

            // Currently we are reloading all project
            if (projectController.getProject() != null) {

                File fileDirectory = new File(projectController.getProject().getConfigurationResource().getURL()
                        .getPath());
                Application.getInstance().getActionManager().getAction(OpenProjectAction.class)
                        .openProject(fileDirectory);
            }
        } else {
            projectController.fireEvent(new ProjectDirtyEvent(this,true));
        }
    }

    private void onRemoveDisplay() {
        FileDeletedDialog dialog = new FileDeletedDialog(Application.getFrame());
        dialog.show();

        if (dialog.shouldSave()) {
            Application.getInstance().getActionManager().getAction(SaveAction.class).performAction(null);
        } else if (dialog.shouldClose()) {
            Application.getInstance().getFrameController().projectClosedAction();
        } else {
            projectController.fireEvent(new ProjectDirtyEvent(this, true));
        }
    }

    public void initAll(){
        projectController.getEventController().addProjectFileOnChangeEventListener(this);
    }
}
