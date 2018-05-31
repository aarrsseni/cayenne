package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.event.DbEntityListener;
import org.apache.cayenne.map.event.DbRelationshipListener;
import org.apache.cayenne.map.event.EntityEvent;
import org.apache.cayenne.map.event.RelationshipEvent;
import org.apache.cayenne.modeler.FXMLLoaderFactory;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.components.TableFactory;
import org.apache.cayenne.modeler.event.DbRelationshipDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DbRelationshipDisplayListener;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.modeler.util.Consumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DbRelationshipsController implements DbRelationshipDisplayListener, DbEntityListener, DbRelationshipListener{

    private TableView<Observer> tableView;

    @Inject
    TableFactory tableFactory;

    @Inject
    ProjectController projectController;

    @Inject
    ScreenController screenController;

    @Inject
    FXMLLoaderFactory fxmlLoaderFactory;

    private DbEntity dbEntity;

    private ObservableList<String> targetDbEntity;

    private ObservableList<Observer> dbRels;

    private Map<DbRelationship, TableRow> rowMap;

    private Map<DbRelationship, Property> dependentPropertyMap;

    private Consumer consumer;

    public void init(TableView tableView){
        setTableView(tableView);

        this.targetDbEntity = FXCollections.observableArrayList();
        this.dbRels = FXCollections.observableArrayList();
        this.rowMap = new HashMap<>();

        this.dependentPropertyMap = new HashMap<>();

        prepareTable();
        initListeners();
    }

    private void initListeners(){
        projectController.getEventController().addListener(DbRelationshipDisplayListener.class, this);
        projectController.getEventController().addListener(DbEntityListener.class, this);
        projectController.getEventController().addListener(DbRelationshipListener.class, this);
    }

    private void prepareTable(){
        tableView.getColumns().addAll(tableFactory.createDbRelationshipsTable());
        // single cell selection mode
//        tableView.getSelectionModel().setCellSelectionEnabled(true);

        tableView.setRowFactory( tv -> {
            TableRow<Observer> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem databaseMapping = new MenuItem("Data Base mapping");
            contextMenu.getItems().addAll(databaseMapping);
            row.setContextMenu(contextMenu);

            databaseMapping.setOnAction(val -> {
                rowMap.put((DbRelationship) row.getItem().getBean(), row);
                if (((DbRelationship) row.getItem().getBean()).getTargetEntity() == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Error");
                    alert.setContentText("Please select target DbEntity!");
                    alert.showAndWait();
                } else {

                    Stage dialog = new Stage();
                    dialog.initOwner(screenController.getPrimaryStage());
                    dialog.initModality(Modality.APPLICATION_MODAL);

                    FXMLLoader loader = fxmlLoaderFactory.getLoader(getClass().getResource("../DataBaseMapping.fxml"));
                    Pane childPane = null;
                    try {
                        childPane = loader.load();
                        ((DataBaseMappingController) loader.getController()).setDbRelationship((DbRelationship) row.getItem().getBean());
                        ((DataBaseMappingController) loader.getController()).bind();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Scene popup = new Scene(childPane);
                    dialog.setScene(popup);
                    screenController.setCurrentPopStage(dialog);
                    dialog.showAndWait();
                }
            });
            return row ;
        });
    }

    public void setDbEntity(DbEntity dbEntity){
        this.dbEntity = dbEntity;
    }

    @Override
    public void currentDbRelationshipChanged(DbRelationshipDisplayEvent e) {
        Property depProp = new SimpleBooleanProperty();
        dependentPropertyMap.put((DbRelationship) e.getRelationships()[0], depProp);

        dbRels.add(ObserverDictionary.getObserver(e.getRelationships()[0]));
        tableView.setItems(dbRels);
    }

    public void bindTable(DbEntity dbEntity){
        setDbEntity(dbEntity);

        resetDbEntities();

        for(DbRelationship dbRelationship : dbEntity.getRelationships()) {
            dbRels.add(ObserverDictionary.getObserver(dbRelationship));
            if(!dependentPropertyMap.containsKey(dbRelationship)){
                Property depProp = new SimpleBooleanProperty();
                depProp.setValue(checkForDepPK(dbRelationship));
                dependentPropertyMap.put(dbRelationship, depProp);
            } else {
                dependentPropertyMap.get(dbRelationship).setValue(checkForDepPK(dbRelationship));
            }
        }

        tableView.setItems(dbRels);
    }

    public void unbindTable(){
        targetDbEntity.clear();
        dbRels.clear();

//        setDbEntity(null);
        tableView.setItems(null);
    }

    public void setTableView(TableView<Observer> tableView) {
        this.tableView = tableView;
    }

    private void resetDbEntities(){
        for(DbEntity entity : projectController.getCurrentState().getDataMap().getDbEntities()) {
            addDbEntity(entity);
        }
    }

    public ObservableList<String> getTargetDbEntity() {
        return targetDbEntity;
    }

    public void addDbEntity(DbEntity dbEntity){
        if(!targetDbEntity.contains(dbEntity.getName())){
            targetDbEntity.add(dbEntity.getName());
        }
    }

    @Override
    public void dbEntityChanged(EntityEvent e) {
        targetDbEntity.clear();
        resetDbEntities();
        for(DbEntity dbEntity : projectController.getCurrentState().getDataMap().getDbEntities()){
            for(DbRelationship dbRelationship : dbEntity.getRelationships()){
                if(dbRelationship.getTargetEntity() == e.getEntity()){
                    //use to update field in table
                    ObserverDictionary.getObserver(dbRelationship).getPropertyWithoutBinding("targetEntityName");
                }
            }
        }
//        tableView.getColumns().clear();
//        prepareTable();
    }

    public DbEntity getDbEntity() {
        return dbEntity;
    }

    @Override
    public void dbEntityAdded(EntityEvent e) {

    }

    @Override
    public void dbEntityRemoved(EntityEvent e) {

    }

    @Override
    public void dbRelationshipChanged(RelationshipEvent e) {
        if(dependentPropertyMap.containsKey(e.getRelationship())){
            dependentPropertyMap.get(e.getRelationship()).setValue(checkForDepPK((DbRelationship)e.getRelationship()));
        } else {
            Property depProp = new SimpleBooleanProperty();
            dependentPropertyMap.get(e.getRelationship()).setValue(checkForDepPK((DbRelationship)e.getRelationship()));
            dependentPropertyMap.put((DbRelationship)e.getRelationship(), depProp);
        }
    }

    @Override
    public void dbRelationshipAdded(RelationshipEvent e) {

    }

    @Override
    public void dbRelationshipRemoved(RelationshipEvent e) {

    }

    public Map<DbRelationship, Property> getDependentPropertyMap() {
        return dependentPropertyMap;
    }

    public boolean checkForDepPK(DbRelationship dbRelationship){
        for(DbJoin dbJoin : dbRelationship.getJoins()){
            if(dbJoin.getSource().isPrimaryKey() && dbJoin.getTarget().isPrimaryKey()){
                return true;
            }
        }
        return false;
    }
}
