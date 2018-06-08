package org.apache.cayenne.modeler.controller.objEntity;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.map.*;
import org.apache.cayenne.map.event.EntityEvent;
import org.apache.cayenne.map.event.ObjEntityListener;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DbAttributeDisplayEvent;
import org.apache.cayenne.modeler.event.ObjAttributeDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DbAttributeDisplayListener;
import org.apache.cayenne.modeler.event.listener.ObjAttributeDisplayListener;
import org.apache.cayenne.modeler.jFx.component.factory.TableFactory;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;

import java.util.HashMap;
import java.util.Map;

public class ObjAttributesController implements ObjAttributeDisplayListener, ObjEntityListener, DbAttributeDisplayListener {

    private TableView<Observer> tableView;

    private Map<ObjEntity, ObservableList<Observer>> objAttrsMap;

    private ObjEntity objEntity;

    private ObservableList<String> dbEntityList;

    private ObservableList<String> dbAttrObservableList;

    @Inject
    private TableFactory tableFactory;

    @Inject
    private ProjectController projectController;

    public ObjAttributesController() {
    }

    public void init(TableView<Observer> tableView){
        setTableView(tableView);

        this.objAttrsMap = new HashMap<>();
        this.dbAttrObservableList = FXCollections.observableArrayList();
        this.dbEntityList = FXCollections.observableArrayList();

        prepareTable();
        initListeners();
    }

    public void bindTable(ObjEntity objEntity){
        setObjEntity(objEntity);

        resetDbAttrPathComboBox();
        addAttrs();
    }

    public void unbindTable(){
        tableView.setItems(null);

        dbEntityList.clear();
    }

    private void prepareTable() {
        tableView.getColumns().addAll(tableFactory.createObjTable());

        tableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    private void initListeners() {
        projectController.getEventController().addListener(ObjAttributeDisplayListener.class, this);
        projectController.getEventController().addListener(ObjEntityListener.class, this);
        projectController.getEventController().addListener(DbAttributeDisplayListener.class, this);
    }

    private void setTableView(TableView<Observer> tableView){
        this.tableView = tableView;
    }

    public void setObjEntity(ObjEntity objEntity){
        this.objEntity = objEntity;
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

    @Override
    public void currentObjAttributeChanged(ObjAttributeDisplayEvent e) {
        checkObjAttrs(objEntity, ObserverDictionary.getObserver(e.getAttributes()[0]));

        tableView.setItems(objAttrsMap.get(objEntity));

        setDbType((ObjAttribute) e.getAttributes()[0]);
        addListenersToAttr(e.getAttributes()[0]);
    }

    private void setDbType(ObjAttribute objAttribute){
        if(objAttribute.getDbAttribute() != null) {
            ObserverDictionary.getObserver(objAttribute).getCustomPropertyWithoutBinding("dbType", String.class).setValue(
                    TypesMapping.getSqlNameByType(objAttribute.getDbAttribute().getType()));
        }
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

    private void resetDbType(){
        for(ObjAttribute objAttribute : objEntity.getAttributes()) {
            ObserverDictionary.getObserver(objAttribute).getCustomPropertyWithoutBinding("dbType", String.class).setValue(
                    null);
        }
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

    @Override
    public void currentDbAttributeChanged(DbAttributeDisplayEvent e) {
        resetDbAttrPathComboBox();
    }
}
