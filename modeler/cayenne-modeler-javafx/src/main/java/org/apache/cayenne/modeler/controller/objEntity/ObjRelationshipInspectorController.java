package org.apache.cayenne.modeler.controller.objEntity;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.modeler.controller.ScreenController;
import org.apache.cayenne.modeler.controller.Unbindable;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

public class ObjRelationshipInspectorController implements Unbindable {

    static final String COLLECTION_TYPE_MAP = "java.util.Map";
    static final String COLLECTION_TYPE_SET = "java.util.Set";
    static final String COLLECTION_TYPE_COLLECTION = "java.util.Collection";
    static final String DEFAULT_MAP_KEY = "ID (default)";

    @Inject
    public ScreenController screenController;

    @Inject
    public ObjRelationshipsController objRelationshipsController;

    @FXML
    public Button cancelButton;

    @FXML
    public Button doneButton;

    @FXML
    public TextField relationshipField;

    @FXML
    public TextField source;

    @FXML
    public ComboBox<String> targetDbPath;

    @FXML
    public ComboBox<String> coillectionType;

    @FXML
    public ComboBox<String> mapKey;

    private ObjRelationship objRelationship;

    private ObservableList<String> targetCollectionsList;

    @Inject
    private ObjEntityController objEntityController;

    public ObjRelationshipInspectorController(){
        targetCollectionsList = FXCollections.observableArrayList();
        targetCollectionsList.add(COLLECTION_TYPE_COLLECTION);
        targetCollectionsList.add(ObjRelationship.DEFAULT_COLLECTION_TYPE);
        targetCollectionsList.add(COLLECTION_TYPE_MAP);
        targetCollectionsList.add(COLLECTION_TYPE_SET);
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {

        coillectionType.getItems().addAll(targetCollectionsList);
        coillectionType.setDisable(true);
        mapKey.setDisable(true);
        initActions();
    }

    private void initActions(){
        cancelButton.setOnAction(val -> {
            if(screenController.getCurrentPopStage() != null){
                screenController.getCurrentPopStage().close();
                unbind();
            }
        });

        doneButton.setOnAction(val -> {
            cancel();
        });
    }

    private void cancel(){
        if(screenController.getCurrentPopStage() != null){
            screenController.getCurrentPopStage().close();
            unbind();
        }
    }

    public void setObjRelationship(ObjRelationship objRelationship){
        this.objRelationship = objRelationship;
    }

    @Override
    public void bind() {
        targetDbPath.getItems().add(objRelationship.getTargetEntityName());
        targetDbPath.getItems().add(objRelationship.getSourceEntity().getName());

        mapKey.getItems().add(DEFAULT_MAP_KEY);

        for(ObjAttribute objAttribute : objRelationship.getSourceEntity().getAttributes()){
            mapKey.getItems().add(objAttribute.getName());
        }

        ObserverDictionary.getObserver(objRelationship)
                .bind("name", relationshipField.textProperty())
                .bind("targetEntityName", targetDbPath.valueProperty())
                .bind("collectionType", coillectionType.valueProperty())
                .bind("mapKey", mapKey.valueProperty());

        source.setText(objRelationship.getSourceEntity().getName());

        if(objRelationship.isToMany()){
            coillectionType.setDisable(false);
        }

        addListenersToAttr();
    }

    private void addListenersToAttr(){
        coillectionType.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue.equals(COLLECTION_TYPE_MAP)){
                mapKey.setDisable(false);
                objRelationship.setMapKey(DEFAULT_MAP_KEY);
                mapKey.getSelectionModel().select(DEFAULT_MAP_KEY);
            } else {
                mapKey.setDisable(true);
            }
        }));
    }

    @Override
    public void unbind() {
        ObserverDictionary.getObserver(objRelationship)
                .unbindAll();

        targetDbPath.getItems().clear();

        mapKey.getItems().clear();
    }
}
