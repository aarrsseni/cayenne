package org.apache.cayenne.modeler.controller.dbEntity;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.action.CreateAttributeAction;
import org.apache.cayenne.modeler.action.CreateObjEntityAction;
import org.apache.cayenne.modeler.action.CreateRelationshipAction;
import org.apache.cayenne.modeler.controller.Unbindable;
import org.apache.cayenne.modeler.controllers.DbEntityFieldsController;
import org.apache.cayenne.modeler.jFx.component.factory.TableFactory;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

public class DbEntityController implements Unbindable{

    private DbEntity dbEntity;

    @FXML
    public Pane dbEntityRoot;

    @FXML
    public VBox propertyBox;

    @FXML
    public ScrollPane dbEntityScrollPane;

    @FXML
    public TableView<Observer> tableView;

    @FXML
    public TableView<Observer> relView;

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
    private TableFactory tableFactory;

    @Inject
    public ProjectController projectController;

    @Inject
    private CreateAttributeAction createAttributeAction;

    @Inject
    private CreateObjEntityAction createObjEntityAction;

    @Inject
    private DbEntityFieldsController dbEntityFieldsController;

    @Inject
    private CreateRelationshipAction createRelationshipAction;

    @Inject
    private DbRelationshipsController dbRelationshipsController;

    @Inject
    private DbAttributeController dbAttributeController;

    private static final String PK_DEFAULT_GENERATOR = "Cayenne-Generated (Default)";
    private static final String PK_DB_GENERATOR = "Database-Generated";
    private static final String PK_CUSTOM_SEQUENCE_GENERATOR = "Custom Sequence";

    public DbEntityController(){
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        pkGenerator.setItems(FXCollections.observableArrayList(PK_DEFAULT_GENERATOR, PK_DB_GENERATOR, PK_CUSTOM_SEQUENCE_GENERATOR));
        pkGenerator.getSelectionModel().selectFirst();

        initListeners();
        makeResizable();

        dbAttributeController.init(tableView);
        dbRelationshipsController.init(relView);
    }

    @Override
    public void bind() {
        dbEntity = projectController.getCurrentState().getDbEntity();

        ObserverDictionary.getObserver(dbEntity)
                .bind("name", dbEntityName.textProperty())
                .bind("catalog", dbEntityCatalog.textProperty())
                .bind("schema", dbEntitySchema.textProperty())
                .bind("qualifier", dbEntityQualifier.textProperty());

        dbAttributeController.bindTable(dbEntity);
        dbRelationshipsController.bindTable(dbEntity);
    }

    @Override
    public void unbind() {
        dbAttributeController.unbindTable();

        dbRelationshipsController.unbindTable();
        ObserverDictionary.getObserver(dbEntity).unbindAll();
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

    private void initListeners(){
        dbEntityName.textProperty().addListener((observable, oldName, newName) -> dbEntityFieldsController.dbEntityNameChanged(newName));
        dbEntityCatalog.textProperty().addListener(((observable, oldValue, newValue) -> dbEntityFieldsController.dbEntityCatalogChanged(newValue)));
        dbEntitySchema.textProperty().addListener(((observable, oldValue, newValue) -> dbEntityFieldsController.dbEntitySchemaChanged(newValue)));
        dbEntityQualifier.textProperty().addListener(((observable, oldValue, newValue) -> dbEntityFieldsController.dbEntityQualifierChenged(newValue)));
    }

    @FXML
    public void createAttribute(ActionEvent e) throws Exception {
        createAttributeAction.handle(e);
    }

    @FXML
    public void createObjEntityAction(ActionEvent e) {
        createObjEntityAction.handle(e);
    }

    @FXML
    public void createRelationships(ActionEvent e) {
        createRelationshipAction.handle(e);
    }
}
