package org.apache.cayenne.modeler.components;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.DbRelationshipsController;
import org.apache.cayenne.modeler.controller.ObjEntityController;
import org.apache.cayenne.modeler.util.CoreModelerUtil;
import org.apache.cayenne.modeler.util.DefaultComboBoxCellConverter;
import org.apache.cayenne.modeler.util.SqlComboBoxCellConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttributeTable {

    @Inject
    private CayenneTableColumnFactory cayenneTableColumnFactory;

    @Inject
    public ProjectController projectController;

    @Inject
    public ObjEntityController objEntityController;

    @Inject
    public DbRelationshipsController dbRelationshipsController;

    public List createDbTable() {
        return new ArrayList<>(Arrays.asList(
                cayenneTableColumnFactory.createTextColumn("Name", "name"),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("Type", "type",
                        FXCollections.observableArrayList(TypesMapping.getDatabaseTypes()),
                        new SqlComboBoxCellConverter()),
                cayenneTableColumnFactory.createBooleanColumn("PK", "primaryKey"),
                cayenneTableColumnFactory.createBooleanColumn("Mandatory", "mandatory"),
                cayenneTableColumnFactory.createIntegerColumn("Max Length", "maxLength"),
//                cayenneTableColumnFactory.createTextColumn("Comments", "name"),
                cayenneTableColumnFactory.createIntegerColumn("Scale", "scale")
        ));
    }

    public List createObjTable() {
        return new ArrayList<>(Arrays.asList(
                cayenneTableColumnFactory.createTextColumn("Name", "name"),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("Type", "type",
                        FXCollections.observableArrayList(CoreModelerUtil.getRegisteredTypeNames()),
                        new DefaultComboBoxCellConverter()),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("DbAttribute path", "dbAttributePath", objEntityController.getDbAttrObservableList(), new DefaultComboBoxCellConverter()),
                cayenneTableColumnFactory.createReadOnlyColumn("Db Type", "dbType"),
                cayenneTableColumnFactory.createBooleanColumn("Used for Locking", "usedForLocking")
        ));
    }

    public List createDbRelationshipsTable() {
        return new ArrayList<>(Arrays.asList(
                cayenneTableColumnFactory.createTextColumn("Name", "name"),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumnWithIcon("Target", "targetEntityName", dbRelationshipsController.getTargetDbEntity(), new DefaultComboBoxCellConverter()),
                cayenneTableColumnFactory.createBooleanColumn("To Dep PK", "toDependentPK"),
                cayenneTableColumnFactory.createBooleanColumn("To many", "toMany")
        ));
    }
}
