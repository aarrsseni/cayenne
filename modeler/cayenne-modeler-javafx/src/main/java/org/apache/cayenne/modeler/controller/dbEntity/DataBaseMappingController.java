package org.apache.cayenne.modeler.controller.dbEntity;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.ScreenController;
import org.apache.cayenne.modeler.controller.Unbindable;
import org.apache.cayenne.modeler.jFx.component.factory.TableFactory;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.modeler.services.DataBaseMappingService;

import java.util.HashMap;
import java.util.Map;

public class DataBaseMappingController implements Unbindable {

    @Inject
    ScreenController screenController;

    @Inject
    ProjectController projectController;

    @Inject
    TableFactory attributeTable;

    @Inject
    DbRelationshipsController dbRelationshipsController;

    @Inject
    private DataBaseMappingService dataBaseMappingService;

    @FXML
    private TextField relationshipField;

    @FXML
    private TextField reverseRelationshipField;

    @FXML
    private TableView joinsTable;

    @FXML
    private Button cancelButton;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button doneButton;

    private DbRelationship dbRelationship;

    private DbRelationship reverseRelationship;

    private Map<DbRelationship, ObservableList<Observer>> dbJoinMap;

    private ObservableList<String> dbSourceAttrs;

    private ObservableList<String> dbTargetAttrs;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        this.dbJoinMap = new HashMap<>();
        this.dbSourceAttrs = FXCollections.observableArrayList();
        this.dbTargetAttrs = FXCollections.observableArrayList();

        initActions();
        prepareTable();
    }

    private void initActions(){
        cancelButton.setOnAction(val -> {
            if(screenController.getCurrentPopStage() != null){
                screenController.getCurrentPopStage().close();
                unbind();
            }
        });

        addButton.setOnAction(val -> {
            DbJoin dbJoin = new DbJoin(dbRelationship);
            checkDbJoins(dbRelationship, ObserverDictionary.getObserver(dbJoin));

            joinsTable.getItems().add(ObserverDictionary.getObserver(dbJoin));
            joinsTable.getSelectionModel().select(ObserverDictionary.getObserver(dbJoin));
        });

        doneButton.setOnAction(val -> {
            save();

            cancel();
        });
    }

    private void cancel(){
        if(screenController.getCurrentPopStage() != null){
            screenController.getCurrentPopStage().close();
            unbind();
            dbRelationshipsController.unbindTable();
            dbRelationshipsController.bindTable(dbRelationshipsController.getDbEntity());
        }
    }

    private void prepareTable(){
        joinsTable.getColumns().addAll(attributeTable.createDbJoinTable());
    }

    public void setDbRelationship(DbRelationship dbRelationship) {
        this.dbRelationship = dbRelationship;
    }

    public DbRelationship getDbRelationship() {
        return dbRelationship;
    }

    private void checkDbJoins(DbRelationship dbRelationship, Observer dbJoin){
        if(dbJoinMap.containsKey(dbRelationship)) {
            dbJoinMap.get(dbRelationship).add(dbJoin);
        } else {
            ObservableList<Observer> observableList = FXCollections.observableArrayList();
            observableList.add(dbJoin);
            dbJoinMap.put(dbRelationship, observableList);
        }
    }

    @Override
    public void bind() {
        ObserverDictionary.getObserver(dbRelationship)
                .bind("name", relationshipField.textProperty());

        reverseRelationship = dbRelationship.getReverseRelationship();

        if(dbRelationship.getReverseRelationship() != null) {
            ObserverDictionary.getObserver(dbRelationship.getReverseRelationship())
                    .bind("name", reverseRelationshipField.textProperty());
        }

        for(DbJoin dbJoin : dbRelationship.getJoins()) {
            checkDbJoins(dbRelationship, ObserverDictionary.getObserver(dbJoin));
        }

        for(DbJoin dbJoin : dbRelationship.getJoins()) {
            joinsTable.getItems().add(ObserverDictionary.getObserver(dbJoin));
        }

        for(DbAttribute dbAttribute : dbRelationship.getSourceEntity().getAttributes()){
            dbSourceAttrs.add(dbAttribute.getName());
        }
        for(DbAttribute dbAttribute : dbRelationship.getTargetEntity().getAttributes()){
            dbTargetAttrs.add(dbAttribute.getName());
        }
    }

    @Override
    public void unbind() {
        ObserverDictionary.getObserver(dbRelationship)
                .unbindAll();

        if(dbRelationship.getReverseRelationship() != null) {
            ObserverDictionary.getObserver(dbRelationship.getReverseRelationship())
                    .unbindAll();
        }

        setDbRelationship(null);
        reverseRelationship = null;

        dbSourceAttrs.clear();
        dbTargetAttrs.clear();
        joinsTable.getItems().clear();
    }

    public ObservableList<String> getDbSourceAttrs() {
        return dbSourceAttrs;
    }

    public ObservableList<String> getDbTargetAttrs() {
        return dbTargetAttrs;
    }

    private void save() {
        dataBaseMappingService.handleNameUpdate(dbRelationship, relationshipField.getText().trim());

        for(Observer dbJoin : dbJoinMap.get(dbRelationship)){
            if(!dbRelationship.getJoins().contains((DbJoin) dbJoin.getBean())) {
                dbRelationship.addJoin((DbJoin) dbJoin.getBean());
            }
        }

        dataBaseMappingService.save(dbRelationship, reverseRelationship, reverseRelationshipField.getText().trim());
    }
}
