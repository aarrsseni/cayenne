package org.apache.cayenne.modeler.controller.dbEntity;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.apache.cayenne.configuration.event.DbAttributeEvent;
import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.ScreenController;
import org.apache.cayenne.modeler.event.DbAttributeDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DbAttributeDisplayListener;
import org.apache.cayenne.modeler.jFx.component.factory.TableFactory;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

import java.util.HashMap;
import java.util.Map;

public class DbAttributeController implements DbAttributeDisplayListener{

    private TableView<Observer> tableView;

    @Inject
    private TableFactory tableFactory;

    @Inject
    private ProjectController projectController;

    @Inject
    ScreenController screenController;

    private DbEntity dbEntity;

    private Map<DbEntity, ObservableList<Observer>> dbAttrsMap;

    public DbAttributeController(){
        this.dbAttrsMap = new HashMap<>();
    }

    public void init(TableView<Observer> tableView){
        setTableView(tableView);
        tableView.setEditable(true);
        prepareTable();
        initListeners();
    }

    private void initListeners(){
        projectController.getEventController().addListener(DbAttributeDisplayListener.class, this);
    }

    private void prepareTable(){
        tableView.getColumns().addAll(tableFactory.createDbTable());
        // single cell selection mode
        tableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    private void setTableView(TableView<Observer> tableView) {
        this.tableView = tableView;
    }

    public void setDbEntity(DbEntity dbEntity){
        this.dbEntity = dbEntity;
    }

    public void bindTable(DbEntity dbEntity){
        setDbEntity(dbEntity);

        for(DbAttribute dbAttribute : dbEntity.getAttributes()){
            checkDbAttrs(dbEntity, ObserverDictionary.getObserver(dbAttribute));
        }

        tableView.setItems(dbAttrsMap.get(dbEntity));
    }

    public void unbindTable(){
        if(!dbAttrsMap.isEmpty() && dbAttrsMap.containsKey(dbEntity)) {
            dbAttrsMap.get(dbEntity).clear();
        }

        tableView.setItems(null);
    }

    @Override
    public void currentDbAttributeChanged(DbAttributeDisplayEvent e) {
        checkDbAttrs(dbEntity, ObserverDictionary.getObserver(e.getAttributes()[0]));
        tableView.setItems(dbAttrsMap.get(dbEntity));

        addListenersToAttr(e.getAttributes()[0]);
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

    private void addListenersToAttr(Attribute dbAttr){
        ObserverDictionary.getObserver(dbAttr).getPropertyWithoutBinding("name").addListener(((observable, oldValue, newValue) -> {
            DbAttribute dbAttribute = projectController.getCurrentState().getDbEntity().getAttribute((String) oldValue);
            DbAttributeEvent event = new DbAttributeEvent(this, dbAttribute, projectController.getCurrentState().getDbEntity());
            event.setOldName((String) oldValue);

            dbAttribute.setName((String) newValue);
            dbAttribute.getEntity().dbAttributeChanged(event);
        }));
    }
}
