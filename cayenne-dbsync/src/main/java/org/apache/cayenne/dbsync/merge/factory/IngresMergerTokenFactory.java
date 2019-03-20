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

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.QuotingStrategy;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.dbsync.merge.token.db.AddJoinToDb;
import org.apache.cayenne.dbsync.merge.token.db.DropColumnToDb;
import org.apache.cayenne.dbsync.merge.token.db.DropJoinToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetAllowNullToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetColumnTypeToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetGeneratedFlagToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetNotNullToDb;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.JoinVisitor;
import org.apache.cayenne.map.relationship.RelationshipDirection;

public class IngresMergerTokenFactory extends DefaultMergerTokenFactory {

    @Override
    public MergerToken createSetColumnTypeToDb(final DbEntity entity, DbAttribute columnOriginal,
            final DbAttribute columnNew) {

        return new SetColumnTypeToDb(entity, columnOriginal, columnNew) {

            @Override
            protected void appendPrefix(StringBuffer sqlBuffer, QuotingStrategy context) {
                sqlBuffer.append("ALTER TABLE ");
                sqlBuffer.append(context.quotedFullyQualifiedName(entity));
                sqlBuffer.append(" ALTER COLUMN ");
                sqlBuffer.append(context.quotedName(columnNew));
                sqlBuffer.append(" ");
            }
        };
    }

    @Override
    public MergerToken createDropColumnToDb(DbEntity entity, DbAttribute column) {
        return new DropColumnToDb(entity, column) {

            @Override
            public List<String> createSql(DbAdapter adapter) {
                StringBuilder buf = new StringBuilder();
                QuotingStrategy context = adapter.getQuotingStrategy();
                buf.append("ALTER TABLE ");
                buf.append(context.quotedFullyQualifiedName(getEntity()));
                buf.append(" DROP COLUMN ");
                buf.append(context.quotedName(getColumn()));
                buf.append(" RESTRICT ");

                return Collections.singletonList(buf.toString());
            }

        };
    }

    @Override
    public MergerToken createAddJoinToDb(DbJoin join) {
        return new AddJoinToDb(join) {
            @Override
            public List<String> createSql(DbAdapter adapter) {
                RelationshipDirection direction = AddJoinToDb.getRelationshipDirection(join);
                DataMap dataMap = join.getDataMap();
                DbEntity source = dataMap.getDbEntity(join.getDbEntities()[direction.ordinal()]);
                QuotingStrategy context = adapter.getQuotingStrategy();
                StringBuilder buf = new StringBuilder();
                StringBuilder refBuf = new StringBuilder();

                buf.append("ALTER TABLE ");
                buf.append(context.quotedFullyQualifiedName(source));

                    // requires the ADD CONSTRAINT statement
                buf.append(" ADD CONSTRAINT ");
                String name = "U_" + source.getName() + "_"
                        + (long) (System.currentTimeMillis() / (Math.random() * 100000));

                buf.append(context.quotedIdentifier(source, name));
                buf.append(" FOREIGN KEY (");

                join.getDbJoinCondition().accept(new JoinVisitor<Void>() {

                    private void append(ColumnPair columnPair) {
                        if(direction == RelationshipDirection.LEFT) {
                            buf.append(context
                                    .quotedIdentifier(dataMap,
                                            columnPair.getLeft()));
                            refBuf.append(context
                                    .quotedIdentifier(dataMap,
                                            columnPair.getRight()));
                        } else {
                        buf.append(context
                                .quotedIdentifier(dataMap,
                                        columnPair.getRight()));
                        refBuf.append(context
                                .quotedIdentifier(dataMap,
                                        columnPair.getLeft()));
                    }
                }

                @Override
                public Void visit(ColumnPair columnPair) {
                    append(columnPair);
                    return null;
                    }

                    @Override
                    public Void visit(ColumnPair[] columnPairs) {
                        boolean first = true;
                        for(ColumnPair columnPair : columnPairs) {
                            if (!first) {
                                buf.append(", ");
                                refBuf.append(", ");
                            } else {
                                first = false;
                            }
                            append(columnPair);
                        }
                        return null;
                    }
                });
                buf.append(") REFERENCES ");
                DbEntity targetEntity = dataMap
                        .getDbEntity(
                                join.getDbEntities()[direction.getOppositeDirection().ordinal()]);
                buf.append(context.quotedFullyQualifiedName(targetEntity));
                buf.append(" (");
                buf.append(refBuf.toString());
                buf.append(')');

                // also make sure we delete dependent FKs
                buf.append(" ON DELETE CASCADE");

                return Collections.singletonList(buf.toString());
            }
        };
    }

