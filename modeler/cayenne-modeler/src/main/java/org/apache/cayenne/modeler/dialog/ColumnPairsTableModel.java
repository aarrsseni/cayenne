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

import java.util.ArrayList;

import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.map.relationship.DbJoinModel;
import org.apache.cayenne.modeler.util.CayenneTableModel;

/** Model for editing DbAttributePair-s. Changes in the join attributes
 *  don't take place until commit() is called. Creation of the new
 *  DbAttributes is not allowed - user should choose from the existing ones.
*/
public class ColumnPairsTableModel extends CayenneTableModel<ColumnPair> {

    // Columns
    static final int SOURCE = 0;
    static final int TARGET = 1;

    protected DbEntity source;
    protected DbEntity target;
    private DbJoinModel dbJoinModel;

    /** Is the table editable. */
    private boolean editable;

    public ColumnPairsTableModel(
        DbJoinModel dbJoinModel,
        ProjectController mediator,
        Object src) {
        super(mediator, src, new ArrayList<>(dbJoinModel.getColumnPairs()));
        this.dbJoinModel = dbJoinModel;
        this.source = dbJoinModel.getLeftEntity();
        this.target = dbJoinModel.getRightEntity();
    }

    public ColumnPairsTableModel(
        DbJoinModel dbJoinModel,
        ProjectController mediator,
        Object src,
        boolean editable) {
        this(dbJoinModel, mediator, src);
        this.editable = editable;
    }

    public Class getElementsClass() {
        return ColumnPair.class;
    }

    /** Mode new attribute pairs from list to the DbRelationship. */
    public void commit() {
        dbJoinModel.setColumnPairs(getObjectList());
    }

    /**
     * Returns null to disable ordering.
     */
    public String getOrderingKey() {
        return null;
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        if (column == SOURCE)
            return dbJoinModel.getLeftEntity().getName();
        else if (column == TARGET)
            return dbJoinModel.getRightEntity().getName();
        else
            return "";
    }

    public ColumnPair getColumnPair(int row) {
        return (row >= 0 && row < objectList.size())
            ? objectList.get(row)
            : null;
    }

    public Object getValueAt(int row, int column) {
        ColumnPair columnPair = getColumnPair(row);
        if (columnPair == null) {
            return null;
        }

        if (column == SOURCE) {
            return columnPair.getLeft();
        }
        else if (column == TARGET) {
            return columnPair.getRight();
        }
        else {
            return null;
        }
    }

    public void setUpdatedValueAt(Object aValue, int row, int column) {
        ColumnPair columnPair = getColumnPair(row);
        if (columnPair == null) {
            return;
        }

        String value = (String) aValue;
        if (column == SOURCE) {
            if (source == null || source.getAttribute(value) == null) {
                value = null;
            }

            columnPair.setLeft(value);
        }
        else if (column == TARGET) {
            if (target == null || target.getAttribute(value) == null) {
                value = null;
            }

            columnPair.setRight(value);
        }

        fireTableRowsUpdated(row, row);
    }

    public boolean isCellEditable(int row, int col) {
        if (col == SOURCE) {
            return dbJoinModel.getLeftEntity() != null && editable;
        }
        else if (col == TARGET) {
            return dbJoinModel.getRightEntity() != null && editable;
        }

        return false;
    }

    @Override
    public boolean isColumnSortable(int sortCol) {
        return true;
    }

    @Override
    public void sortByColumn(int sortCol, boolean isAscent) {
        switch(sortCol){
            case SOURCE:
                sortByElementProperty("sourceName", isAscent);
                break;
            case TARGET:
                sortByElementProperty("targetName", isAscent);
                break;
        }
    }
}
