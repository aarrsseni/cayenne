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

package org.apache.cayenne.dbsync.merge.token.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.dbsync.merge.context.MergerContext;
import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactory;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.util.Util;

public class AddRelationshipToModel extends AbstractToModelToken.Entity {

    public static final String COMMA_SEPARATOR = ", ";
    public static final int COMMA_SEPARATOR_LENGTH = COMMA_SEPARATOR.length();

    private DbRelationship relationship;

    public AddRelationshipToModel(DbEntity entity, DbRelationship relationship) {
        super("Add Relationship", 125, entity);
        this.relationship = relationship;
    }

    public static String getTokenValue(DbRelationship rel) {
        String attributes = "";
        List<String> joinList = new ArrayList<>();
        rel.getJoin().accept(join -> {
            joinList.add(join.getTargetName());
            return true;
        });

        if(joinList.isEmpty()) {
            attributes = "{}";
        } else if(joinList.size() == 1) {
            attributes = joinList.get(0);
        } else {
            attributes = "{" + Util.join(joinList, COMMA_SEPARATOR) + "}";
        }
        return rel.getName() + " " + rel.getSourceEntity().getName() + "->" + rel.getTargetEntityName() + "." + attributes;
    }

    @Override
    public MergerToken createReverse(MergerTokenFactory factory) {
        return factory.createDropRelationshipToDb(getEntity(), relationship);
    }

    @Override
    public void execute(MergerContext context) {
        // Set name to relationship if it was created without it, e.g. in createReverse() action
        if(relationship.getName() == null) {
            relationship.setName(context.getNameGenerator().relationshipName(relationship));
        }

        getEntity().addRelationship(relationship);
        for (ObjEntity e : getMappedObjEntities()) {
            context.getEntityMergeSupport().synchronizeOnDbRelationshipAdded(e, relationship);
        }

        context.getDelegate().dbRelationshipAdded(relationship);
    }

    @Override
    public String getTokenValue() {
        return getTokenValue(relationship);
    }
}
