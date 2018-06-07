package org.apache.cayenne.modeler.jFx.converters;

public interface ComboBoxCellConverter<T> {

    T toItem(String s);

    String fromItem(T s);
}
