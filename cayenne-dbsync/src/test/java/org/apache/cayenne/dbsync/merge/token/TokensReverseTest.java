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

package org.apache.cayenne.dbsync.merge.token;

import java.util.Collections;

import org.apache.cayenne.dbsync.merge.factory.HSQLMergerTokenFactory;
import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactory;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.cayenne.dbsync.merge.builders.ObjectMother.dbAttr;
import static org.apache.cayenne.dbsync.merge.builders.ObjectMother.dbEntity;

/**
 * @since 4.0.
 */
public class TokensReverseTest {

    @Test
    public void testReverses() {
        DataMap dataMap = new DataMap();

        DbAttribute attr = dbAttr().build();
        DbEntity entity = dbEntity().attributes(attr).build();

        DbAttribute targetAttr = dbAttr().build();
        DbEntity targetEntity = dbEntity().attributes(targetAttr).build();

        dataMap.addDbEntity(entity);
        dataMap.addDbEntity(targetEntity);

        DbJoin dbJoin = new DbJoinBuilder()
                .entities(new String[]{entity.getName(), targetEntity.getName()})
                .names(new String[]{"Test", null})
                .toManySemantics(ToManySemantics.ONE_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .condition(new SinglePairCondition(new ColumnPair(attr.getName(), targetAttr.getName())))
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);

        testOneToOneReverse(factory().createAddColumnToDb(entity, attr));
        testOneToOneReverse(factory().createAddColumnToModel(entity, attr));
        testOneToOneReverse(factory().createDropColumnToDb(entity, attr));
        testOneToOneReverse(factory().createDropColumnToModel(entity, attr));

        testOneToOneReverse(factory().createAddJoinToDb(dbJoin));
        testOneToOneReverse(factory().createAddJoinToModel(dbJoin));
        testOneToOneReverse(factory().createDropJoinToDb(dbJoin));
        testOneToOneReverse(factory().createAddJoinToModel(dbJoin));

        testOneToOneReverse(factory().createCreateTableToDb(entity));
        testOneToOneReverse(factory().createCreateTableToModel(entity));
        testOneToOneReverse(factory().createDropTableToDb(entity));
        testOneToOneReverse(factory().createDropTableToModel(entity));

        testOneToOneReverse(factory().createSetAllowNullToDb(entity, attr));
        testOneToOneReverse(factory().createSetAllowNullToModel(entity, attr));
        testOneToOneReverse(factory().createSetNotNullToDb(entity, attr));
        testOneToOneReverse(factory().createSetNotNullToModel(entity, attr));

        DbAttribute attr2 = dbAttr().build();
        testOneToOneReverse(factory().createSetColumnTypeToDb(entity, attr, attr2));
        testOneToOneReverse(factory().createSetColumnTypeToModel(entity, attr, attr2));

        testOneToOneReverse(factory().createSetPrimaryKeyToDb(entity, Collections.singleton(attr), Collections.singleton(attr2), "PK"));
        testOneToOneReverse(factory().createSetPrimaryKeyToModel(entity, Collections.singleton(attr), Collections.singleton(attr2), "PK"));

        testOneToOneReverse(factory().createSetValueForNullToDb(entity, attr, new DefaultValueForNullProvider()));
    }

    private void testOneToOneReverse(MergerToken token) {
        MergerToken token2 = token.createReverse(factory()).createReverse(factory());

        Assert.assertEquals(token.getTokenName(), token2.getTokenName());
        Assert.assertEquals(token.getTokenValue(), token2.getTokenValue());
        Assert.assertEquals(token.getDirection(), token2.getDirection());
    }

    private MergerTokenFactory factory() {
        return new HSQLMergerTokenFactory();
    }
}
