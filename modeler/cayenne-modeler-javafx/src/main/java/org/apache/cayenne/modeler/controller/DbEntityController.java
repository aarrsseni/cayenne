package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DbAttributeDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DbAttributeDisplayListener;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.modeler.services.AttributeService;
import org.apache.cayenne.modeler.components.AttributeTable;

import java.util.HashMap;
import java.util.Map;

public class DbEntityController implements Unbindable, DbAttributeDisplayListener{

    private DbEntity dbEntity;

    @FXML
    public Pane dbEntityRoot;

    @FXML
    public ScrollPane dbEntityScrollPane;

    @FXML
    public TableView<Observer> tableView;

    @FXML
    public TableView relView;

    @FXML
    public SplitPane splitPane;

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public TabPane tabPane;

    @FXML
    public AnchorPane relAnchorPane;

    @FXML
    private TextField dbEntityName;

    @FXML
    private TextField dbEntityCatalog;

    @FXML
    private TextField dbEntitySchema;

    @FXML
    private TextField dbEntityQualifier;

    @FXML
    private TextField dbEntityComment;

    @FXML
    private ChoiceBox pkGenerator;

    @Inject
    private AttributeTable attributeTable;

    @Inject
    public ProjectController projectController;

    @Inject
    private AttributeService attributeService;

    @Inject
    private AttributeService createAttributeService;

    private Map<DbEntity, ObservableList<Observer>> dbAttrsMap;

    static final String PK_DEFAULT_GENERATOR = "Cayenne-Generated (Default)";
    static final String PK_DB_GENERATOR = "Database-Generated";
    static final String PK_CUSTOM_SEQUENCE_GENERATOR = "Custom Sequence";

    public DbEntityController(){
        this.dbAttrsMap = new HashMap<>();
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        pkGenerator.setItems(FXCollections.observableArrayList(PK_DEFAULT_GENERATOR, PK_DB_GENERATOR, PK_CUSTOM_SEQUENCE_GENERATOR));
        pkGenerator.getSelectionModel().selectFirst();

        tableView.setEditable(true);

        prepareTables();
        initListeners();
        makeResizable();
    }


    @Override
    public void bind() {
        dbEntity = projectController.getCurrentState().getDbEntity();

        ObserverDictionary.getObserver(dbEntity)
                .bind("name", dbEntityName.textProperty())
                .bind("catalog", dbEntityCatalog.textProperty())
                .bind("schema", dbEntitySchema.textProperty())
                .bind("qualifier", dbEntityQualifier.textProperty());

        tableView.setItems(dbAttrsMap.get(dbEntity));

        System.out.println("Bind " + getClass());
    }

    @Override
    public void unbind() {
        tableView.setItems(null);
        ObserverDictionary.getObserver(dbEntity).unbindAll();
        System.out.println("Unbind " + getClass());
    }

    private void makeResizable() {
        dbEntityRoot.heightProperty().addListener((arg0, arg1, arg2) -> {
            dbEntityScrollPane.setPrefHeight(arg2.doubleValue());
        });
        dbEntityRoot.widthProperty().addListener((arg0, arg1, arg2) -> {
            dbEntityScrollPane.setPrefWidth(arg2.doubleValue());
        });

        tabPane.widthProperty().addListener((arg0, arg1, arg2) -> {
            tableView.setPrefWidth(arg2.doubleValue());
        });

        tabPane.widthProperty().addListener((arg0, arg1, arg2) -> {
            relView.setPrefWidth(arg2.doubleValue());
        });

        anchorPane.heightProperty().addListener((arg0, arg1, arg2) -> {
            tableView.setPrefHeight(arg2.doubleValue());
        });

        relAnchorPane.heightProperty().addListener((arg0, arg1, arg2) -> {
            relView.setPrefHeight(arg2.doubleValue());
        });

        dbEntityScrollPane.setFitToWidth(true);
        dbEntityScrollPane.setFitToHeight(true);
    }

    @FXML
    public void createAttribute(ActionEvent e) throws Exception {
        if (projectController.getCurrentState().getEmbeddable() != null) {
            createAttributeService.createEmbAttribute();
        }

        if (projectController.getCurrentState().getObjEntity() != null) {
            createAttributeService.createObjAttribute();
        } else if (projectController.getCurrentState().getDbEntity() != null) {
            createAttributeService.createDbAttribute();
        }
    }

    @Override
    public void currentDbAttributeChanged(DbAttributeDisplayEvent e) {
        checkDbAttrs(dbEntity, ObserverDictionary.getObserver(e.getAttributes()[0]));

        tableView.setItems(dbAttrsMap.get(dbEntity));
    }

    private void initListeners(){
        projectController.getEventController().addListener(DbAttributeDisplayListener.class, this);
    }

    private void checkDbAttrs(DbEntity dbEntity, Observer dbAttributeRow){
        if(dbAttrsMap.containsKey(dbEntity)) {
            dbAttrsMap.get(dbEntity).add(dbAttributeRow);
        } else {
            ObservableList<Observer> observableList = FXCollections.observableArrayList();
            observableList.add(dbAttributeRow);
            dbAttrsMap.put(dbEntity, observableList);
        }
    }

    private void prepareTables(){
        tableView.getColumns().addAll(attributeTable.createDbTable());

        // single cell selection mode
        tableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    @FXML
    public void createObjEntityAction(ActionEvent e) {
        System.out.println("test");
    }
}
