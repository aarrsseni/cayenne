package org.apache.cayenne.modeler.components;

import javafx.collections.ObservableList;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.util.ComboBoxCellConverter;

public class CustomAutoCompleteComboBoxCell<O extends Observer, T> extends CustomComboBoxCell<O, T>{

    public CustomAutoCompleteComboBoxCell(ObservableList<String> types, ComboBoxCellConverter<T> comboBoxCellConverter) {
        super(types, comboBoxCellConverter);
        new AutoCompleteComboBoxListener<>(getComboBox());
    }
}
