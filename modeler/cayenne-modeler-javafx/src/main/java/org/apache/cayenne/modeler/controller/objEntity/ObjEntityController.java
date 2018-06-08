package org.apache.cayenne.modeler.controller.objEntity;

import com.google.inject.Inject;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.action.CreateAttributeAction;
import org.apache.cayenne.modeler.action.CreateRelationshipAction;
import org.apache.cayenne.modeler.controller.Unbindable;
import org.apache.cayenne.modeler.controllers.ObjEntityFieldsController;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

public class ObjEntityController implements Unbindable{

    @FXML
    private TextField objEntityName;

    @FXML
    private ComboBox<String> inheritance;

    @FXML
    private ComboBox<String> tablesComboBox;

    @FXML
    private CheckBox isAbstractClass;

    @FXML
    private TextField javaClass;

    @FXML
    private TextField javaSuperclass;

    @FXML
    private TextField qualifier;

    @FXML
    private CheckBox isReadOnly;

    @FXML
    private CheckBox optimisticLocking;

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public AnchorPane relAnchorPane;

    @FXML
    private TableView<Observer> tableView;

    @FXML
    private TableView relationshipView;

    @FXML
    public Pane objEntityRoot;

    @FXML
    public ScrollPane objEntityScrollPane;

    @FXML
    public TabPane tabPane;

    private ObjEntity objEntity;

    private Property<String> optimisticLockingProperty;

    private ObservableList<String> dbEntityList;

    @Inject
    public ProjectController projectController;

    @Inject
    private ObjEntityFieldsController objEntityFieldsController;

    @Inject
    private CreateAttributeAction createAttributeAction;

    @Inject
    private ObjRelationshipsController objRelationshipsController;

    @Inject
    private ObjAttributesController objAttributesController;

    @Inject
    private CreateRelationshipAction createRelationshipAction;

    public ObjEntityController(){
        this.dbEntityList = FXCollections.observableArrayList();
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        optimisticLockingProperty = new SimpleStringProperty();

        initListeners();
        makeResizable();

        objAttributesController.init(tableView);
        objRelationshipsController.init(relationshipView);
    }

    @Override
    public void bind() {
        objEntity = projectController.getCurrentState().getObjEntity();

        ObserverDictionary.getObserver(objEntity)
                .bind("name", objEntityName.textProperty())
                .bind("superClassName", javaSuperclass.textProperty())
                .bind("className", javaClass.textProperty())
                .bind("readOnly", isReadOnly.selectedProperty())
                .bind("_abstract", isAbstractClass.selectedProperty())
                .bind("declaredLockType", optimisticLockingProperty)
                .bind("dbEntityName", tablesComboBox.valueProperty());

        optimisticLocking.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            optimisticLockingProperty.setValue(String.valueOf(newValue
                    ? ObjEntity.LOCK_TYPE_OPTIMISTIC
                    : ObjEntity.LOCK_TYPE_NONE));
        }));

        for(DbEntity dbEntity : projectController.getCurrentState().getDataMap().getDbEntities()){
            dbEntityList.addAll(dbEntity.getName());
        }
        tablesComboBox.setItems(dbEntityList);

       objAttributesController.bindTable(objEntity);
       objRelationshipsController.bindTable(objEntity);
    }

    @Override
    public void unbind() {
        ObserverDictionary.getObserver(objEntity)
                .unbindAll();

        objAttributesController.unbindTable();
        objRelationshipsController.unbindTable();

        dbEntityList.clear();
        tablesComboBox.setItems(dbEntityList);
    }

    private void makeResizable() {
        objEntityRoot.heightProperty().addListener((arg0, arg1, arg2) -> {
            objEntityScrollPane.setPrefHeight(arg2.doubleValue());
        });
        objEntityRoot.widthProperty().addListener((arg0, arg1, arg2) -> {
            objEntityScrollPane.setPrefWidth(arg2.doubleValue());
        });

        tabPane.widthProperty().addListener((arg0, arg1, arg2) -> {
            tableView.setPrefWidth(arg2.doubleValue());
        });

        tabPane.widthProperty().addListener((arg0, arg1, arg2) -> {
            relationshipView.setPrefWidth(arg2.doubleValue());
        });

        anchorPane.heightProperty().addListener((arg0, arg1, arg2) -> {
            tableView.setPrefHeight(arg2.doubleValue());
        });

        relAnchorPane.heightProperty().addListener((arg0, arg1, arg2) -> {
            relationshipView.setPrefHeight(arg2.doubleValue());
        });

        objEntityScrollPane.setFitToWidth(true);
        objEntityScrollPane.setFitToHeight(true);
    }

    private void initListeners(){
        objEntityName.textProperty().addListener(((observable, oldValue, newValue) -> objEntityFieldsController.objEntityNameChanged(newValue)));
        javaSuperclass.textProperty().addListener(((observable, oldValue, newValue) -> objEntityFieldsController.objEntitySuperclassChanged(newValue)));
        javaClass.textProperty().addListener(((observable, oldValue, newValue) -> objEntityFieldsController.objEntityClassNameChanged(newValue)));
        isAbstractClass.selectedProperty().addListener(((observable, oldValue, newValue) -> objEntityFieldsController.isAbstractChanged(newValue)));
        isReadOnly.selectedProperty().addListener(((observable, oldValue, newValue) -> objEntityFieldsController.isReadOnlyChanged(newValue)));
        tablesComboBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
                if(newValue != null){
                objEntityFieldsController.dbEntityChanged(projectController.getCurrentState().getDataMap().getDbEntity(newValue));
            }
        }));
    }

    @FXML
    public void createAttribute(ActionEvent e) {
        createAttributeAction.handle(e);
    }

    @FXML
    public void createObjRelationships(ActionEvent e) {
        createRelationshipAction.handle(e);
    }
}
