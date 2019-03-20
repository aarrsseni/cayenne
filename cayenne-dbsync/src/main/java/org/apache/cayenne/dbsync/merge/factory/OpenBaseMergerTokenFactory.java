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
package org.apache.cayenne.dbsync.merge.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.dbsync.merge.token.db.AddJoinToDb;
import org.apache.cayenne.dbsync.merge.token.db.CreateTableToDb;
import org.apache.cayenne.dbsync.merge.token.db.DropJoinToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetAllowNullToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetColumnTypeToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetNotNullToDb;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.JoinVisitor;
import org.apache.cayenne.map.relationship.RelationshipDirection;

public class OpenBaseMergerTokenFactory extends DefaultMergerTokenFactory {

    @Override
    public MergerToken createCreateTableToDb(DbEntity entity) {
        return new CreateTableToDb(entity) {

            @Override
            public List<String> createSql(DbAdapter adapter) {
                List<String> sqls = new ArrayList<>();
                // create table first as OpenBase adapter created primary key in its
                // getPkGenerator().createAutoPkStatements
                sqls.add(adapter.createTable(getEntity()));
                sqls.addAll(adapter.getPkGenerator().createAutoPkStatements(
                        Collections.singletonList(getEntity())));
                return sqls;
            }

        };
    }

    @Override
    public MergerToken createDropJoinToDb(DbJoin dbJoin) {
        return new DropJoinToDb(dbJoin) {
            @Override
            public List<String> createSql(DbAdapter adapter) {

                // FK_NAME form jdbc metadata seem to be wrong. It contain a column name
                // and not the 'relationshipName'
                // TODO: tell openbase developer mail list

                RelationshipDirection direction = AddJoinToDb.getRelationshipDirection(dbJoin);
                DataMap dataMap = dbJoin.getDataMap();
                DbEntity source = dataMap.getDbEntity(
                        dbJoin.getDbEntities()[direction.ordinal()]);
                DbEntity dest = dataMap
                        .getDbEntity(
                                dbJoin.getDbEntities()[direction.getOppositeDirection().ordinal()]);

                // only use the first. See adapter
                // TODO: can we be sure this is the first and same as used by the adapter?
                return dbJoin.getDbJoinCondition().accept(new JoinVisitor<List<String>>() {

                    private List<String> buildList(ColumnPair columnPair) {
                        if(direction == RelationshipDirection.LEFT) {
                            return Collections.singletonList("delete from _SYS_RELATIONSHIP where "
                                    + " source_table = '" + dest.getFullyQualifiedName() + "'"
                                    + " and source_column = '" + columnPair.getRight() + "'"
                                    + " and dest_table = '" + source.getFullyQualifiedName() + "'"
                                    + " and dest_column = '" + columnPair.getLeft() + "'");
                        } else {
                            return Collections.singletonList("delete from _SYS_RELATIONSHIP where "
                                    + " source_table = '" + dest.getFullyQualifiedName() + "'"
                                    + " and source_column = '" + columnPair.getLeft() + "'"
                                    + " and dest_table = '" + source.getFullyQualifiedName() + "'"
                                    + " and dest_column = '" +  columnPair.getRight() + "'");
                        }
                    }

                    @Override
                    public List<String> visit(ColumnPair columnPair) {
                        return buildList(columnPair);
                    }

                    @Override
                    public List<String> visit(ColumnPair[] columnPairs) {
                        return buildList(columnPairs[0]);
                    }
                });
            }
        };
    }

    @Override
    public MergerToken createSetColumnTypeToDb(
            final DbEntity entity,
            final DbAttribute columnOriginal,
            final DbAttribute columnNew) {
        return new SetColumnTypeToDb(entity, columnOriginal, columnNew) {

            @Override
            public List<String> createSql(DbAdapter adapter) {
                List<String> sqls = new ArrayList<>();

                if (columnOriginal.getMaxLength() != columnNew.getMaxLength()) {
                    sqls.add("ALTER TABLE "
                            + entity.getFullyQualifiedName()
                            + " COLUMN "
                            + columnNew.getName()
                            + " SET LENGTH "
                            + columnNew.getMaxLength());
                }

                return sqls;
            }

        };
    }

    @Override
    public MergerToken createSetNotNullToDb(DbEntity entity, DbAttribute column) {
        return new SetNotNullToDb(entity, column) {

            @Override
            public List<String> createSql(DbAdapter adapter) {

                return Collections.singletonList("ALTER TABLE " + getEntity().getFullyQualifiedName()
                        + " COLUMN " + getColumn().getName() + " SET NOT NULL");
            }

        };
    }

    @Override
    public MergerToken createSetAllowNullToDb(DbEntity entity, DbAttribute column) {
        return new SetAllowNullToDb(entity, column) {

            @Override
            public List<String> createSql(DbAdapter adapter) {

                return Collections.singletonList("ALTER TABLE " + getEntity().getFullyQualifiedName()
                        + " COLUMN " + getColumn().getName() + " SET NULL");
            }

        };
    }
}
