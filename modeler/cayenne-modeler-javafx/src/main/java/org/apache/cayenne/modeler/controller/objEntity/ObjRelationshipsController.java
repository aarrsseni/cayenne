package org.apache.cayenne.modeler.controller.objEntity;

import com.google.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.event.EntityEvent;
import org.apache.cayenne.map.event.ObjEntityListener;
import org.apache.cayenne.modeler.FXMLLoaderFactory;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.ScreenController;
import org.apache.cayenne.modeler.event.ObjRelationshipDisplayEvent;
import org.apache.cayenne.modeler.event.listener.ObjRelationshipDisplayListener;
import org.apache.cayenne.modeler.jFx.component.factory.TableFactory;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.modeler.util.ModelerUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ObjRelationshipsController implements ObjRelationshipDisplayListener, ObjEntityListener {

    private TableView<Observer> tableView;

    @Inject
    private TableFactory tableFactory;

    @Inject
    private ProjectController projectController;

    @Inject
    private ScreenController screenController;

    @Inject
    private FXMLLoaderFactory fxmlLoaderFactory;

    private ObjEntity objEntity;

    private ObservableList<Observer> objRels;

    private ObservableList<String> dbAttrPath;

    private Map<Observer, ChangeListener<String>> changeListenerMap;

    ChangeListener<String> changeListener;

    public void init(TableView<Observer> tableView){
        setTableView(tableView);

        objRels = FXCollections.observableArrayList();
        dbAttrPath = FXCollections.observableArrayList();
        changeListenerMap = new HashMap<>();

        prepareTable();
        initListeners();
    }

    public void bindTable(ObjEntity objEntity){
        setObjEntity(objEntity);

        for(ObjRelationship objRelationship : objEntity.getRelationships()){
            objRels.add(ObserverDictionary.getObserver(objRelationship));
        }
        tableView.setItems(objRels);

        for(ObjRelationship objRelationship : objEntity.getRelationships()){
            ObserverDictionary.getObserver(objRelationship).getCustomPropertyWithoutBinding("semantics", String.class).setValue(getSemantics(objRelationship));

            ObserverDictionary.getObserver(objRelationship).getCustomPropertyWithoutBinding("dbPath", String.class).setValue(objRelationship.getDbRelationshipPath());
        }

        if(objEntity.getDbEntity() != null) {
            for (DbRelationship dbRelationship : objEntity.getDbEntity().getRelationships()) {
                dbAttrPath.add(dbRelationship.getName());
            }
        }

        initListenersForRels();
    }

    public void unbindTable(){
        if(changeListenerMap.size() != 0) {
            for (Observer observer : objRels) {
                observer.getCustomPropertyWithoutBinding("dbPath", String.class).removeListener(changeListenerMap.get(observer));
            }
        }

        changeListenerMap.clear();

        objRels.clear();
        dbAttrPath.clear();

        setObjEntity(null);
    }

    private void prepareTable() {
        tableView.getColumns().addAll(tableFactory.createObjRelationshipsTable());

        tableView.setRowFactory( tv -> {
            TableRow<Observer> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem databaseMapping = new MenuItem("Data Base mapping");
            contextMenu.getItems().addAll(databaseMapping);
            row.setContextMenu(contextMenu);

            databaseMapping.setOnAction(val -> {
                if (((ObjRelationship) row.getItem().getBean()).getSourceEntity().getDbEntity() == null) {
                    ModelerUtils.showErrorAlert("Please select DbEntity!");
                } else {

                    Stage dialog = new Stage();
                    dialog.initOwner(screenController.getPrimaryStage());
                    dialog.initModality(Modality.APPLICATION_MODAL);

                    FXMLLoader loader = fxmlLoaderFactory.getLoader(getClass().getResource("ObjRelationshipInspectorView.fxml"));
                    Pane childPane = null;
                    try {
                        childPane = loader.load();
                        ((ObjRelationshipInspectorController) loader.getController()).setObjRelationship((ObjRelationship) row.getItem().getBean());
                        ((ObjRelationshipInspectorController) loader.getController()).bind();
                        Scene popup = new Scene(childPane);
                        dialog.setScene(popup);
                        screenController.setCurrentPopStage(dialog);
                        dialog.showAndWait();
                    } catch (IOException e) {
                        LoggerFactory.getLogger(getClass()).error("Can't load Obj Relationship inspector view." + e);
                    }
                }
            });
            return row ;
        });
    }

    private void initListeners(){
        projectController.getEventController().addListener(ObjRelationshipDisplayListener.class, this);
        projectController.getEventController().addListener(ObjEntityListener.class, this);
    }

    private void initListenersForRels() {
        for(Observer observer : objRels){
            changeListenerMap.put(observer, (observable, oldValue, newValue) ->
                    ((ObjRelationship) observer.getBean()).setDbRelationshipPath(newValue));
            observer.getCustomPropertyWithoutBinding("dbPath", String.class).addListener(changeListenerMap.get(observer));
        }
    }

    private void setTableView(TableView<Observer> tableView) {
        this.tableView = tableView;
    }

    public void setObjEntity(ObjEntity objEntity) {
        this.objEntity = objEntity;
    }

    private String getSemantics(ObjRelationship relationship) {
        StringBuilder semantics =  new StringBuilder(20);
        semantics.append(relationship.isToMany() ? "to many" : "to one");
        if (relationship.isReadOnly()) {
            semantics.append(", read-only");
        }
        if (relationship.isToMany()) {
            String collection = "list";
            if (relationship.getCollectionType() != null) {
                int dot = relationship.getCollectionType().lastIndexOf('.');
                collection = relationship
                        .getCollectionType()
                        .substring(dot + 1)
                        .toLowerCase();
            }

            semantics.append(", ").append(collection);
        }
        return semantics.toString();
    }

    public ObservableList<String> getDbAttrPath() {
        return dbAttrPath;
    }

    @Override
    public void currentObjRelationshipChanged(ObjRelationshipDisplayEvent e) {
        resetTable(e.getEntity());
    }

    @Override
    public void objEntityChanged(EntityEvent e) {
        resetTable(e.getEntity());
    }

    @Override
    public void objEntityAdded(EntityEvent e) {

    }

    @Override
    public void objEntityRemoved(EntityEvent e) {

    }

    private void resetTable(Entity e){
        unbindTable();
        bindTable((ObjEntity)e);
    }
}
