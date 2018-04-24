package org.apache.cayenne.modeler.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import org.apache.cayenne.CayenneRuntimeException;

import java.io.IOException;

import static org.apache.cayenne.modeler.BQApplication.getInjector;

public class MainController implements Unbindable{

    public ScreenController screenController;

    @FXML
    public Pane rootPane;

    @FXML
    public Pane toolBarPane;

    @FXML
    public Pane statusBarPane;

    @FXML
    public Pane treeViewPane;

    @FXML
    public Pane projectViewPane;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        initToolBar();
        initStatusBar();
        initTreeView();
        initLastView();
        screenController = getInjector().getInstance(ScreenController.class);
    }

    public void setPaneResizable(Pane rootPane, Pane childPane) {
        rootPane.heightProperty().addListener((arg0, arg1, arg2) -> childPane.setPrefHeight(arg2.doubleValue()));
        rootPane.widthProperty().addListener((arg0, arg1, arg2) -> childPane.setPrefWidth(arg2.doubleValue()));

        childPane.setPrefSize(rootPane.getPrefWidth(), rootPane.getPrefHeight());
    }

    public void initToolBar() {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../ToolBar.fxml"));
        Pane childPane = loader.load();

        toolBarPane.getChildren().clear();
        toolBarPane.getChildren().add(childPane);

        setPaneResizable(toolBarPane, childPane);
        } catch (IOException e) {
            throw new CayenneRuntimeException("Can't load tool bar." , e);
        }
    }

    public void initStatusBar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../StatusBar.fxml"));
            Pane childPane = loader.load();

            statusBarPane.getChildren().clear();
            statusBarPane.getChildren().add(childPane);

            setPaneResizable(statusBarPane, childPane);
        } catch (IOException e) {
            throw new CayenneRuntimeException("Can't load status bar." , e);
        }
    }

    public void initTreeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../TreeView.fxml"));
            Pane childPane = loader.load();

            treeViewPane.getChildren().clear();
            treeViewPane.getChildren().add(childPane);

            setPaneResizable(treeViewPane, childPane);
        } catch (IOException e) {
            throw new CayenneRuntimeException("Can't load tree view." , e);
        }
    }

    public void initLastView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../DomainView.fxml"));
            Pane childPane = loader.load();

            projectViewPane.getChildren().clear();
            projectViewPane.getChildren().add(childPane);

            setPaneResizable(projectViewPane, childPane);
        } catch (IOException e) {
            throw new CayenneRuntimeException("Can't load status bar." , e);
        }
    }

    @Override
    public void unbind() {

    }
}
