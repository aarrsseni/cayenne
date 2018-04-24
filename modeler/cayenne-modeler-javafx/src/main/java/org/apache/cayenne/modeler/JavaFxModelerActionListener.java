package org.apache.cayenne.modeler;

import com.google.inject.Inject;
import org.apache.cayenne.modeler.action.listener.ProjectOpenActionListener;
import org.apache.cayenne.modeler.event.listener.ProjectOpenListener;

public class JavaFxModelerActionListener {

    @Inject
    public ProjectController projectController;

    @Inject
    public JavaFxModelerController controller;

    public void initListeners() {
        projectController.getEventController().addListener(ProjectOpenListener.class, new ProjectOpenActionListener(controller));
    }
}
