package org.apache.cayenne.modeler.jFx.component.cell;

import javafx.collections.ObservableList;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.jFx.converters.ComboBoxCellConverter;

public class CustomAutoCompleteComboBoxCell<O extends Observer, T> extends CustomComboBoxCell<O, T>{

    public CustomAutoCompleteComboBoxCell(ObservableList<String> types, ComboBoxCellConverter<T> comboBoxCellConverter) {
        super(types, comboBoxCellConverter);
        new AutoCompleteComboBoxListener<>(getComboBox());
    }
}
