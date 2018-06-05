package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import org.apache.cayenne.modeler.ModelerPreferences;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.action.NewProjectAction;
import org.apache.cayenne.modeler.action.OpenProjectAction;

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
    public OpenProjectAction openProjectAction;

    public WelcomeController(){}

    private ObservableList<File> items;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        items = FXCollections.observableArrayList (ModelerPreferences.getLastProjFiles());
        recentFiles.setItems(items);

        makeResizable();

        recentFiles.setOnMouseClicked(val -> {
            openProjectAction.projectOpener(recentFiles.getSelectionModel().getSelectedItem());
        });
    }

    @FXML
    public void openProjectAction(ActionEvent e) {
        openProjectAction.handle(e);
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
}
