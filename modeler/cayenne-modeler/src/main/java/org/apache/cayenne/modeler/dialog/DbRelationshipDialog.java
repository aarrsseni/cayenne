/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler.dialog;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Optional;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.map.event.RelationshipEvent;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.DbRelationshipDialogView;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.model.DbRelationshipModelConverter;
import org.apache.cayenne.modeler.event.RelationshipDisplayEvent;
import org.apache.cayenne.modeler.map.relationship.DbJoinModel;
import org.apache.cayenne.modeler.map.relationship.DbJoinMutable;
import org.apache.cayenne.modeler.undo.CreateRelationshipUndoableEdit;
import org.apache.cayenne.modeler.undo.RelationshipUndoableEdit;
import org.apache.cayenne.modeler.util.CayenneController;
import org.apache.cayenne.modeler.util.ModelerUtil;
import org.apache.cayenne.modeler.util.combo.AutoCompletion;
import org.apache.cayenne.project.extension.info.ObjectInfo;
import org.apache.cayenne.util.Util;

/**
 * @since 4.2
 */
public class DbRelationshipDialog extends CayenneController {

    private DbRelationshipDialogView view;

    private boolean isCreate = false;
    private DbRelationship prevDbRelationship;

    private DbJoinModel dbJoinModel;

    private ProjectController projectController;

    private RelationshipUndoableEdit undo;

    private DataMap dataMap;

    public DbRelationshipDialog(ProjectController projectController) {
        this.view = new DbRelationshipDialogView(this);
        this.projectController = projectController;
        this.dataMap = projectController.getCurrentDataMap();
    }

    @Override
    public Component getView() {
        return view;
    }

    public DbRelationshipDialog createNewRelationship(DbEntity dbEntity) {
        isCreate = true;

        dbJoinModel = new DbJoinModel();

        validateDbEntity(dbEntity);
        dbJoinModel.setLeftEntity(dbEntity);
        dbJoinModel.setLeftName(NameBuilder.builder(new DbRelationship(), dbEntity).name());

        initFromModel();
        initController();
        return this;
    }

    private void validateDbEntity(DbEntity dbEntity) {
        if (dbEntity == null) {
            throw new CayenneRuntimeException("Null source entity: %s", dbJoinModel);
        }
        if (dbEntity.getDataMap() == null) {
            throw new CayenneRuntimeException("Null DataMap: %s", dbJoinModel.getLeftEntity());
        }
    }

    public DbRelationshipDialog modifyRaltionship(DbRelationship dbRelationship) {
        isCreate = false;
        this.dbJoinModel = new DbRelationshipModelConverter().getModel(dbRelationship);
        this.undo = new RelationshipUndoableEdit(dbJoinModel, dbRelationship);
        this.prevDbRelationship = dbRelationship;

        initController();
        initFromModel();
        return this;
    }

    public void startUp() {
        view.setVisible(true);
        view.dispose();
    }

    private void initFromModel() {
        TargetComboBoxModel targetComboBoxModel = new TargetComboBoxModel(dbJoinModel
                .getLeftEntity().getDataMap().getDbEntities());
        view.getTargetEntities().setModel(targetComboBoxModel);

        view.getSourceName().setText(dbJoinModel.getLeftEntity().getName());
        view.getToDepPk().setSelected(dbJoinModel.getLeftToDepPK());
        view.getToMany().setSelected(dbJoinModel.getLeftToMany());

        view.getNameField().setText(dbJoinModel.getLeftName());
        view.getReverseName().setText(dbJoinModel.getRightName());

        if(dbJoinModel.getRightEntity() == null) {
            enableOptions(false);
        } else {
            enableInfo();
        }

        view.getComment().setText(dbJoinModel.getComment());
    }


