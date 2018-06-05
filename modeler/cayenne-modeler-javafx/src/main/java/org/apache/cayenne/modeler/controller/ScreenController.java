package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.cayenne.modeler.FXMLLoaderFactory;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScreenController {

    private Scene scene;
    private Stage primaryStage;
    private Unbindable unbindableController;

    private Stage currentPopStage;

    @Inject
    private FXMLLoaderFactory fxmlLoaderFactory;

    private Set<Unbindable> controllers = new HashSet<>();

    private Map<Pane, Unbindable> controllersCache = new HashMap<>();

    private Map<String, Pane> panesCache = new HashMap<>();

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
        this.unbindableController = unbindableController;
    }

    public void addController(String key, Pane pane, Unbindable controller) {
        if(!panesCache.containsKey(key)) {
            panesCache.put(key, pane);
            controllersCache.put(pane, controller);
            controller.bind();
        }
    }

    public void clearScene(Pane parentPane) {
        for (Node node : parentPane.getChildren()) {
            controllersCache.get(node).unbind();
        }

        parentPane.getChildren().clear();
    }

    public Map<Pane, Unbindable> getControllersCache() {
        return controllersCache;
    }

    public Map<String, Pane> getPanesCache() {
        return panesCache;
    }

    public void loadAndUpdatePane(Pane parentPane, String path) {

        if(getPanesCache().containsKey(path)) {
            controllersCache.get(getPanesCache().get(path)).unbind();

            clearScene(parentPane);

            parentPane.getChildren().add(getPanesCache().get(path));
            getControllersCache().get(getPanesCache().get(path)).bind();
        } else {
            try{
                FXMLLoader loader = fxmlLoaderFactory.getLoader(getClass().getResource(path));
                Pane childPane = loader.load();

                clearScene(parentPane);

                parentPane.getChildren().add(childPane);
                addController(path, childPane, loader.getController());
                setPaneResizable(parentPane, childPane);
            } catch (Exception ex) {
                LoggerFactory.getLogger(getClass()).error("Can't load " + path + "." + ex);
            }
        }
    }

    private void setPaneResizable(Pane rootPane, Pane childPane) {
        childPane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        rootPane.heightProperty().addListener((arg0, arg1, arg2) -> childPane.setPrefHeight(arg2.doubleValue()));
        rootPane.widthProperty().addListener((arg0, arg1, arg2) -> childPane.setPrefWidth(arg2.doubleValue()));
    }

    public Stage getCurrentPopStage() {
        return currentPopStage;
    }

    public void setCurrentPopStage(Stage currentPopStage) {
        this.currentPopStage = currentPopStage;
    }
}
