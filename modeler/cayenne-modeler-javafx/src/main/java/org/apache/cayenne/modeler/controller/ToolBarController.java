package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.cayenne.modeler.BQApplication;
import org.apache.cayenne.modeler.action.SaveAsAction;

import java.io.IOException;

public class ToolBarController implements Unbindable{

    @Inject
    public SaveAsAction saveAction;

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

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() throws IOException {
        saveAction = BQApplication.getInjector().getInstance(SaveAsAction.class);
    }

    @Override
    public void unbind() {

    }

    @FXML
    public void saveAction(ActionEvent e) throws Exception {
        saveAction.handle(e);
    }

}
