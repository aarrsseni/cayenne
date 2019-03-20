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
package org.apache.cayenne.dbsync.merge.builders;

import java.util.Collections;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;

/**
 * @since 4.0.
 */
public class DataMapBuilder extends DefaultBuilder<DataMap> {

    public DataMapBuilder() {
        this(new DataMap());
    }

    public DataMapBuilder(DataMap dataMap) {
        super(dataMap);
    }

    public DataMapBuilder with(DbEntity ... entities) {
        for (DbEntity entity : entities) {
            obj.addDbEntity(entity);
        }

        return this;
    }

    public DataMapBuilder with(DbEntityBuilder ... entities) {
        for (DbEntityBuilder entity : entities) {
            obj.addDbEntity(entity.build());
        }

        return this;
    }

    public DataMapBuilder withDbEntities(int count) {
        for (int i = 0; i < count; i++) {
            obj.addDbEntity(ObjectMother.dbEntity().random());
        }

        return this;
    }

    public DataMapBuilder with(ObjEntity... entities) {
        for (ObjEntity entity : entities) {
            obj.addObjEntity(entity);
        }

        return this;
    }

    public DataMapBuilder with(ObjEntityBuilder ... entities) {
        for (ObjEntityBuilder entity : entities) {
            obj.addObjEntity(entity.build());
        }

        return this;
    }

    public DataMapBuilder withObjEntities(int count) {
        for (int i = 0; i < count; i++) {
            obj.addObjEntity(ObjectMother.objEntity().random());
        }

        return this;
    }

    public DataMapBuilder join(String from, String to) {
        return join(null, null, from, to);
    }

    public DataMapBuilder join(String name, String reverseName, String from, String to) {
        String[] fromSplit = from.split("\\.");
        DbEntity fromEntity = obj.getDbEntity(fromSplit[0]);
        if (fromEntity == null) {
            throw new IllegalArgumentException("Entity '" + fromSplit[0] + "' is undefined");
        }

        String[] toSplit = to.split("\\.");

        DbJoin dbJoin = new DbJoinBuilder()
                .entities(new String[]{fromEntity.getName(), toSplit[0]})
                .names(new String[]{name, reverseName})
                .toManySemantics(ToManySemantics.ONE_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .condition(new SinglePairCondition(new ColumnPair(fromSplit[1], toSplit[1])))
                .dataMap(obj)
                .build();
        obj.addJoin(dbJoin);

        return this;
    }

    public DataMap build() {
        if (obj.getNamespace() == null) {
            obj.setNamespace(new EntityResolver(Collections.singleton(obj)));
        }
        obj.getDbJoinList().forEach(dbJoin -> dbJoin.compile(obj));
        return obj;
    }

    @Override
    public DataMap random() {
        if (dataFactory.chance(90)) {
            withDbEntities(dataFactory.getNumberUpTo(10));
        }


        return build();
    }
}
