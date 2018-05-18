package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DataMapDisplayEvent;
import org.apache.cayenne.modeler.event.DbEntityDisplayEvent;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DataMapDisplayListener;
import org.apache.cayenne.modeler.event.listener.DbEntityDisplayListener;
import org.apache.cayenne.modeler.event.listener.DomainDisplayListener;

public class MainController implements Unbindable, DataMapDisplayListener, DomainDisplayListener, DbEntityDisplayListener{

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

    @Inject
    public ProjectController projectController;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        projectController.getCurrentState().initControllerStateListeners();

        initToolBar();
        initStatusBar();
        initTreeView();
        initLastView();
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

    private void initLastView() {
        screenController.loadAndUpdatePane(projectViewPane, "../DomainView.fxml");
    }

    private void initListeners() {
        projectController.getEventController().addListener(DataMapDisplayListener.class, this);
        projectController.getEventController().addListener(DomainDisplayListener.class, this);
        projectController.getEventController().addListener(DbEntityDisplayListener.class, this);
    }

    @Override
    public void bind() {
        System.out.println("Bind mainController");
    }

    @Override
    public void unbind() {
        System.out.println("Unbind mainController");
    }

    @Override
    public void currentDataMapChanged(DataMapDisplayEvent e) {
        screenController.loadAndUpdatePane(projectViewPane, "../DataMapView.fxml");
    }

    @Override
    public void currentDomainChanged(DomainDisplayEvent e) {
        screenController.loadAndUpdatePane(projectViewPane, "../DomainView.fxml");
    }

    @Override
    public void currentDbEntityChanged(DbEntityDisplayEvent e) {
        screenController.loadAndUpdatePane(projectViewPane, "../DbEntityView.fxml");
    }
}
