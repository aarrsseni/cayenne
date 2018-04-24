package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.JavaFxModelerController;
import org.apache.cayenne.modeler.event.ProjectOpenEvent;
import org.apache.cayenne.modeler.event.listener.ProjectOpenListener;

public class ProjectOpenActionListener implements ProjectOpenListener{

    private JavaFxModelerController controller;

    public ProjectOpenActionListener(JavaFxModelerController controller) {
        this.controller = controller;
    }

    @Override
    public void openProject(ProjectOpenEvent e) {
        controller.projectOpenedAction(e.getProject());
    }

}
