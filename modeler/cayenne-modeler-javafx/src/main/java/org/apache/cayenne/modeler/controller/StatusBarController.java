package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DomainDisplayListener;

public class StatusBarController implements Unbindable, DomainDisplayListener{

    @FXML
    public Label status;

    @Inject
    private ProjectController projectController;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        initListeners();
    }

    @Override
    public void bind() {
        System.out.println("Bind statusBarController");
    }

    @Override
    public void unbind() {
        System.out.println("Unbind statusBarController");
    }

    public void initListeners() {
        projectController.getEventController().addListener(DomainDisplayListener.class, this);
    }

    @Override
    public void currentDomainChanged(DomainDisplayEvent e) {
        status.setText(e.getDomain().getName() + " was created.");
    }
}
