package org.apache.cayenne.modeler.jFx.component.cell;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import org.apache.cayenne.modeler.BQApplication;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.dbEntity.DbEntityController;
import org.apache.cayenne.modeler.observer.Observer;

public class CustomTextCell extends BasicTableCell<Observer, String>{

    private TextField textField;

    DbEntityController dbEntityController;

    ProjectController projectController;

    public CustomTextCell(){
        super();
        textField = new TextField();
        dbEntityController = BQApplication.getInjector().getInstance(DbEntityController.class);
        projectController = BQApplication.getInjector().getInstance(ProjectController.class);

        setGraphic(textField);
        initHelpers();
    }

    @Override
    public void startEdit(){
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
