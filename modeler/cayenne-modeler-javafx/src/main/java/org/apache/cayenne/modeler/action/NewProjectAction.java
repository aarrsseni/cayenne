package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.JavaFxModelerController;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.ProjectService;
import org.apache.cayenne.modeler.util.AbstractCayenneAction;

public class NewProjectAction extends AbstractCayenneAction{

    @Inject
    public ProjectService projectService;

    @Inject
    public ProjectController projectController;

    @Inject
    public JavaFxModelerController controller;

    private static final String NAME = "New Project Action";

    @Override
    public void handle(Event event) {
        if (projectController.getProject() != null && !closeProject(true)) {
            return;
        }
        projectService.newProject();
    }

    /** Returns true if successfully closed project, false otherwise. */
    public boolean closeProject(boolean checkUnsaved) {
        // check if there is a project...
        if (projectController == null || projectController.getProject() == null) {
            return true;
        }

//        if (checkUnsaved && !checkSaveOnClose()) {
//            return false;
//        }
//
//        CayenneModelerController controller = Application
//                .getInstance()
//                .getFrameController();
//
//        application.getUndoManager().discardAllEdits();

        controller.projectClosedAction();

        return true;
    }
}
