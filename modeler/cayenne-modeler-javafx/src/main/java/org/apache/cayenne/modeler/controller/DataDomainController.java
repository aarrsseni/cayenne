package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
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

    @Inject
    public ProjectController projectController;

    private DataChannelDescriptor dataDomain;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        dataDomain = projectController.getCurrentState().getDomain();

        initListeners();
        makeResizable();
    }

    public void initListeners() {
        projectController.getEventController().addListener(DomainDisplayListener.class, this);
    }

    @Override
    public void bind() {
        ObserverDictionary.getObserver(dataDomain)
                .bind("name", name.textProperty());
        System.out.println("Bind dataDomainController");
    }

    @Override
    public void unbind() {
        ObserverDictionary.getObserver(dataDomain).unbindAll();
        System.out.println("Unbind dataDomain.");
    }

    @Override
    public void currentDomainChanged(DomainDisplayEvent e) {
//        name.setText(e.getDomain().getName());
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
    }
}
