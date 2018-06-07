package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.JavaFxModelerController;

public class ProjectAction extends AbstractCayenneAction{

    @Inject
    public ProjectController projectController;

    @Inject
    public JavaFxModelerController javaFxModelerController;

    @Override
    public void handle(Event event) {

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

        javaFxModelerController.projectClosedAction();

        return true;
    }
}