    private void initController() {
        view.getTargetEntities().addActionListener(action -> {
            String selectedItem = (String) view.getTargetEntities().getSelectedItem();
            if(dbJoinModel.getRightEntity() == null) {
                dbJoinModel.setRightEntity(dataMap.getDbEntity(selectedItem));
            } else if(!dbJoinModel.getRightEntity().getName().equals(selectedItem)) {
                DbEntity srcEntity = dbJoinModel.getLeftEntity();
                if (WarningDialogByDbTargetChange.showWarningDialog(projectController,
                        srcEntity,
                        dbJoinModel.getLeftName())) {
                    if(!isCreate) {
                        srcEntity.getDataMap().getDbJoinList().remove(prevDbRelationship.getDbJoin());
                        dbJoinModel.getRightEntity()
                                .removeRelationship(dbJoinModel.getRightName());
                    }
                    srcEntity.removeRelationship(dbJoinModel.getLeftName());
                    // clear joins...
                    dbJoinModel.getColumnPairs().clear();
                    dbJoinModel.setRightEntity(dataMap.getDbEntity(selectedItem));
                    isCreate = true;
                } else {
                    view.getTargetEntities().setSelectedItem(dbJoinModel.getRightEntity());
                }
            }
            enableInfo();
        });

        view.getToDepPk().setEnabled(dbJoinModel.isValidForDepPk());

        view.getToDepPk().addActionListener(selected -> {
            boolean isSelected = view.getToDepPk().isSelected();

            if(dbJoinModel.getRightToDepPK() && isSelected) {
                boolean setToDepPk = JOptionPane.showConfirmDialog(Application.getFrame(), "Unset reverse relationship's \"To Dep PK\" setting?",
                        "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
                dbJoinModel.setLeftToDepPK(setToDepPk);
                dbJoinModel.setRightToDepPK(!setToDepPk);
            } else {
                dbJoinModel.setLeftToDepPK(view.getToDepPk().isSelected());
            }
        });

        view.getSaveButton().addActionListener(e -> {
            if(save()) {
                view.setCancelPressed(false);
                view.dispose();
                view.setVisible(false);
            }
        });

        view.getCancelButton().addActionListener(e -> {
            view.setCancelPressed(true);
            view.setVisible(false);
        });

        view.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) {
                view.setCancelPressed(true);
            }
        });

        view.getAddButton().addActionListener(e -> {
            ColumnPairsTableModel model = (ColumnPairsTableModel) view.getTable().getModel();

            ColumnPair columnPair = new ColumnPair();
            model.addRow(columnPair);

            view.getTable().select(model.getRowCount() - 1);
        });

        view.getRemoveButton().addActionListener(e -> {
            ColumnPairsTableModel model = (ColumnPairsTableModel) view.getTable().getModel();
            stopEditing();
            int row = view.getTable().getSelectedRow();

            ColumnPair columnPair = model.getColumnPair(row);
            dbJoinModel.getColumnPairs().remove(columnPair);

            if(dbJoinModel.isValidForDepPk()) {
                view.getToDepPk().setEnabled(true);
            } else {
                view.getToDepPk().setEnabled(false);
                view.getToDepPk().setSelected(false);
                dbJoinModel.setLeftToDepPK(false);
            }

            model.removeRow(columnPair);
        });

        view.getToMany().addActionListener(action -> {
            dbJoinModel.setLeftToMany(view.getToMany().isSelected());
        });
    }

    private void enableInfo() {
        enableOptions(true);

        view.getTable().setModel(new ColumnPairsTableModel(dbJoinModel,
                projectController, this, true));

        view.getTable().getModel().addTableModelListener(change -> {
            if(change.getLastRow() != Integer.MAX_VALUE) {
                if(dbJoinModel.isValidForDepPk()) {
                    view.getToDepPk().setEnabled(true);
                } else {
                    view.getToDepPk().setEnabled(false);
                }
            }
        });

        TableColumn sourceColumn = view.getTable().getColumnModel().getColumn(ColumnPairsTableModel.SOURCE);
        JComboBox comboBox = Application.getWidgetFactory().createComboBox(
                ModelerUtil.getDbAttributeNames(dbJoinModel.getLeftEntity()), true);

        AutoCompletion.enable(comboBox);
        sourceColumn.setCellEditor(Application.getWidgetFactory().createCellEditor(comboBox));

        TableColumn targetColumn = view.getTable().getColumnModel().getColumn(ColumnPairsTableModel.TARGET);
        comboBox = Application.getWidgetFactory().createComboBox(
                ModelerUtil.getDbAttributeNames(dbJoinModel.getRightEntity()), true);
        AutoCompletion.enable(comboBox);

        targetColumn.setCellEditor(Application.getWidgetFactory().createCellEditor(comboBox));
    }

    private void enableOptions(boolean enable) {
        view.enableOptions(enable);
    }

    private void stopEditing() {
        // Stop whatever editing may be taking place
        int col_index = view.getTable().getEditingColumn();
        if (col_index >= 0) {
            TableColumn col = view.getTable().getColumnModel().getColumn(col_index);
            col.getCellEditor().stopCellEditing();
        }
    }

    private boolean save() {
        stopEditing();

        ColumnPairsTableModel model = (ColumnPairsTableModel) view.getTable().getModel();
        boolean joinsSelected = model.getObjectList().size() > 0;
        boolean joinsSelectedContainsEmptyJoins = false;
        for(ColumnPair columnPair : model.getObjectList()) {
            if(columnPair.getLeft() == null || columnPair.getRight() == null ||
                    !columnPair.getLeft().isEmpty() || !columnPair.getRight().isEmpty()) {
                joinsSelectedContainsEmptyJoins = true;
            }
        }

        if(!joinsSelected && joinsSelectedContainsEmptyJoins) {
            JOptionPane.showMessageDialog(
                    view,
                    "No joins were selected. " +
                            "To create relationship you need to add join.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        DbRelationship relationship, reverseRelationship;
        model.commit();
        DbJoinMutable dbJoin;
        if(isCreate) {
            DbRelationshipModelConverter converter = new DbRelationshipModelConverter();
            dbJoin = converter.createDbJoin(dbJoinModel, dataMap);
            dbJoin.compile(dataMap);
            relationship = dbJoin.getRelationhsip();
            prevDbRelationship = relationship;
            reverseRelationship = relationship.getReverseRelationship();
            if (relationship.getSourceEntity() == relationship.getTargetEntity()) {
                projectController
                        .fireDbRelationshipEvent(
                                new RelationshipEvent(
                                        this,
                                        reverseRelationship,
                                        reverseRelationship.getSourceEntity(),
                                        MapEvent.ADD));
            }
        } else {
            dbJoin = new DbRelationshipModelConverter()
                    .updateDbRelationships(dbJoinModel, prevDbRelationship);
            relationship = dbJoin.getRelationship(prevDbRelationship.getDirection());
            reverseRelationship = relationship.getReverseRelationship();
        }

        handleNameUpdate(relationship, dbJoinModel.getLeftName().trim());
        String reverseName = dbJoinModel.getRightName();
        handleNameUpdate(reverseRelationship, reverseName != null && !reverseName.isEmpty() ?
                reverseName.trim() :
                null);
        reverseRelationship.setRuntime(false);
        ObjectInfo.putToMetaData(projectController.getApplication().getMetaData(),
                dbJoin,
                ObjectInfo.COMMENT,
                dbJoinModel.getComment());
        fireDbRelationshipEvent(isCreate, relationship);
        return true;
    }

    private void handleNameUpdate(DbRelationship relationship, String userInputName) {
        if(Util.nullSafeEquals(relationship.getName(), userInputName)) {
            return;
        }

        String sourceEntityName = NameBuilder
                .builder(relationship, relationship.getSourceEntity())
                .baseName(userInputName)
                .name();

        if (Util.nullSafeEquals(sourceEntityName, relationship.getName())) {
            return;
        }
        String oldName = relationship.getName();
        relationship.setName(sourceEntityName);

        projectController
                .fireDbRelationshipEvent(
                new RelationshipEvent(this, relationship, relationship.getSourceEntity(), oldName));
    }

    private void fireDbRelationshipEvent(boolean isCreate, DbRelationship relationship) {
        if(!isCreate) {
            projectController
                    .fireDbRelationshipEvent(
                            new RelationshipEvent(this, relationship, relationship.getSourceEntity(), MapEvent.CHANGE));
            Application.getInstance().getUndoManager().addEdit(undo);
        } else {
            DbEntity dbEntity = relationship.getSourceEntity();
            projectController.fireDbRelationshipEvent(new RelationshipEvent(this, relationship, dbEntity, MapEvent.ADD));
            RelationshipDisplayEvent rde = new RelationshipDisplayEvent(this, relationship, dbEntity, projectController.getCurrentDataMap(),
                    (DataChannelDescriptor) projectController.getProject().getRootNode());
            projectController.fireDbRelationshipDisplayEvent(rde);
            Application.getInstance().getUndoManager().addEdit(
                    new CreateRelationshipUndoableEdit(relationship.getSourceEntity(), new DbRelationship[]{relationship}));
        }
    }

    public DbJoinModel getDbJoinModel() {
        return dbJoinModel;
    }

    public Optional<DbRelationship> getRelationship() {
        return view.isCancelPressed() ? Optional.empty() : Optional.of(prevDbRelationship);
    }

    final class TargetComboBoxModel extends DefaultComboBoxModel<String> {

        TargetComboBoxModel(Collection<DbEntity> dbEntities) {
            super();
            dbEntities.forEach(dbEntity -> this.addElement(dbEntity.getName()));
            if(dbJoinModel.getRightEntity() == null) {
                this.setSelectedItem(null);
            } else {
                this.setSelectedItem(dbJoinModel.getRightEntity().getName());
            }
        }

    }
}
