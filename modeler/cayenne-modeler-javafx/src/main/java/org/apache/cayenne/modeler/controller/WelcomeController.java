package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.apache.cayenne.modeler.ModelerPreferences;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.action.NewProjectAction;
import org.apache.cayenne.modeler.services.OpenProjectService;
import org.apache.cayenne.modeler.services.util.OpenProjectStatus;
import org.apache.cayenne.resource.Resource;

import java.io.File;

public class WelcomeController{

    @FXML
    private Pane welcomeScreenParent;

    @FXML
    private Pane welcomeScreen;

    @FXML
    private Pane rootPane;

    @FXML
    private ListView<File> recentFiles;

    @Inject
    ScreenController screenController;

    @Inject
    NewProjectAction newProjectAction;

    @Inject
    public ProjectController projectController;

    @Inject
    OpenProjectService openProjectService;

    public WelcomeController(){}

    private ObservableList<File> items;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        items = FXCollections.observableArrayList (ModelerPreferences.getLastProjFiles());
        recentFiles.setItems(items);

        makeResizable();

        recentFiles.setOnMouseClicked(val -> {
            projectOpener(recentFiles.getSelectionModel().getSelectedItem());
        });
    }

    @FXML
    public void openProjectAction(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File res = fileChooser.showOpenDialog(screenController.getPrimaryStage());

        if (res == null) {
            try {
                // Get the project file name (always cayenne.xml)

            } catch (Exception ex) {

            }
        }

        if (res != null) {
            // by now if the project is unsaved, this has been a user choice...
//            if (projectController != null) {
//                return;
//            }

            openProject(res);
        }
    }

    @FXML
    public void newProjectAction(ActionEvent e) {
        newProjectAction.handle(e);
    }

    private void makeResizable() {
        rootPane.heightProperty().addListener((arg0, arg1, arg2) -> {
            welcomeScreenParent.setPrefHeight(arg2.doubleValue());
        });
        rootPane.widthProperty().addListener((arg0, arg1, arg2) -> {
            welcomeScreenParent.setPrefWidth(arg2.doubleValue());
        });

        welcomeScreenParent.heightProperty().addListener((arg0, arg1, arg2) -> {
            welcomeScreen.setPrefHeight(arg2.doubleValue() * 0.8);
        });
        welcomeScreenParent.widthProperty().addListener((arg0, arg1, arg2) -> {
            welcomeScreen.setPrefWidth(arg2.doubleValue() * 0.8);
        });
    }

    private void projectOpener(File file){
//        if(projectController != null){
//            return;
//        }

        File f = file;

        if (f == null) {
            try {
                // Get the project file name (always cayenne.xml)

            } catch (Exception ex) {

            }
        }

        if (f != null) {
            // by now if the project is unsaved, this has been a user choice...
//            if (projectController != null) {
//                return;
//            }

            openProject(f);
        }
    }

    /** Opens specified project file. File must already exist. */
    public void openProject(File file) {
        try {
            OpenProjectStatus status = openProjectService.canOpen(file);

            Resource rootSource = openProjectService.getRootSource(file);
            switch (status.getProjectStatus()) {
                case ERROR:
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Error");
                    alert.setContentText("Error");
                    alert.showAndWait();
                    closeProject(false);
                    return;
                case UPGRADE_NEEDED:
                    if (processUpgrades()) {
                        rootSource = openProjectService.upgradeResource(rootSource);
                    } else {
                        closeProject(false);
                        return;
                    }
                    break;
            }
            openProjectService.openProjectResourse(rootSource);
        } catch (Exception ex) {

        }
    }

    private boolean processUpgrades() {
        // need an upgrade
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Error");
        alert.setContentText("Error");
        alert.showAndWait();
        return alert.getResult() != ButtonType.NO;
    }

    /** Returns true if successfully closed project, false otherwise. */
    public boolean closeProject(boolean checkUnsaved) {
        // check if there is a project...
        if (projectController == null || projectController.getProject() == null) {
            return true;
        }

        if (checkUnsaved) {
            return false;
        }

//        CayenneModelerController controller = Application
//                .getInstance()
//                .getFrameController();
//
//        application.getUndoManager().discardAllEdits();
//
//        controller.projectClosedAction();

        return true;
    }
}
