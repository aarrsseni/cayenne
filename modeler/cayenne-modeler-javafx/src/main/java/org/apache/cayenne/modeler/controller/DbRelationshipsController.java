package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.event.DbEntityListener;
import org.apache.cayenne.map.event.EntityEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.components.AttributeTable;
import org.apache.cayenne.modeler.event.DbRelationshipDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DbRelationshipDisplayListener;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

import java.util.HashMap;
import java.util.Map;

public class DbRelationshipsController implements DbRelationshipDisplayListener, DbEntityListener{

    private TableView<Observer> tableView;

    @Inject
    AttributeTable attributeTable;

    @Inject
    ProjectController projectController;

    private Map<DbEntity, ObservableList<Observer>> dbRelsMap;

    DbEntity dbEntity;

    private ObservableList<String> targetDbEntity;

    public void init(TableView tableView, DbEntity dbEntity){
        setTableView(tableView);

        this.dbRelsMap = new HashMap<>();
        this.targetDbEntity = FXCollections.observableArrayList();

        prepareTable();
        initListeners();
    }

    private void initListeners(){
        projectController.getEventController().addListener(DbRelationshipDisplayListener.class, this);
        projectController.getEventController().addListener(DbEntityListener.class, this);
    }

    private void prepareTable(){
        tableView.getColumns().addAll(attributeTable.createDbRelationshipsTable());
        // single cell selection mode
        tableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    private void checkDbRels(DbEntity dbEntity, Observer dbRel){
        if(dbRelsMap.containsKey(dbEntity)) {
            dbRelsMap.get(dbEntity).add(dbRel);
        } else {
            ObservableList<Observer> observableList = FXCollections.observableArrayList();
            observableList.add(dbRel);
            dbRelsMap.put(dbEntity, observableList);
        }
    }

    public void setDbEntity(DbEntity dbEntity){
        this.dbEntity = dbEntity;
    }

    @Override
    public void currentDbRelationshipChanged(DbRelationshipDisplayEvent e) {
        checkDbRels(dbEntity, ObserverDictionary.getObserver(e.getRelationships()[0]));

        tableView.setItems(dbRelsMap.get(dbEntity));
    }

    public void bindTable(DbEntity dbEntity){
        setDbEntity(dbEntity);

        resetDbEntities();

        tableView.setItems(dbRelsMap.get(dbEntity));
    }

    public void unbindTable(){
        targetDbEntity.clear();

        setDbEntity(null);
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

    @Override
    public void dbEntityAdded(EntityEvent e) {

    }

    @Override
    public void dbEntityRemoved(EntityEvent e) {

    }
}
