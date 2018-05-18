package org.apache.cayenne.modeler.util;

public interface ComboBoxCellConverter<T> {

    T toItem(String s);

    String fromItem(T s);
}
