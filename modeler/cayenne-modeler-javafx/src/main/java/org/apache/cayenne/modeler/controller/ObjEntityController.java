package org.apache.cayenne.modeler.controller;

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
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.map.*;
import org.apache.cayenne.map.event.EntityEvent;
import org.apache.cayenne.map.event.ObjEntityListener;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.components.TableFactory;
import org.apache.cayenne.modeler.controllers.ObjEntityFieldsController;
import org.apache.cayenne.modeler.event.DbAttributeDisplayEvent;
import org.apache.cayenne.modeler.event.DbEntityDisplayEvent;
import org.apache.cayenne.modeler.event.ObjAttributeDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DbAttributeDisplayListener;
import org.apache.cayenne.modeler.event.listener.DbEntityDisplayListener;
import org.apache.cayenne.modeler.event.listener.ObjAttributeDisplayListener;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.modeler.services.AttributeService;
import org.apache.cayenne.modeler.services.RelationshipService;

import java.util.HashMap;
import java.util.Map;

public class ObjEntityController implements Unbindable, ObjAttributeDisplayListener, DbEntityDisplayListener, DbAttributeDisplayListener, ObjEntityListener {

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

    private Property optimisticLockingProperty;

    private Property<String> dbEntityProperty;

    private Map<ObjEntity, ObservableList<Observer>> objAttrsMap;

    private ObservableList<String> dbEntityList;

    private ObservableList<String> dbAttrObservableList;

    @Inject
    public ProjectController projectController;

    @Inject
    private ObjEntityFieldsController objEntityFieldsController;

    @Inject
    private TableFactory tableFactory;

    @Inject
    private AttributeService createAttributeService;

    @Inject
    private ObjRelationshipsController objRelationshipsController;

    @Inject
    public RelationshipService relationshipService;

    public ObjEntityController(){
        this.objAttrsMap = new HashMap<>();
        this.dbAttrObservableList = FXCollections.observableArrayList();
        this.dbEntityList = FXCollections.observableArrayList();
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        optimisticLockingProperty = new SimpleStringProperty();
        dbEntityProperty = new SimpleStringProperty();

        initListeners();
        makeResizable();
        prepareTables();

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

       fillTable();

       objRelationshipsController.bindTable(objEntity);
       objRelationshipsController.setObjEntity(objEntity);

       System.out.println("Bind " + getClass());
    }

    @Override
    public void unbind() {
        ObserverDictionary.getObserver(objEntity)
                .unbindAll();

        objRelationshipsController.unbindTable();

        tableView.setItems(null);

        dbEntityList.clear();
        tablesComboBox.setItems(dbEntityList);

        System.out.println("Unbind " + getClass());
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
        projectController.getEventController().addListener(ObjAttributeDisplayListener.class, this);
        projectController.getEventController().addListener(DbEntityDisplayListener.class, this);
        projectController.getEventController().addListener(DbAttributeDisplayListener.class, this);
        projectController.getEventController().addListener(ObjEntityListener.class, this);

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

    private void checkObjAttrs(ObjEntity objEntity, Observer dbAttributeRow){
        if(objAttrsMap.containsKey(objEntity)) {
            objAttrsMap.get(objEntity).add(dbAttributeRow);
        } else {
            ObservableList<Observer> observableList = FXCollections.observableArrayList();
            observableList.add(dbAttributeRow);
            objAttrsMap.put(objEntity, observableList);
        }
    }

    private void prepareTables(){
        tableView.getColumns().addAll(tableFactory.createObjTable());

        // single cell selection mode
        tableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    private void fillTable(){
        for(DbEntity dbEntity : projectController.getCurrentState().getDataMap().getDbEntities()){
            dbEntityList.addAll(dbEntity.getName());
        }
        tablesComboBox.setItems(dbEntityList);

        resetDbAttrPathComboBox();
        addAttrs();
    }

    private void addAttrs(){
        ObservableList<Observer> observableList = FXCollections.observableArrayList();
        for(ObjAttribute objAttribute : objEntity.getAttributes()) {
            observableList.add(ObserverDictionary.getObserver(objAttribute));
            setDbType(objAttribute);
            addListenersToAttr(objAttribute);
        }
        objAttrsMap.put(objEntity, observableList);

        tableView.setItems(objAttrsMap.get(objEntity));
    }

    private void resetDbAttrPathComboBox(){
        dbAttrObservableList.clear();
        if(objEntity.getDbEntity() != null) {
            for (DbAttribute dbAttribute : objEntity.getDbEntity().getAttributes()) {
                dbAttrObservableList.add(dbAttribute.getName());
            }
        }
    }

    public ObservableList<String> getDbAttrObservableList(){
        return dbAttrObservableList;
    }

    private void addListenersToAttr(Attribute attribute) {
        ObserverDictionary.getObserver(attribute).getPropertyWithoutBinding("dbAttributePath").addListener(((observable, oldValue, newValue) -> {
            if(newValue != null){
                if(newValue.equals("")){
                    ObserverDictionary.getObserver(attribute).getCustomPropertyWithoutBinding("dbType", String.class).setValue(null);
                } else {
                    setDbType((ObjAttribute) attribute);
                }
            }
        }));
    }

    private void setDbType(ObjAttribute objAttribute){
        if(objAttribute.getDbAttribute() != null) {
            ObserverDictionary.getObserver(objAttribute).getCustomPropertyWithoutBinding("dbType", String.class).setValue(
                    TypesMapping.getSqlNameByType(objAttribute.getDbAttribute().getType()));
        }
    }

    private void resetDbType(){
        for(ObjAttribute objAttribute : objEntity.getAttributes()) {
            ObserverDictionary.getObserver(objAttribute).getCustomPropertyWithoutBinding("dbType", String.class).setValue(
                    null);
        }
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

    @FXML
    public void createObjRelationships(ActionEvent e) throws Exception {
        relationshipService.createRelationship();
    }

    @Override
    public void currentObjAttributeChanged(ObjAttributeDisplayEvent e) {
        checkObjAttrs(objEntity, ObserverDictionary.getObserver(e.getAttributes()[0]));

        tableView.setItems(objAttrsMap.get(objEntity));

        setDbType((ObjAttribute) e.getAttributes()[0]);
        addListenersToAttr(e.getAttributes()[0]);
    }

    @Override
    public void currentDbAttributeChanged(DbAttributeDisplayEvent e) {
        resetDbAttrPathComboBox();
    }

    @Override
    public void currentDbEntityChanged(DbEntityDisplayEvent e) {

    }

    @Override
    public void objEntityChanged(EntityEvent e) {
        if (e.getSource() == this) {
            return;
        }
        resetDbAttrPathComboBox();
        resetDbType();
    }

    @Override
    public void objEntityAdded(EntityEvent e) {

    }

    @Override
    public void objEntityRemoved(EntityEvent e) {

    }

    public Map<ObjEntity, ObservableList<Observer>> getObjAttrsMap(){
        return objAttrsMap;
    }
}
