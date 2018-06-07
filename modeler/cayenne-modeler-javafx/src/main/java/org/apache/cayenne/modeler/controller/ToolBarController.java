package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.cayenne.modeler.action.*;

import java.io.IOException;

public class ToolBarController implements Unbindable{

    @Inject
    private SaveAsAction saveAction;

    @Inject
    private CreateDataMapAction createDataMapAction;

    @Inject
    private CreateDbEntityAction createDbEntityAction;

    @Inject
    private CreateObjEntityAction createObjEntityAction;

    @Inject
    private NewProjectAction newProjectAction;

    @FXML
    private Button moveBackwardButton;

    @FXML
    private Button moveForwardButton;

    @FXML
    private Button newProjectButton;

    @FXML
    private Button openProjectButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cutButton;

    @FXML
    private Button copyButton;

    @FXML
    private Button pasteButton;

    @FXML
    private Button undoButton;

    @FXML
    private Button redoButton;

    @FXML
    private Button createDataNodeButton;

    @FXML
    private Button createDataMapButton;

    @FXML
    private Button createDbEntityButton;

    @FXML
    private Button createStoredProcedureButton;

    @FXML
    private Button createObjEntityButton;

    @FXML
    private Button createEmbeddableButton;

    @FXML
    private Button createQueryButton;

    @Inject
    public ScreenController screenController;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() throws IOException {
    }

    @Override
    public void bind() {
    }

    @Override
    public void unbind() {
    }

    @FXML
    public void createDataMapAction(ActionEvent e) {
        createDataMapAction.handle(e);
    }

    @FXML
    public void saveAction(ActionEvent e) {
        saveAction.handle(e);
    }

    @FXML
    public void createDbEntityAction(ActionEvent e) {
        createDbEntityAction.handle(e);
    }

    @FXML
    public void createObjEntityAction(ActionEvent e) {
        createObjEntityAction.handle(e);
    }
}
