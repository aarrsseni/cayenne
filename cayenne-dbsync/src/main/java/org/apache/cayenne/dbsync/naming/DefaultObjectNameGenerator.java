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
package org.apache.cayenne.dbsync.naming;

import java.util.Locale;
import java.util.Objects;

import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.DirectionalJoinVisitor;
import org.apache.cayenne.map.relationship.JoinVisitor;
import org.apache.cayenne.map.relationship.RelationshipDirection;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.apache.cayenne.util.Util;
import org.jvnet.inflector.Noun;

/**
 * The default strategy for converting DB-layer to Object-layer names.
 *
 * @since 4.0
 */
public class DefaultObjectNameGenerator implements ObjectNameGenerator {

    private DbEntityNameStemmer dbEntityNameStemmer;

    public DefaultObjectNameGenerator() {
        this.dbEntityNameStemmer = NoStemStemmer.getInstance();
    }

    public DefaultObjectNameGenerator(DbEntityNameStemmer dbEntityNameStemmer) {
        this.dbEntityNameStemmer = dbEntityNameStemmer;
    }

    @Override
    public String relationshipName(DbRelationship... relationshipChain) {

        if (relationshipChain == null || relationshipChain.length < 1) {
            throw new IllegalArgumentException("At least on relationship is expected: " + relationshipChain);
        }

        // ignore the name of DbRelationship itself (FWIW we may be generating a new name for it here)...
        // generate the name based on join semantics...

        String name = isToMany(relationshipChain)
                ? toManyRelationshipName(relationshipChain)
                : toOneRelationshipName(relationshipChain);

        return Util.underscoredToJava(name, false);
    }

    @Override
    public String relationshipName(DbJoin dbJoin, RelationshipDirection relationshipDirection) {
        if(dbJoin == null) {
            throw new IllegalArgumentException("At least on dbJoin is expected: " + dbJoin);
        }

        String name = isToMany(dbJoin, relationshipDirection)
                ? toManyRelationshipName(dbJoin, relationshipDirection)
                : toOneRelationshipName(dbJoin, relationshipDirection);

        return Util.underscoredToJava(name, false);
    }

    protected boolean isToMany(DbJoin dbJoin, RelationshipDirection direction) {
        ToManySemantics toManySemantics = dbJoin.getToManySemantics();
        if(direction == RelationshipDirection.LEFT) {
            return toManySemantics == ToManySemantics.ONE_TO_MANY ||
                    toManySemantics == ToManySemantics.MANY_TO_MANY;
        } else {
            return toManySemantics == ToManySemantics.MANY_TO_ONE ||
                    toManySemantics == ToManySemantics.MANY_TO_MANY;
        }
    }

    protected boolean isToMany(DbRelationship... relationshipChain) {

        for (DbRelationship r : relationshipChain) {
            if (r.isToMany()) {
                return true;
            }
        }

        return false;
    }

    protected String stemmed(String dbEntityName) {
        return dbEntityNameStemmer.stem(Objects.requireNonNull(dbEntityName));
    }

    protected String toManyRelationshipName(DbRelationship... relationshipChain) {

        DbRelationship last = relationshipChain[relationshipChain.length - 1];

        String baseName = stemmed(last.getTargetEntityName());

        try {
            // by default we use English rules here...
            return Noun.pluralOf(baseName.toLowerCase(), Locale.ENGLISH);
        } catch (Exception inflectorError) {
            //  seems that Inflector cannot be trusted. For instance, it
            // throws an exception when invoked for word "ADDRESS" (although
            // lower case works fine). To feel safe, we use superclass'
            // behavior if something's gone wrong
            return baseName;
        }
    }

    protected String toManyRelationshipName(DbJoin dbJoin, RelationshipDirection direction) {
        String[] entities = dbJoin.getDbEntities();
        String baseName = stemmed(entities[direction.getOppositeDirection().ordinal()]);

        try {
            // by default we use English rules here...
            return Noun.pluralOf(baseName.toLowerCase(), Locale.ENGLISH);
        } catch (Exception inflectorError) {
            //  seems that Inflector cannot be trusted. For instance, it
            // throws an exception when invoked for word "ADDRESS" (although
            // lower case works fine). To feel safe, we use superclass'
            // behavior if something's gone wrong
            return baseName;
        }
    }

    protected String toOneRelationshipName(DbRelationship... relationshipChain) {

        DbRelationship first = relationshipChain[0];
        DbRelationship last = relationshipChain[relationshipChain.length - 1];

        // TODO: multi-join relationships

        return first.accept(new DirectionalJoinVisitor<String>() {
            @Override
            public String visit(DbAttribute[] source, DbAttribute[] target) {
                return visit(source[0], target[0]);
            }

            @Override
            public String visit(DbAttribute source, DbAttribute target) {
                String fkColName = source.getName();
                if (fkColName == null) {
                    return stemmed(last.getTargetEntityName());
                } else if (fkColName.toUpperCase().endsWith("_ID") && fkColName.length() > 3) {
                    return fkColName.substring(0, fkColName.length() - 3);
                } else if (fkColName.toUpperCase().endsWith("ID") && fkColName.length() > 2) {
                    return fkColName.substring(0, fkColName.length() - 2);
                } else {
                    return stemmed(last.getTargetEntityName());
                }
            }
        });
    }

    protected String toOneRelationshipName(DbJoin dbJoin, RelationshipDirection direction) {
        return dbJoin.getDbJoinCondition().accept(new JoinVisitor<String>() {

            private String name(ColumnPair columnPair) {
                String fkColName = direction == RelationshipDirection.LEFT ?
                        columnPair.getLeft() : columnPair.getRight();
                String targetName = dbJoin.getDbEntities()[direction.getOppositeDirection().ordinal()];
                if (fkColName == null) {
                    return stemmed(targetName);
                } else if (fkColName.toUpperCase().endsWith("_ID") && fkColName.length() > 3) {
                    return fkColName.substring(0, fkColName.length() - 3);
                } else if (fkColName.toUpperCase().endsWith("ID") && fkColName.length() > 2) {
                    return fkColName.substring(0, fkColName.length() - 2);
                } else {
                    return stemmed(targetName);
                }
            }

            @Override
            public String visit(ColumnPair columnPair) {
                return name(columnPair);
            }

            @Override
            public String visit(ColumnPair[] columnPairs) {
                return visit(columnPairs[0]);
            }
        });
    }

    @Override
    public String objEntityName(DbEntity dbEntity) {
        String baseName = stemmed(dbEntity.getName());
        return Util.underscoredToJava(baseName, true);
    }

    @Override
    public String objAttributeName(DbAttribute attr) {
        return Util.underscoredToJava(attr.getName(), false);
    }
}
