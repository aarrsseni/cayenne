package org.apache.cayenne.modeler;

import org.apache.cayenne.modeler.action.OpenProjectAction;
import org.apache.cayenne.modeler.action.SaveAction;
import org.apache.cayenne.modeler.dialog.FileDeletedDialog;
import org.apache.cayenne.modeler.event.ProjectFileChangeTrackerEvent;
import org.apache.cayenne.modeler.event.ProjectFileChangeTrackerListener;
import org.apache.cayenne.modeler.event.SaveFlagEvent;

import javax.swing.*;
import java.io.File;

/*
 * @since 4.1
 * Class is used to show file change dialog.
 */
public class ProjectFileChangeTrackerDisplay implements ProjectFileChangeTrackerListener{

    protected ProjectController projectController;

    public ProjectFileChangeTrackerDisplay(ProjectController projectController){
        this.projectController = projectController;
    }

    @Override
    public void doOnChange(ProjectFileChangeTrackerEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                e.getProjectFileChangeTracker().setShownChangeDialog(true);
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
                    projectController.fireSaveFlagEvent(new SaveFlagEvent(this,true));
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

    @Override
    public void doOnRemove(ProjectFileChangeTrackerEvent e) {
        if (projectController.getProject() != null) {

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    e.getProjectFileChangeTracker().setShownRemoveDialog(true);
                    FileDeletedDialog dialog = new FileDeletedDialog(Application.getFrame());
                    dialog.show();

                    if (dialog.shouldSave()) {
                        Application.getInstance().getActionManager().getAction(SaveAction.class).performAction(null);
                    } else if (dialog.shouldClose()) {
                        Application.getInstance().getFrameController().projectClosedAction();
                    } else {
                        projectController.fireSaveFlagEvent(new SaveFlagEvent(this, true));
                    }
                    e.getProjectFileChangeTracker().setShownRemoveDialog(false);
                }
            });
        }
    }

//    TODO Replace it with getEventController().addProjectFileChangeTrackerListener(e) when ProjectFileChangeTracker will be moved to core
    public void initAll(){
        projectController.getEventController().listenerMap.add(ProjectFileChangeTrackerListener.class, this);
    }
}
