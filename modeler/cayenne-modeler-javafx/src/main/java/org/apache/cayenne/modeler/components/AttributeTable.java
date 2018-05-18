package org.apache.cayenne.modeler.components;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.modeler.util.SqlComboBoxCellConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttributeTable {

    @Inject
    private CayenneTableColumnFactory cayenneTableColumnFactory;

    public List createDbTable() {
        return new ArrayList<>(Arrays.asList(
                cayenneTableColumnFactory.createTextColumn("Name", "name"),
                cayenneTableColumnFactory.createComboBoxColumn("Type", "type",
                        FXCollections.observableArrayList(TypesMapping.getDatabaseTypes()),
                        new SqlComboBoxCellConverter()),
                cayenneTableColumnFactory.createBooleanColumn("PK", "primaryKey"),
                cayenneTableColumnFactory.createBooleanColumn("Mandatory", "mandatory"),
                cayenneTableColumnFactory.createIntegerColumn("Max Length", "maxLength"),
//                cayenneTableColumnFactory.createTextColumn("Comments", "name"),
                cayenneTableColumnFactory.createIntegerColumn("Scale", "scale")
        ));
    }
}
