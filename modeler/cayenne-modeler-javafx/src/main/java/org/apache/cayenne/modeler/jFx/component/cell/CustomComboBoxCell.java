package org.apache.cayenne.modeler.jFx.component.cell;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.jFx.converters.ComboBoxCellConverter;

public class CustomComboBoxCell<O extends Observer, T> extends BasicTableCell<O, T>{

    private ComboBox<String> comboBox;
    private ObservableList<String> types;

    private ComboBoxCellConverter<T> comboBoxCellConverter;

    CustomComboBoxCell(ObservableList<String> types, ComboBoxCellConverter<T> comboBoxCellConverter) {
        super();
        comboBox = new ComboBox<>();
        this.types = types;
        this.comboBoxCellConverter = comboBoxCellConverter;

        initComboBox();
        initComboBoxFocus();
    }

    @Override
    public void startEdit(){
        if(!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()){
            return;
        }

        super.startEdit();
        Platform.runLater(()-> {
            comboBox.requestFocus();
            comboBox.getEditor().end();
        });
        setGraphic(comboBox);
    }

    @Override
    public void commitEdit(T item) {
        if(item != null){
            Platform.runLater(this::requestFocus);

            setItem(item);
            setText(comboBoxCellConverter.fromItem(item));
            super.commitEdit(getItem());
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(null);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem( item, empty );
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                setText(null);
                setGraphic(comboBox);
            } else {
                if(getItem() != null) {
                    setText(comboBoxCellConverter.fromItem(getItem()));
                    comboBox.getSelectionModel().select(comboBoxCellConverter.fromItem(getItem()));
                    setGraphic(null);
                } else {
                    setText(null);
                    comboBox.getSelectionModel().clearSelection();
                    initComboBox();
                }
            }
        }
    }

    private void initComboBox(){
        comboBox.setItems(types);
        comboBox.setEditable(true);
        comboBox.setVisibleRowCount(10);

        this.widthProperty().addListener((arg0, arg1, arg2) -> {
            comboBox.setPrefWidth(arg2.doubleValue());
        });
    }

    private void initComboBoxFocus(){
        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                if(comboBox.getSelectionModel().getSelectedItem() != null) {
                    commitEdit(comboBoxCellConverter.toItem(comboBox.getSelectionModel().getSelectedItem()));
                }
            }
        });
    }

    @Override
    public void commit() {
        commitEdit(comboBoxCellConverter.toItem(comboBox.getSelectionModel().getSelectedItem()));
    }

    ComboBox<String> getComboBox() {
        return comboBox;
    }
}
