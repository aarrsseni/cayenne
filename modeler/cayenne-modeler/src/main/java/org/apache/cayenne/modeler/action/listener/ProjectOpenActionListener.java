package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.CayenneModelerController;
import org.apache.cayenne.modeler.event.ProjectOpenEvent;
import org.apache.cayenne.modeler.event.ProjectOpenListener;

public class ProjectOpenActionListener implements ProjectOpenListener{

    protected CayenneModelerController projectController;

    public ProjectOpenActionListener(CayenneModelerController projectController){
        this.projectController = projectController;
    }

    @Override
    public void openProject(ProjectOpenEvent e) {
        projectController.projectOpenedAction(e.getProject());
    }

}
