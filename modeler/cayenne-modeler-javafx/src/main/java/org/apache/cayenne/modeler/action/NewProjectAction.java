package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.controller.JavaFxModelerController;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.ProjectService;

public class NewProjectAction extends ProjectAction{

    @Inject
    public ProjectService projectService;

    @Inject
    public ProjectController projectController;

    @Inject
    public JavaFxModelerController controller;

    @Override
    public void handle(Event event) {
        if (projectController.getProject() != null && !closeProject(true)) {
            return;
        }
        projectService.newProject();
    }
}
