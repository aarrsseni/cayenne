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
package org.apache.cayenne.project.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.DirectionalJoinVisitor;
import org.apache.cayenne.util.Util;
import org.apache.cayenne.validation.ValidationResult;

class DbRelationshipValidator extends ConfigurationNodeValidator {

    void validate(DbRelationship relationship, ValidationResult validationResult) {

        if (relationship.getTargetEntity() == null) {
            addFailure(
                    validationResult,
                    relationship,
                    "DbRelationship '%s' has no target entity",
                    toString(relationship));
        }  else {
            // validate joins
            relationship.accept(new DirectionalJoinVisitor<Void>() {

                private void validate(DbAttribute source, DbAttribute target) {
                    if (source == null && target == null) {
                        addFailure(
                                validationResult,
                                relationship,
                                "DbRelationship '%s' has a join with no source and target attributes selected",
                                DbRelationshipValidator.this.toString(relationship));
                    } else if (source == null) {
                        addFailure(
                                validationResult,
                                relationship,
                                "DbRelationship '%s' has a join with no source attribute selected",
                                DbRelationshipValidator.this.toString(relationship));
                    } else if (target == null) {
                        addFailure(
                                validationResult,
                                relationship,
                                "DbRelationship '%s' has a join with no target attribute selected",
                                DbRelationshipValidator.this.toString(relationship));
                    }
                }

                @Override
                public Void visit(DbAttribute[] source, DbAttribute[] target) {
                    int length = source.length;
                    for(int i = 0; i < length; i++) {
                        validate(source[i], target[i]);
                    }
                    return null;
                }

                @Override
                public Void visit(DbAttribute source, DbAttribute target) {
                    validate(source, target);
                    return null;
                }
            });
        }

        if(!relationship.isToPK()) {
            DbRelationship reverseRelationship = relationship.getReverseRelationship();
            if(reverseRelationship != null && !reverseRelationship.isToPK()) {
                addFailure(
                        validationResult,
                        relationship,
                        "DbRelationship '%s' has join not to PK. Cayenne doesn't allow this type of relationship",
                        toString(relationship));
            }
        }

        if (Util.isEmptyString(relationship.getName())) {
            addFailure(validationResult, relationship, "Unnamed DbRelationship");
        } else if (relationship.getSourceEntity().getAttribute(relationship.getName()) != null) {
            // check if there are attributes having the same name
            addFailure(
                    validationResult,
                    relationship,
                    "Name of DbRelationship '%s' conflicts with the name of one of DbAttributes in the same entity",
                    toString(relationship));
        } else {
            NameValidationHelper helper = NameValidationHelper.getInstance();
            String invalidChars = helper.invalidCharsInDbPathComponent(relationship.getName());
            if (invalidChars != null) {
                addFailure(
                        validationResult,
                        relationship,
                        "Name of DbRelationship '%s' contains invalid characters: %s",
                        toString(relationship),
                        invalidChars);
            }
        }

        checkForDuplicates(relationship, validationResult);
        checkOnGeneratedStrategyConflict(relationship, validationResult);
        checkToMany(relationship, validationResult);
    }

    private void checkToMany(DbRelationship relationship, ValidationResult validationResult) {
        if(relationship != null) {
            if(relationship.getReverseRelationship() != null
                    && relationship.isToMany() && relationship.getReverseRelationship().isToMany()) {
                addFailure(
                        validationResult,
                        relationship,
                        "Relationship '%s' and reverse '%s' are both toMany",
                        relationship.getName(), relationship.getReverseRelationship().getName());
            }
            checkTypesOfAttributesInRelationship(relationship, validationResult);
        }
    }

    private void checkTypesOfAttributesInRelationship(DbRelationship relationship, ValidationResult validationResult) {
        relationship.accept(new DirectionalJoinVisitor<Void>() {

            private void validate(DbAttribute source, DbAttribute target) {
                if (source != null && target != null
                        && source.getType() != target.getType()) {
                    addFailure(
                            validationResult,
                            relationship,
                            "Attributes '%s' and '%s' have different types in a relationship '%s'",
                            source.getName(), target.getName(), relationship.getName());
                }
            }

            @Override
            public Void visit(DbAttribute[] source, DbAttribute[] target) {
                int length = source.length;
                for(int i = 0; i < length; i++) {
                    validate(source[i], target[i]);
                }
                return null;
            }

            @Override
            public Void visit(DbAttribute source, DbAttribute target) {
                validate(source, target);
                return null;
            }
        });
    }

    private void checkOnGeneratedStrategyConflict(DbRelationship relationship, ValidationResult validationResult) {
        if (relationship.isToDependentPK()) {
            Collection<DbAttribute> attributes = relationship.getTargetEntity().getGeneratedAttributes();
            for (DbAttribute attribute : attributes) {
                if (attribute.isGenerated()) {
                    addFailure(
                            validationResult,
                            relationship,
                            "'To Dep Pk' incompatible with Database-Generated on '%s' relationship",
                            toString(relationship));
                }
            }
        }
    }

    /**
     * Per CAY-1813, make sure two (or more) DbRelationships do not map to the
     * same database path.
     */
    private void checkForDuplicates(DbRelationship relationship, ValidationResult validationResult) {
        if (relationship                       != null &&
            relationship.getName()             != null &&
            relationship.getTargetEntityName() != null) {

            String dbRelationshipPath =
                       relationship.getTargetEntityName() +
                       "." +
                       getJoins(relationship);

            DbEntity entity = relationship.getSourceEntity();

            for (DbRelationship comparisonRelationship : entity.getRelationships()) {
                if (relationship != comparisonRelationship) {
                    String comparisonDbRelationshipPath =
                               comparisonRelationship.getTargetEntityName() +
                               "." +
                               getJoins(comparisonRelationship);

                    if (dbRelationshipPath.equals(comparisonDbRelationshipPath)) {
                        addFailure(validationResult,
                                   relationship,
                                   "DbEntity '%s' contains a " +
                                           "duplicate DbRelationship mapping ('%s' -> '%s')",
                                   entity.getName(),
                                   relationship.getName(),
                                   dbRelationshipPath);
                        return; // Duplicate found, stop.
                    }
                }
            }
        }
    }

    private String getJoins(DbRelationship relationship) {
        List<String> joins = relationship.accept(new DirectionalJoinVisitor<List<String>>() {
            @Override
            public List<String> visit(DbAttribute[] source, DbAttribute[] target) {
                int length = source.length;
                List<String> joins = new ArrayList<>(length);
                for(int i = 0; i < length; i++) {
                    joins.add("[source=" + source[i].getName() + ",target=" + target[i].getName() + "]");
                }
                return joins;
            }

            @Override
            public List<String> visit(DbAttribute source, DbAttribute target) {
                return Collections
                        .singletonList("[source=" + source.getName() + ",target=" + target.getName() + "]");
            }
        });

        Collections.sort(joins);
        return Util.join(joins, ",");
    }

    private String toString(DbRelationship relationship) {
        if (relationship.getSourceEntity() == null) {
            return "[null source entity]." + relationship.getName();
        }

        return relationship.getSourceEntity().getName() + "." + relationship.getName();
    }

}
