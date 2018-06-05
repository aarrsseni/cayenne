package org.apache.cayenne.modeler.components;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import org.apache.cayenne.modeler.observer.Observer;

public class CustomNumberCell extends BasicTableCell<Observer, Integer>{

    private TextField textField;

    public CustomNumberCell(){
        super();
        textField = new TextField();
        setGraphic(textField);
        setItem(null);

        initHelpers();
    }

    @Override
    public void startEdit(){
        Platform.runLater(()-> textField.requestFocus());
        super.startEdit();
        if(!isEmpty()) {
            setGraphic(textField);
            if(-1 != getItem()) {
                textField.setText(String.valueOf(getItem()));
            } else {
                textField.setText(null);
            }
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
    public void commitEdit(Integer item) {
        if(item != null){
            Platform.runLater(this::requestFocus);
            super.commitEdit(item);
        }
    }

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                setText(null);
            } else {
                if (getItem() != null) {
                    if(-1 == getItem()) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(String.valueOf(getItem()));
                        setGraphic(null);
                    }
                }
            }
        }
    }

    @Override
    public void commit() {
        commitEdit(Integer.parseInt(textField.getText()));
    }

    private void initHelpers() {
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused ) {
                if(textField.getText() != null && !textField.getText().isEmpty()) {
                    commitEdit(Integer.parseInt(textField.getText()));
                }
            }
        });
    }
}