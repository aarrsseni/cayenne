package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.ProjectService;
import org.apache.cayenne.modeler.util.AbstractCayenneAction;

public class NewProjectAction extends AbstractCayenneAction{

    @Inject
    public ProjectService projectService;

    @Inject
    public ProjectController projectController;

    private static final String NAME = "New Project Action";

    @Override
    public void handle(Event event) {
        if (projectController.getProject() != null) {
            return;
        }
        projectService.newProject();
    }
}
