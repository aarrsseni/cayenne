package org.apache.cayenne.modeler.controller;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class ScreenController {

    private Scene scene;
    private Stage primaryStage;
    private Unbindable unbindableController;

    private Set<Unbindable> controllers = new HashSet<>();

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void updateScene(Pane pane) {
        scene.setRoot(pane);
    }

    public Scene getScene() {
        return scene;
    }

    public void setUnbindableController(Unbindable unbindableController) {
        controllers.add(unbindableController);

        if(this.unbindableController != null) {
            unbindableController.unbind();
        }

        this.unbindableController = unbindableController;
    }
}
