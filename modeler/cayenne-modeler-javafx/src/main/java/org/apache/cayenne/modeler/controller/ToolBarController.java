package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.cayenne.modeler.action.CreateDataMapAction;
import org.apache.cayenne.modeler.action.NewProjectAction;
import org.apache.cayenne.modeler.action.SaveAsAction;
import org.apache.cayenne.modeler.services.DbEntityService;

import java.io.IOException;

public class ToolBarController implements Unbindable{

    @Inject
    private SaveAsAction saveAction;

    @Inject
    private CreateDataMapAction createDataMapAction;

    @Inject
    private DbEntityService dbEntityService;

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
    private NewProjectAction newProjectAction;

    @Inject
    public ScreenController screenController;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() throws IOException {
    }

    @Override
    public void bind() {
        System.out.println("Bind toolBarController");
    }

    @Override
    public void unbind() {
        System.out.println("Unbind toolBarController");
    }

    @FXML
    public void createDataMapAction(ActionEvent e) {
        createDataMapAction.handle(e);
    }

    @FXML
    public void saveAction(ActionEvent e) throws Exception {
        saveAction.handle(e);
    }

    @FXML
    public void createDbEntityAction(ActionEvent e) throws Exception {
        dbEntityService.createDbEntity();
    }

}
