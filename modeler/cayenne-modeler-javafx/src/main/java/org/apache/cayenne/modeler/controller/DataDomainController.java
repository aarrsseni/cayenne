package org.apache.cayenne.modeler.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.modeler.BQApplication;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DomainDisplayListener;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

public class DataDomainController implements Unbindable, DomainDisplayListener{

    @FXML
    private TextField name;

    @FXML
    private CheckBox useSharedCache;

    @FXML
    private CheckBox objectValidation;

    @FXML
    private Pane root;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TabPane tabPane;

    private DataChannelDescriptor dataDomain;

    public ProjectController projectController;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        projectController = BQApplication.getInjector().getInstance(ProjectController.class);
        dataDomain = projectController.getCurrentState().getDomain();

        bindDataDomain();
        initListeners();
        makeResizable();
    }

    private void bindDataDomain() {
        ObserverDictionary.getObserver(dataDomain)
                .bind("name", name.textProperty());
    }

    public void initListeners() {
        projectController.getEventController().addListener(DomainDisplayListener.class, this);
    }

    @Override
    public void unbind() {
        ObserverDictionary.getObserver(dataDomain).unbind("name", name.textProperty());
    }

    @Override
    public void currentDomainChanged(DomainDisplayEvent e) {
        name.setText(e.getDomain().getName());
    }

    public void makeResizable() {
        root.heightProperty().addListener((arg0, arg1, arg2) -> {
            scrollPane.setPrefHeight(arg2.doubleValue());
        });
        root.widthProperty().addListener((arg0, arg1, arg2) -> {
            scrollPane.setPrefWidth(arg2.doubleValue());
        });

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.heightProperty().addListener((arg0, arg1, arg2) -> {
            tabPane.setPrefHeight(arg2.doubleValue());
        });
        scrollPane.widthProperty().addListener((arg0, arg1, arg2) -> {
            tabPane.setPrefWidth(arg2.doubleValue());
        });
    }
}
