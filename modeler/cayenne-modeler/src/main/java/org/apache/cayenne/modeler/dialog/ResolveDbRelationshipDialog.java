/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler.dialog;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.event.DbRelationshipEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.pref.TableColumnPreferences;
import org.apache.cayenne.modeler.undo.RelationshipUndoableEdit;
import org.apache.cayenne.modeler.util.CayenneDialog;
import org.apache.cayenne.modeler.util.CayenneTable;
import org.apache.cayenne.modeler.util.ModelerUtil;
import org.apache.cayenne.modeler.util.PanelFactory;
import org.apache.cayenne.modeler.util.combo.AutoCompletion;
import org.apache.cayenne.util.Util;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Editor of DbRelationship joins.
 */
public class ResolveDbRelationshipDialog extends CayenneDialog {

    protected DbRelationship relationship;
    protected DbRelationship reverseRelationship;

    protected JTextField name;
    protected JTextField reverseName;
    protected CayenneTable table;
    protected TableColumnPreferences tablePreferences;
    protected JButton addButton;
    protected JButton removeButton;
    protected JButton saveButton;
    protected JButton cancelButton;

    private boolean cancelPressed;

    private RelationshipUndoableEdit undo;

    private boolean editable = true;

    public ResolveDbRelationshipDialog(DbRelationship relationship) {
        this(relationship, true);
    }

    public ResolveDbRelationshipDialog(DbRelationship relationship, boolean editable) {
        super(Application.getFrame(), "", true);
        this.editable = editable;

        initView();
        initController();
        if(!initWithModel(relationship)){
            cancelPressed = true;
            return;
        }

        this.undo = new RelationshipUndoableEdit(relationship);

        this.pack();
        this.centerWindow();

    }

    @Override
    public void setVisible(boolean b) {
        if(b && cancelPressed) {
            return;
        }
        super.setVisible(b);
    }

