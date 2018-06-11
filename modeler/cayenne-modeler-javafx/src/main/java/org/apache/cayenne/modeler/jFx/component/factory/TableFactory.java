package org.apache.cayenne.modeler.jFx.component.factory;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import org.apache.cayenne.dba.TypesMapping;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.dbEntity.DataBaseMappingController;
import org.apache.cayenne.modeler.controller.dbEntity.DbRelationshipsController;
import org.apache.cayenne.modeler.controller.objEntity.ObjAttributesController;
import org.apache.cayenne.modeler.controller.objEntity.ObjEntityController;
import org.apache.cayenne.modeler.controller.objEntity.ObjRelationshipsController;
import org.apache.cayenne.modeler.jFx.component.DbRelationshipConsumer;
import org.apache.cayenne.modeler.jFx.converters.ComboBoxStringIntegerConverter;
import org.apache.cayenne.modeler.jFx.converters.DefaultComboBoxCellConverter;
import org.apache.cayenne.modeler.jFx.converters.SqlComboBoxCellConverter;
import org.apache.cayenne.modeler.observer.Observer;
import org.apache.cayenne.modeler.util.CoreModelerUtil;
import org.apache.cayenne.modeler.util.ModelerUtils;

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
    private DbRelationshipsController dbRelationshipsController;

    @Inject
    private DataBaseMappingController dataBaseMappingController;

    @Inject
    private DbRelationshipConsumer dbRelationshipConsumer;

    @Inject
    private ObjRelationshipsController objRelationshipsController;

    @Inject
    private ObjAttributesController objAttributesController;

    public List<? extends TableColumn<Observer, ?>> createDbTable() {
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

    public List<? extends TableColumn<Observer, ?>> createObjTable() {
        return new ArrayList<>(Arrays.asList(
                cayenneTableColumnFactory.createTextColumn("Name", "name"),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("Type", "type",
                        FXCollections.observableArrayList(CoreModelerUtil.getRegisteredTypeNames()),
                        new DefaultComboBoxCellConverter()),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("DbAttribute path", "dbAttributePath", objAttributesController.getDbAttrObservableList(), new DefaultComboBoxCellConverter()),
                cayenneTableColumnFactory.createReadOnlyColumn("Db Type", "dbType"),
                cayenneTableColumnFactory.createBooleanColumn("Used for Locking", "usedForLocking")
        ));
    }

    public List<? extends TableColumn<Observer, ?>> createDbRelationshipsTable() {
        return new ArrayList<>(Arrays.asList(
                cayenneTableColumnFactory.createTextColumn("Name", "name"),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumnWithIcon("Target", "targetEntityName", dbRelationshipsController.getTargetDbEntity(), new DefaultComboBoxCellConverter()),
                cayenneTableColumnFactory.createBooleanColumnWithListeners("To Dep PK", "toDependentPK", dbRelationshipConsumer),
                cayenneTableColumnFactory.createBooleanColumn("To many", "toMany")
        ));
    }

    public List<? extends TableColumn<Observer, ?>> createDbJoinTable() {
        return new ArrayList<>(Arrays.asList(
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("Source name", "sourceName", dataBaseMappingController.getDbSourceAttrs() , new DefaultComboBoxCellConverter()),
                cayenneTableColumnFactory.createAutocompleteComboBoxColumn("Target name", "targetName", dataBaseMappingController.getDbTargetAttrs(), new DefaultComboBoxCellConverter())
        ));
    }

    public List<? extends TableColumn<Observer, ?>> createObjRelationshipsTable() {
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
