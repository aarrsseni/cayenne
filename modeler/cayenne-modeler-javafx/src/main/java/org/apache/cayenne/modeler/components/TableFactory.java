package org.apache.cayenne.modeler.components;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.DataBaseMappingController;
import org.apache.cayenne.modeler.controller.DbRelationshipsController;
import org.apache.cayenne.modeler.controller.ObjEntityController;
import org.apache.cayenne.modeler.controller.ObjRelationshipsController;
import org.apache.cayenne.modeler.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableFactory {

    @Inject
    private CayenneTableColumnFactory cayenneTableColumnFactory;

    @Inject
    public ProjectController projectController;

    @Inject
    public ObjEntityController objEntityController;

    @Inject
    public DbRelationshipsController dbRelationshipsController;

    @Inject
    public DataBaseMappingController dataBaseMappingController;

    @Inject
    public Consumer consumer;

    @Inject
    public ObjRelationshipsController objRelationshipsController;

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
                cayenneTableColumnFactory.createBooleanColumnWithListeners("To Dep PK", "toDependentPK", consumer),
                cayenneTableColumnFactory.createBooleanColumn("To many", "toMany")
        ));
    }

    public List createDbJoinTable() {
        return new ArrayList<>(Arrays.asList(
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("Source name", "sourceName", dataBaseMappingController.getDbSourceAttrs() , new DefaultComboBoxCellConverter()),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("Target name", "targetName", dataBaseMappingController.getDbTargetAttrs(), new DefaultComboBoxCellConverter())
        ));
    }

    public List createObjRelationshipsTable() {
        return new ArrayList<>(Arrays.asList(
                cayenneTableColumnFactory.createTextColumn("Name", "name"),
                cayenneTableColumnFactory.createDefaultReadOnlyColumn("Target", "targetEntityName"),
                cayenneTableColumnFactory.createComboBoxWithCustomProperty("DbRelationship Path", "dbPath", objRelationshipsController.getDbAttrPath(), new DefaultComboBoxCellConverter()),
                cayenneTableColumnFactory.createReadOnlyColumn("Semantics", "semantics"),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("Delete rule", "deleteRule", FXCollections.observableArrayList(ModelerUtils.getDeleteRules()), new ComboBoxStringIntegerConverter()),
                cayenneTableColumnFactory.createBooleanColumn("Used for locking", "usedForLocking")
        ));
    }
}