    /**
     * Creates graphical components.
     */
    private void initView() {

        // create widgets
        name = new JTextField(25);
        reverseName = new JTextField(25);

        addButton = new JButton("Add");
        addButton.setEnabled(this.editable);

        removeButton = new JButton("Remove");
        removeButton.setEnabled(this.editable);

        saveButton = new JButton("Done");

        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(this.editable);

        table = new AttributeTable();

        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePreferences = new TableColumnPreferences(getClass(), "dbentity/dbjoinTable");

        // assemble
        getContentPane().setLayout(new BorderLayout());

        CellConstraints cc = new CellConstraints();
        PanelBuilder builder = new PanelBuilder(
                new FormLayout(
                        "right:max(50dlu;pref), 3dlu, fill:min(150dlu;pref), 3dlu, fill:min(50dlu;pref)",
                        "p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, top:14dlu, 3dlu, top:p:grow"));
        builder.setDefaultDialogBorder();

        builder.addSeparator("DbRelationship Information", cc.xywh(1, 1, 5, 1));
        builder.addLabel("Relationship:", cc.xy(1, 3));
        builder.add(name, cc.xywh(3, 3, 1, 1));
        builder.addLabel("Reverse Relationship", cc.xy(1, 5));
        builder.add(reverseName, cc.xywh(3, 5, 1, 1));

        builder.addSeparator("Joins", cc.xywh(1, 7, 5, 1));
        builder.add(new JScrollPane(table), cc.xywh(1, 9, 3, 3, "fill, fill"));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEADING));

        buttons.add(addButton);
        buttons.add(removeButton);

        builder.add(buttons, cc.xywh(5, 9, 1, 3));

        getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
        getContentPane().add(PanelFactory.createButtonPanel(new JButton[]{
                saveButton, cancelButton
        }), BorderLayout.SOUTH);
    }

    private boolean initWithModel(DbRelationship aRelationship) {
        // sanity check
        if (aRelationship.getSourceEntity() == null) {
            throw new CayenneRuntimeException("Null source entity: %s", aRelationship);
        }

        if (aRelationship.getSourceEntity().getDataMap() == null) {
            throw new CayenneRuntimeException("Null DataMap: %s", aRelationship.getSourceEntity());
        }

        if (aRelationship.getTargetEntity() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select target DbEntity first",
                    "Select target",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        // Once assigned, can reference relationship directly. Would it be
        // OK to assign relationship at the very top of this method?
        relationship = aRelationship;
        reverseRelationship = relationship.getReverseRelationship();

        // init UI components
        setTitle("DbRelationship Info: "
                + relationship.getSourceEntity().getName()
                + " to "
                + relationship.getTargetEntityName());

        table.setModel(new DbJoinTableModel(relationship, getMediator(), this, true));
        TableColumn sourceColumn = table.getColumnModel().getColumn(
                DbJoinTableModel.SOURCE);
        JComboBox comboBox = Application.getWidgetFactory().createComboBox(
                ModelerUtil.getDbAttributeNames(getMediator(), relationship.getSourceEntity()), true);

        AutoCompletion.enable(comboBox);
        sourceColumn.setCellEditor(Application.getWidgetFactory().createCellEditor(
                comboBox));

        TableColumn targetColumn = table.getColumnModel().getColumn(
                DbJoinTableModel.TARGET);
        comboBox = Application.getWidgetFactory().createComboBox(
                ModelerUtil.getDbAttributeNames(getMediator(), relationship.getTargetEntity()), true);
        AutoCompletion.enable(comboBox);

        targetColumn.setCellEditor(Application.getWidgetFactory().createCellEditor(
                comboBox));

        if (reverseRelationship != null) {
            reverseName.setText(reverseRelationship.getName());
        }

        name.setText(relationship.getName());
        tablePreferences.bind(table, null, null, null, DbJoinTableModel.SOURCE, true);
        return true;
    }

    private void initController() {
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DbJoinTableModel model = (DbJoinTableModel) table.getModel();

                DbJoin join = new DbJoin(relationship);
                model.addRow(join);

                undo.addDbJoinAddUndo(join);

                table.select(model.getRowCount() - 1);
            }
        });

        removeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DbJoinTableModel model = (DbJoinTableModel) table.getModel();
                stopEditing();
                int row = table.getSelectedRow();

                DbJoin join = model.getJoin(row);
                undo.addDbJoinRemoveUndo(join);

                model.removeRow(join);
            }
        });

        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancelPressed = false;

                if (editable) {
                    save();
                }

                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancelPressed = true;
                setVisible(false);
            }
        });
    }

    public boolean isCancelPressed() {
        return cancelPressed;
    }

    private void stopEditing() {
        // Stop whatever editing may be taking place
        int col_index = table.getEditingColumn();
        if (col_index >= 0) {
            TableColumn col = table.getColumnModel().getColumn(col_index);
            col.getCellEditor().stopCellEditing();
        }
    }

    private void save() {
        stopEditing();

        DbJoinTableModel model = (DbJoinTableModel) table.getModel();
        boolean updatingReverse = model.getObjectList().size() > 0;

        // handle name update
        handleNameUpdate(relationship, name.getText().trim());

        model.commit();

        // check "to dep pk" setting,
        // maybe this is no longer valid
        if (relationship.isToDependentPK() && !relationship.isValidForDepPk()) {
            relationship.setToDependentPK(false);
        }

        // If new reverse DbRelationship was created, add it to the target
        // Don't create reverse with no joins - makes no sense...
        if (updatingReverse) {

            // If didn't find anything, create reverseDbRel
            if (reverseRelationship == null) {
                reverseRelationship = new DbRelationship();
                reverseRelationship.setName(NameBuilder
                        .builder(reverseRelationship, relationship.getTargetEntity())
                        .baseName(reverseName.getText().trim())
                        .name());

                reverseRelationship.setSourceEntity(relationship.getTargetEntity());
                reverseRelationship.setTargetEntityName(relationship.getSourceEntity());
                reverseRelationship.setToMany(!relationship.isToMany());
                relationship.getTargetEntity().addRelationship(reverseRelationship);

                // fire only if the relationship is to the same entity...
                // this is needed to update entity view...
                if (relationship.getSourceEntity() == relationship.getTargetEntity()) {
                    getMediator().fireEvent(
                            new DbRelationshipEvent(
                                    this,
                                    reverseRelationship,
                                    reverseRelationship.getSourceEntity(),
                                    MapEvent.ADD));
                }
            } else {
                handleNameUpdate(reverseRelationship, reverseName.getText().trim());
            }

            Collection<DbJoin> reverseJoins = getReverseJoins();
            reverseRelationship.setJoins(reverseJoins);

            // check if joins map to a primary key of this entity
            if (!relationship.isToDependentPK() && reverseRelationship.isValidForDepPk()) {
                reverseRelationship.setToDependentPK(true);
            }
        }

        Application.getInstance().getUndoManager().addEdit(undo);

        getMediator().fireEvent(
                new DbRelationshipEvent(this, relationship, relationship.getSourceEntity()));
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
        undo.addNameUndo(relationship, oldName, sourceEntityName);

        getMediator().fireEvent(
                new DbRelationshipEvent(this, relationship, relationship.getSourceEntity(), oldName));
    }

    private Collection<DbJoin> getReverseJoins() {
        Collection<DbJoin> joins = relationship.getJoins();

        if ((joins == null) || (joins.size() == 0)) {
            return Collections.emptyList();
        }

        List<DbJoin> reverseJoins = new ArrayList<>(joins.size());

        // Loop through the list of attribute pairs, create reverse pairs
        // and put them to the reverse list.
        for (DbJoin pair : joins) {
            DbJoin reverseJoin = pair.createReverseJoin();

            // since reverse relationship is not yet initialized,
            // reverse join will not have it set automatically
            reverseJoin.setRelationship(reverseRelationship);
            reverseJoins.add(reverseJoin);
        }

        return reverseJoins;
    }

    final class AttributeTable extends CayenneTable {

        final Dimension preferredSize = new Dimension(203, 100);

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return preferredSize;
        }
    }
}
