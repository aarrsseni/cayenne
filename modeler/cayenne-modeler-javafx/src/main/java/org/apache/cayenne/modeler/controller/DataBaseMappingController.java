package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.cayenne.configuration.event.DbRelationshipEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.components.TableFactory;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.util.Util;

import java.util.*;

public class DataBaseMappingController implements Unbindable {

    @Inject
    ScreenController screenController;

    @Inject
    ProjectController projectController;

    @Inject
    TableFactory attributeTable;

    @Inject
    DbRelationshipsController dbRelationshipsController;

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
//        stopEditing();
//
//        DbJoinTableModel model = (DbJoinTableModel) table.getModel();
//        boolean updatingReverse = model.getObjectList().size() > 0;

        // handle name update
        handleNameUpdate(dbRelationship, relationshipField.getText().trim());

        for(Observer dbJoin : dbJoinMap.get(dbRelationship)){
            if(!dbRelationship.getJoins().contains((DbJoin) dbJoin.getBean())) {
                dbRelationship.addJoin((DbJoin) dbJoin.getBean());
            }
        }

//        model.commit();

        // check "to dep pk" setting,
        // maybe this is no longer valid
        if (dbRelationship.isToDependentPK() && !dbRelationship.isValidForDepPk()) {
            dbRelationship.setToDependentPK(false);
        }

        // If new reverse DbRelationship was created, add it to the target
        // Don't create reverse with no joins - makes no sense...
        if (true) {

            // If didn't find anything, create reverseDbRel
            if (reverseRelationship == null) {
                reverseRelationship = new DbRelationship();
                reverseRelationship.setName(NameBuilder
                        .builder(reverseRelationship, dbRelationship.getTargetEntity())
                        .baseName(reverseRelationshipField.getText().trim())
                        .name());

                reverseRelationship.setSourceEntity(dbRelationship.getTargetEntity());
                reverseRelationship.setTargetEntityName(dbRelationship.getSourceEntity());
                reverseRelationship.setToMany(!dbRelationship.isToMany());
                dbRelationship.getTargetEntity().addRelationship(reverseRelationship);

                // fire only if the relationship is to the same entity...
                // this is needed to update entity view...
                if (dbRelationship.getSourceEntity() == dbRelationship.getTargetEntity()) {
                    projectController.fireEvent(
                            new DbRelationshipEvent(
                                    this,
                                    reverseRelationship,
                                    reverseRelationship.getSourceEntity(),
                                    MapEvent.ADD));
                }
            } else {
                handleNameUpdate(reverseRelationship, reverseRelationshipField.getText().trim());
            }

            Collection<DbJoin> reverseJoins = getReverseJoins();
            reverseRelationship.setJoins(reverseJoins);

            // check if joins map to a primary key of this entity
            if (!dbRelationship.isToDependentPK() && reverseRelationship.isValidForDepPk()) {
                reverseRelationship.setToDependentPK(true);
            }
        }

        projectController.fireEvent(
                new DbRelationshipEvent(this, dbRelationship, dbRelationship.getSourceEntity()));


    }

    private void handleNameUpdate(DbRelationship relationship, String userInputName) {
        if(Util.nullSafeEquals(relationship.getName(), userInputName)) {
            return;
        }

        String sourceEntityName = NameBuilder
                .builder(relationship, relationship.getSourceEntity())
                .baseName(userInputName)
                .name();

        if (Util.nullSafeEquals(sourceEntityName, relationship.getName())) {
            return;
        }
        String oldName = relationship.getName();
        relationship.setName(sourceEntityName);
//        undo.addNameUndo(relationship, oldName, sourceEntityName);

        projectController.fireEvent(
                new DbRelationshipEvent(this, relationship, relationship.getSourceEntity(), oldName));
    }

    private Collection<DbJoin> getReverseJoins() {
        Collection<DbJoin> joins = dbRelationship.getJoins();

        if ((joins == null) || (joins.size() == 0)) {
            return Collections.emptyList();
        }

        List<DbJoin> reverseJoins = new ArrayList<>(joins.size());

        // Loop through the list of attribute pairs, create reverse pairs
        // and put them to the reverse list.
        for (DbJoin pair : joins) {
            DbJoin reverseJoin = pair.createReverseJoin();

            // since reverse relationship is not yet initialized,
            // reverse join will not have it set automatically
            reverseJoin.setRelationship(reverseRelationship);
            reverseJoins.add(reverseJoin);
        }

        return reverseJoins;
    }
}
