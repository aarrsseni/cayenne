package org.apache.cayenne.modeler.jFx.converters;

public class    DefaultComboBoxCellConverter implements ComboBoxCellConverter<String>{

    @Override
    public String toItem(String s) {
        return s;
    }

    @Override
    public String fromItem(String s) {
        return s;
    }
}