    @Override
    public MergerToken createSetNotNullToDb(DbEntity entity, DbAttribute column) {
        return new SetNotNullToDb(entity, column) {

            @Override
            public List<String> createSql(DbAdapter adapter) {

                /*
                 * TODO: we generate this query as in ingres db documentation,
                 * but unfortunately ingres don't support it
                 */

                StringBuilder sqlBuffer = new StringBuilder();

                QuotingStrategy context = adapter.getQuotingStrategy();

                sqlBuffer.append("ALTER TABLE ");
                sqlBuffer.append(getEntity().getFullyQualifiedName());
                sqlBuffer.append(" ALTER COLUMN ");
                sqlBuffer.append(context.quotedName(getColumn()));
                sqlBuffer.append(" ");
                sqlBuffer.append(adapter.externalTypesForJdbcType(getColumn().getType())[0]);

                if (adapter.typeSupportsLength(getColumn().getType()) && getColumn().getMaxLength() > 0) {
                    sqlBuffer.append("(");
                    sqlBuffer.append(getColumn().getMaxLength());
                    sqlBuffer.append(")");
                }

                sqlBuffer.append(" NOT NULL");

                return Collections.singletonList(sqlBuffer.toString());
            }

        };
    }

    @Override
    public MergerToken createSetAllowNullToDb(DbEntity entity, DbAttribute column) {
        return new SetAllowNullToDb(entity, column) {

            @Override
            public List<String> createSql(DbAdapter adapter) {
                StringBuilder sqlBuffer = new StringBuilder();
                QuotingStrategy context = adapter.getQuotingStrategy();
                sqlBuffer.append("ALTER TABLE ");
                sqlBuffer.append(context.quotedFullyQualifiedName(getEntity()));
                sqlBuffer.append(" ALTER COLUMN ");
                sqlBuffer.append(context.quotedName(getColumn()));
                sqlBuffer.append(" ");
                sqlBuffer.append(adapter.externalTypesForJdbcType(getColumn().getType())[0]);

                if (adapter.typeSupportsLength(getColumn().getType()) && getColumn().getMaxLength() > 0) {
                    sqlBuffer.append("(");
                    sqlBuffer.append(getColumn().getMaxLength());
                    sqlBuffer.append(")");
                }

                sqlBuffer.append(" WITH NULL");

                return Collections.singletonList(sqlBuffer.toString());
            }

        };
    }

    @Override
    public MergerToken createDropJoinToDb(DbJoin dbJoin) {
        return new DropJoinToDb(dbJoin) {

            @Override
            public List<String> createSql(DbAdapter adapter) {
                String fkName = getFkName();

                if (fkName == null) {
                    return Collections.emptyList();
                }
                RelationshipDirection direction = AddJoinToDb.getRelationshipDirection(dbJoin);
                DataMap dataMap = dbJoin.getDataMap();
                DbEntity entity = dataMap.getDbEntity(
                        dbJoin.getDbEntities()[direction.ordinal()]);
                StringBuilder buf = new StringBuilder();
                buf.append("ALTER TABLE ");
                buf.append(adapter.getQuotingStrategy().quotedFullyQualifiedName(entity));
                buf.append(" DROP CONSTRAINT ");
                buf.append(fkName);
                buf.append(" CASCADE ");

                return Collections.singletonList(buf.toString());
            }

        };
    }

    @Override
    public MergerToken createSetGeneratedFlagToDb(DbEntity entity, DbAttribute column, boolean isGenerated) {
        return new SetGeneratedFlagToDb(entity, column, isGenerated) {
            @Override
            protected void appendAutoIncrement(DbAdapter adapter, StringBuffer builder) {
                throw new UnsupportedOperationException("Can't automatically alter column to IDENTITY in Ingres database. You should do this manually.");
            }

            @Override
            protected void appendDropAutoIncrement(DbAdapter adapter, StringBuffer builder) {
                builder.append("DROP IDENTITY");
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
    }
}
