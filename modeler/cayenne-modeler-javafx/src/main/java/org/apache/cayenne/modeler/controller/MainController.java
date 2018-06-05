package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.components.MenuBarFactory;
import org.apache.cayenne.modeler.event.*;
import org.apache.cayenne.modeler.event.listener.*;

public class MainController implements Unbindable, DataMapDisplayListener, DbEntityDisplayListener, ObjEntityDisplayListener{

    @Inject
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
    public MenuBar menuBar;

    @Inject
    public ProjectController projectController;

    @Inject
    public MenuBarFactory menuBarFactory;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        projectController.getCurrentState().initControllerStateListeners();

        initToolBar();
        initStatusBar();
        initTreeView();
        initDomainView();
        initMenuBar();
        initListeners();
    }

    private void initToolBar() {
        screenController.loadAndUpdatePane(toolBarPane, "../ToolBar.fxml");
    }

    private void initStatusBar() {
        screenController.loadAndUpdatePane(statusBarPane, "../StatusBar.fxml");
    }

    private void initTreeView() {
        screenController.loadAndUpdatePane(treeViewPane, "../TreeView.fxml");
    }

    private void initDomainView() {
        screenController.loadAndUpdatePane(projectViewPane, "../DomainView.fxml");
    }

    private void initMenuBar() {
        menuBarFactory.setMenuBar(menuBar);
        menuBarFactory.createMenuBar();
    }

    private void initListeners() {
        projectController.getEventController().addListener(DataMapDisplayListener.class, this);
        projectController.getEventController().addListener(DbEntityDisplayListener.class, this);
        projectController.getEventController().addListener(ObjEntityDisplayListener.class, this);
    }

    @Override
    public void bind() {
    }

    @Override
    public void unbind() {
    }

    @Override
    public void currentDataMapChanged(DataMapDisplayEvent e) {
        screenController.loadAndUpdatePane(projectViewPane, "../DataMapView.fxml");
    }

    @Override
    public void currentDbEntityChanged(DbEntityDisplayEvent e) {
        screenController.loadAndUpdatePane(projectViewPane, "../DbEntityView.fxml");
    }

    @Override
    public void currentObjEntityChanged(ObjEntityDisplayEvent e) {
        screenController.loadAndUpdatePane(projectViewPane, "../ObjEntityView.fxml");
    }
}
