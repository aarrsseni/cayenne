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
package org.apache.cayenne.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.relationship.DirectionalJoinVisitor;

/**
 * Locates parents by checking for the child PK in the parent DataRow under FK.
 */
class ResultScanParentAttachmentStrategy implements ParentAttachmentStrategy {

    private PrefetchProcessorNode parentNode;
    private DbRelationship currRelationship;
    private PrefetchProcessorNode node;

    // TODO: the ivar below makes this strategy STATEFUL and non-reusable. If we need a
    // stateless version down the line, will need to move this to the
    // PrefetchProcessorNode.
    private Map<Object, List<Persistent>> partitionByChild;

    ResultScanParentAttachmentStrategy(PrefetchProcessorNode node) {

        if (node.getParent() == null) {
            throw new IllegalArgumentException(
                    "ResultScanParentAttachmentStrategy works only for non-root nodes");
        }

        this.node = node;
        parentNode = (PrefetchProcessorNode) node.getParent();

        ObjRelationship relationship = node.getIncoming().getRelationship();

        List<DbRelationship> dbRelationships = relationship.getDbRelationships();
        if (dbRelationships.size() > 1) {
            throw new IllegalArgumentException(
                    "ResultScanParentAttachmentStrategy does not work for flattened relationships");
        }

        currRelationship = dbRelationships.get(0);

    }

    public void linkToParent(DataRow row, Persistent object) {

        if (partitionByChild == null) {
            indexParents();
        }

        Object key = currRelationship.accept(new ScanParentsDirectionalJoinVisitor(row, true));

        List<Persistent> parents = partitionByChild.get(key);
        if (parents != null) {
            for (Persistent parent : parents) {
                node.linkToParent(object, parent);
            }
        }
    }

    private void indexParents() {
        partitionByChild = new HashMap<>();

        List<Persistent> objects = parentNode.getObjects();
        
        // this can be null if parent node returned no rows
        if(objects == null) {
            return;
        }
        
        List<DataRow> rows = parentNode.getDataRows();
        if(rows == null) {
            return;
        }
        
        int size = objects.size();
        for (int i = 0; i < size; i++) {

            DataRow row = rows.get(i);

            Object key = currRelationship.accept(new ScanParentsDirectionalJoinVisitor(row, false));
            
            List<Persistent> parents = partitionByChild
                    .computeIfAbsent(key, k -> new ArrayList<>());

            parents.add(objects.get(i));
        }
    }

    private static class ScanParentsDirectionalJoinVisitor implements DirectionalJoinVisitor<Object> {

        private final DataRow row;
        private final boolean reverse;

        public ScanParentsDirectionalJoinVisitor(DataRow row, boolean reverse) {
            this.row = row;
            this.reverse = reverse;
        }

        @Override
        public Object visit(DbAttribute[] source, DbAttribute[] target) {
            List<Object> values = new ArrayList<>(target.length);

            for(DbAttribute attribute : reverse ? target : source) {
                values.add(row.get(attribute.getName()));
            }

            return values;
        }

        @Override
        public Object visit(DbAttribute source, DbAttribute target) {
            return reverse ?
                    row.get(target.getName()) :
                    row.get(source.getName());
        }
    }
}
