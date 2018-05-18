package org.apache.cayenne.modeler.components;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.util.ComboBoxCellConverter;

public class CayenneTableColumnFactory {

    private <T> TableColumn<Observer, T> createColumn(String columnName, String attrName){
        TableColumn<Observer, T> tableColumn = new TableColumn<>(columnName);
        tableColumn.setCellValueFactory(cell -> cell.getValue().getPropertyWithoutBinding(attrName));

        return tableColumn;
    }

    public TableColumn<Observer, String> createTextColumn(String columnName, String attrName){
        TableColumn<Observer, String> tableColumn = createColumn(columnName, attrName);
        tableColumn.setCellFactory(cell -> new CustomTextCell());
        return tableColumn;
    }

    public TableColumn<Observer, Integer> createIntegerColumn(String columnName, String attrName){
        TableColumn<Observer, Integer> tableColumn = createColumn(columnName, attrName);
        tableColumn.setCellFactory(cell -> new CustomNumberCell());
        return tableColumn;
    }

    public TableColumn<Observer, Boolean> createBooleanColumn(String columnName, String attrName){
        TableColumn<Observer, Boolean> tableColumn = createColumn(columnName, attrName);
        tableColumn.setCellFactory(cell -> new CheckBoxTableCell<>());
        return tableColumn;
    }

    public <T> TableColumn<Observer, T> createComboBoxColumn(String columnName, String attrName, ObservableList<String> list, ComboBoxCellConverter<T> comboBoxCellConverter){
        TableColumn<Observer, T> tableColumn = createColumn(columnName, attrName);
        tableColumn.setCellFactory(cell -> new CustomAutoCompleteComboBoxCell<>(list, comboBoxCellConverter));
        return tableColumn;
    }
}
