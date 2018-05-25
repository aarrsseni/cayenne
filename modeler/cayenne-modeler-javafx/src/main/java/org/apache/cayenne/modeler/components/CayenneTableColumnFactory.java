package org.apache.cayenne.modeler.components;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.util.ComboBoxCellConverter;

public class CayenneTableColumnFactory {

    private <T> TableColumn<Observer, T> createColumn(String columnName, String attrName){
        TableColumn<Observer, T> tableColumn = new TableColumn<>(columnName);
        tableColumn.setCellValueFactory(cell -> {
            return  cell.getValue().getPropertyWithoutBinding(attrName);
        });

        return tableColumn;
    }

    public TableColumn<Observer, String> createReadOnlyColumn(String columnName, String attrName) {
        TableColumn<Observer, String> tableColumn = new TableColumn<>(columnName);
        tableColumn.setCellValueFactory(cell ->
                cell.getValue().getCustomPropertyWithoutBinding(attrName, String.class));

        tableColumn.setCellFactory(new Callback<TableColumn<Observer, String>, TableCell<Observer, String>>() {
            @Override
            public TableCell<Observer, String> call(TableColumn<Observer, String> param) {
                return new TableCell<Observer, String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setTextFill(Color.GREY);
                        if (!empty) {
                            setText(item);
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

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

    public <T> TableColumn<Observer, T> createAutocompleteComboBoxColumn(String columnName, String attrName, ObservableList<String> list, ComboBoxCellConverter<T> comboBoxCellConverter){
        TableColumn<Observer, T> tableColumn = createColumn(columnName, attrName);
        tableColumn.setCellFactory(cell -> new CustomAutoCompleteComboBoxCell<>(list, comboBoxCellConverter));
        return tableColumn;
    }

    public <T> TableColumn<Observer, T> createAutocompleteComboBoxColumnWithIcon(String columnName, String attrName, ObservableList<String> list, ComboBoxCellConverter<T> comboBoxCellConverter){
        TableColumn<Observer, T> tableColumn = createColumn(columnName, attrName);
        tableColumn.setCellFactory(cell -> new AutoCompleteComboBoxWithIcon<>(list, comboBoxCellConverter));
        return tableColumn;
    }
}
