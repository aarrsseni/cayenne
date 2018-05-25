package org.apache.cayenne.modeler.components;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.util.ComboBoxCellConverter;
import org.apache.cayenne.modeler.util.IconUtil;

public class AutoCompleteComboBoxWithIcon<O extends Observer, T> extends CustomComboBoxCell<O, T> {

    public AutoCompleteComboBoxWithIcon(ObservableList<String> types, ComboBoxCellConverter<T> comboBoxCellConverter) {
        super(types, comboBoxCellConverter);
        getComboBox().setCellFactory(cell -> new ListCell<String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty)
                    setGraphic(null);
                else {
                    setGraphic(new HBox(IconUtil.imageForObject(new DbEntity()), new Label(item)));
                }

                setText("");
            }
        });
        new AutoCompleteComboBoxListener<>(getComboBox());
    }
}