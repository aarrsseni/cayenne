package org.apache.cayenne.modeler.jFx.converters;

import org.apache.cayenne.dba.TypesMapping;

public class SqlComboBoxCellConverter implements ComboBoxCellConverter<Integer> {

    @Override
    public Integer toItem(String item) {
        return TypesMapping.getSqlTypeByName(item);
    }

    @Override
    public String fromItem(Integer item) {
        return TypesMapping.getSqlNameByType(item);
    }

}
