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

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.RelationshipDirection;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultObjectNameGeneratorTest {

    private DefaultObjectNameGenerator generator = new DefaultObjectNameGenerator(NoStemStemmer.getInstance());

    private DbRelationship makeRelationship(String srcEntity, String srcKey, String targetEntity, String targetKey,
                                            boolean toMany) {
        DataMap dataMap = new DataMap();
        DbEntity e1 = new DbEntity(srcEntity);
        DbAttribute a1 = new DbAttribute(srcKey);
        e1.addAttribute(a1);
        DbAttribute a2 = new DbAttribute(targetKey);
        if(!srcEntity.equals(targetEntity)) {
            DbEntity e2 = new DbEntity(targetEntity);
            e2.addAttribute(a2);
            dataMap.addDbEntity(e2);
        } else {
            e1.addAttribute(a2);
        }

        dataMap.addDbEntity(e1);

        DbJoin dbJoin = new DbJoinBuilder()
                .entities(new String[]{e1.getName(), targetEntity})
                .names(new String[]{"X", null})
                .toManySemantics(toMany ? ToManySemantics.ONE_TO_MANY :
                        ToManySemantics.MANY_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .condition(new SinglePairCondition(new ColumnPair(srcKey, targetKey)))
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);
        return dbJoin.getRelationhsip();
    }

    @Test
    public void testRelationshipName_LowerCase_Underscores() {

        DbRelationship r1 = makeRelationship("painting", "artist_id", "artist", "artist_id", false);
        assertEquals("artist", generator.relationshipName(r1));

        DbRelationship r2 = makeRelationship("artist", "artist_id", "painting", "artist_id", true);
        assertEquals("paintings", generator.relationshipName(r2));

        DbRelationship r3 = makeRelationship("person", "mother_id", "person", "person_id", false);
        assertEquals("mother", generator.relationshipName(r3));

        DbRelationship r4 = makeRelationship("person", "person_id", "person", "mother_id", true);
        assertEquals("people", generator.relationshipName(r4));

        DbRelationship r5 = makeRelationship("person", "shipping_address_id", "address", "id", false);
        assertEquals("shippingAddress", generator.relationshipName(r5));

        DbRelationship r6 = makeRelationship("person", "id", "address", "person_id", true);
        assertEquals("addresses", generator.relationshipName(r6));
    }

    @Test
    public void testDbJoinName_LowerCase_Inderscores() {
        DbJoin dbJoin = new DbJoinBuilder()
                .names(new String[]{"temp", "temp1"})
                .entities(new String[]{"painting", "artist"})
                .toManySemantics(ToManySemantics.MANY_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .condition(new SinglePairCondition(new ColumnPair("artist_id", "artist_id")))
                .dataMap(new DataMap())
                .build();
        assertEquals("artist", generator.relationshipName(dbJoin, RelationshipDirection.LEFT));
        assertEquals("paintings", generator.relationshipName(dbJoin, RelationshipDirection.RIGHT));
    }

    @Test
    public void testDbJoinName_UpperCase_Underscores() {
        DbJoin dbJoin = new DbJoinBuilder()
                .names(new String[]{"temp", "temp1"})
                .entities(new String[]{"PAINTING", "ARTIST"})
                .toManySemantics(ToManySemantics.MANY_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .condition(new SinglePairCondition(new ColumnPair("ARTIST_ID", "ARTIST_ID")))
                .dataMap(new DataMap())
                .build();
        assertEquals("artist", generator.relationshipName(dbJoin, RelationshipDirection.LEFT));
        assertEquals("paintings", generator.relationshipName(dbJoin, RelationshipDirection.RIGHT));
    }

    @Test
    public void testRelationshipName_UpperCase_Underscores() {

        DbRelationship r1 = makeRelationship("PAINTING", "ARTIST_ID", "ARTIST", "ARTIST_ID", false);
        assertEquals("artist", generator.relationshipName(r1));

        DbRelationship r2 = makeRelationship("ARTIST", "ARTIST_ID", "PAINTING", "ARTIST_ID", true);
        assertEquals("paintings", generator.relationshipName(r2));

        DbRelationship r3 = makeRelationship("PERSON", "MOTHER_ID", "PERSON", "PERSON_ID", false);
        assertEquals("mother", generator.relationshipName(r3));

        DbRelationship r4 = makeRelationship("PERSON", "PERSON_ID", "PERSON", "MOTHER_ID", true);
        assertEquals("people", generator.relationshipName(r4));

        DbRelationship r5 = makeRelationship("PERSON", "SHIPPING_ADDRESS_ID", "ADDRESS", "ID", false);
        assertEquals("shippingAddress", generator.relationshipName(r5));

        DbRelationship r6 = makeRelationship("PERSON", "ID", "ADDRESS", "PERSON_ID", true);
        assertEquals("addresses", generator.relationshipName(r6));
    }

    @Test
    public void testObjEntityName() {
        assertEquals("Artist", generator.objEntityName(new DbEntity("ARTIST")));
        assertEquals("ArtistWork", generator.objEntityName(new DbEntity("ARTIST_WORK")));
    }

    @Test
    public void testObjAttributeName() {
        assertEquals("name", generator.objAttributeName(new DbAttribute("NAME")));
        assertEquals("artistName", generator.objAttributeName(new DbAttribute("ARTIST_NAME")));
    }
}
