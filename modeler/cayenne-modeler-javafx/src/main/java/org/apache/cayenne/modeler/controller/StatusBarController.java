package org.apache.cayenne.modeler.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.cayenne.modeler.BQApplication;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DomainDisplayListener;

public class StatusBarController implements Unbindable, DomainDisplayListener{

    @FXML
    public Label status;

    private ProjectController projectController;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        projectController = BQApplication.getInjector().getInstance(ProjectController.class);
        initListeners();
    }

    @Override
    public void unbind() {

    }

    public void initListeners() {
        projectController.getEventController().addListener(DomainDisplayListener.class, this);
    }

    @Override
    public void currentDomainChanged(DomainDisplayEvent e) {
        status.setText(e.getDomain().getName() + " was created.");
    }
}
