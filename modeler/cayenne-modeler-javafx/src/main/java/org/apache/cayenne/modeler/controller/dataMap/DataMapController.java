package org.apache.cayenne.modeler.controller.dataMap;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.Unbindable;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

public class DataMapController implements Unbindable {

    @Inject
    public ProjectController projectController;

    @FXML
    public Pane dataMapRoot;

    @FXML
    public ScrollPane dataMapScrollPane;

    @FXML
    public TabPane dataMapTabPane;

    @FXML
    private TextField datamapName;

    @FXML
    private TextField dbCatalog;

    @FXML
    private TextField dbSchema;

    @FXML
    private TextField javaPackage;

    @FXML
    private TextField customSuperClass;

    @FXML
    private CheckBox clientSupported;

    @FXML
    private TextField clientPackage;

    @FXML
    private TextField clientCustomSuperclass;

    protected DataMap selectedDataMap;

    public DataMapController(){
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        makeResizable();
        initListeners();

        clientSupported.selectedProperty().addListener((observable, oldValue, newValue) -> {
            clientPackage.setDisable(!newValue);
            clientCustomSuperclass.setDisable(!newValue);
        });
    }

    @Override
    public void bind() {
        selectedDataMap = projectController.getCurrentState().getDataMap();
        ObserverDictionary.getObserver(selectedDataMap)
                .bind("name", datamapName.textProperty())
                .bind("defaultCatalog", dbCatalog.textProperty())
                .bind("defaultSchema", dbSchema.textProperty())
                .bind("defaultPackage", javaPackage.textProperty())
                .bind("defaultSuperclass", customSuperClass.textProperty())
                .bind("clientSupported", clientSupported.selectedProperty())
                .bind("defaultClientPackage", clientPackage.textProperty())
                .bind("defaultClientSuperclass", clientCustomSuperclass.textProperty());
    }

    @Override
    public void unbind() {
        ObserverDictionary.getObserver(selectedDataMap).unbindAll();
    }

    public void initListeners() {
    }

    public void makeResizable() {
        dataMapRoot.heightProperty().addListener((arg0, arg1, arg2) -> {
            dataMapScrollPane.setPrefHeight(arg2.doubleValue());
        });
        dataMapRoot.widthProperty().addListener((arg0, arg1, arg2) -> {
            dataMapScrollPane.setPrefWidth(arg2.doubleValue());
        });

        dataMapScrollPane.setFitToWidth(true);
        dataMapScrollPane.setFitToHeight(true);
    }
}
