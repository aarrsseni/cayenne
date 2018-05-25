package org.apache.cayenne.modeler.components;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.modeler.BQApplication;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.DbEntityController;
import org.apache.cayenne.modeler.observer.Observer;

public class CustomTextCell extends BasicTableCell<Observer, String>{

    private TextField textField;

    @Inject
    DbEntityController dbEntityController;

    @Inject
    ProjectController projectController;

    DbAttribute dbAttribute;

    public CustomTextCell(){
        super();
        textField = new TextField();
        dbEntityController = BQApplication.getInjector().getInstance(DbEntityController.class);
        projectController = BQApplication.getInjector().getInstance(ProjectController.class);

        setGraphic(textField);

        initHelpers();
    }

//    DbAttributeEvent e;

    @Override
    public void startEdit(){
//        dbAttribute = projectController.getCurrentState().getDbEntity().getAttribute(getText());
//        e = new DbAttributeEvent(this, dbAttribute, projectController.getCurrentState().getDbEntity());
//        e.setOldName(getText());
        Platform.runLater(()-> textField.requestFocus());
        super.startEdit();
        if(!isEmpty()) {
            setGraphic(textField);
            textField.setText(getText());
            textField.selectAll();
            setText(null);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(null);
        setText(textField.getText());
    }

    @Override
    public void commitEdit(String item) {
        if(item != null) {
            Platform.runLater(this::requestFocus);
//            dbAttribute.setName(item);
//            dbAttribute.getEntity().dbAttributeChanged(e);
            super.commitEdit(item);
        }
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                setText(null);
            } else {
                if (getItem() != null) {
                    setText(getItem());
                    setGraphic(null);
                }
            }
        }
    }

    @Override
    public void commit() {
        commitEdit(textField.getText());
    }

    private void initHelpers() {
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused ) {
                commitEdit(textField.getText());
            }
        });
    }
}
