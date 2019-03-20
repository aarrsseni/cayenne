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
package org.apache.cayenne.dbsync.merge;

import java.sql.Types;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MergerFactoryIT extends MergeCase {

    @Inject
    private DataContext context;

    @Test
    public void testAddAndDropColumnToDb() throws Exception {
        DbEntity dbEntity = map.getDbEntity("PAINTING");
        assertNotNull(dbEntity);

        // create and add new column to model and db
        DbAttribute column = new DbAttribute("NEWCOL1", Types.VARCHAR, dbEntity);

        column.setMandatory(false);
        column.setMaxLength(10);
        dbEntity.addAttribute(column);
        assertTokensAndExecute(1, 0);

        // try merge once more to check that is was merged
        assertTokensAndExecute(0, 0);

        // remove it from model and db
        dbEntity.removeAttribute(column.getName());
        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);
    }

    @Test
    public void testChangeVarcharSizeToDb() throws Exception {
        DbEntity dbEntity = map.getDbEntity("PAINTING");
        assertNotNull(dbEntity);

        // create and add new column to model and db
        DbAttribute column = new DbAttribute("NEWCOL2", Types.VARCHAR, dbEntity);

        column.setMandatory(false);
        column.setMaxLength(10);
        dbEntity.addAttribute(column);
        assertTokensAndExecute(1, 0);

        // check that is was merged
        assertTokensAndExecute(0, 0);

        // change size
        column.setMaxLength(20);

        // merge to db
        assertTokensAndExecute(1, 0);

        // check that is was merged
        assertTokensAndExecute(0, 0);

        // clean up
        dbEntity.removeAttribute(column.getName());
        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);
    }

    @Test
    public void testMultipleTokensToDb() throws Exception {
        DbEntity dbEntity = map.getDbEntity("PAINTING");
        assertNotNull(dbEntity);

        DbAttribute column1 = new DbAttribute("NEWCOL3", Types.VARCHAR, dbEntity);
        column1.setMandatory(false);
        column1.setMaxLength(10);
        dbEntity.addAttribute(column1);
        DbAttribute column2 = new DbAttribute("NEWCOL4", Types.VARCHAR, dbEntity);
        column2.setMandatory(false);
        column2.setMaxLength(10);
        dbEntity.addAttribute(column2);

        assertTokensAndExecute(2, 0);

        // check that is was merged
        assertTokensAndExecute(0, 0);

        // change size
        column1.setMaxLength(20);
        column2.setMaxLength(30);

        // merge to db
        assertTokensAndExecute(2, 0);

        // check that is was merged
        assertTokensAndExecute(0, 0);

        // clean up
        dbEntity.removeAttribute(column1.getName());
        dbEntity.removeAttribute(column2.getName());
        assertTokensAndExecute(2, 0);
        assertTokensAndExecute(0, 0);
    }

    @Test
    public void testAddTableToDb() throws Exception {
        dropTableIfPresent("NEW_TABLE");

        assertTokensAndExecute(0, 0);

        DbEntity dbEntity = new DbEntity("NEW_TABLE");

        DbAttribute column1 = new DbAttribute("ID", Types.INTEGER, dbEntity);
        column1.setMandatory(true);
        column1.setPrimaryKey(true);
        dbEntity.addAttribute(column1);

        DbAttribute column2 = new DbAttribute("NAME", Types.VARCHAR, dbEntity);
        column2.setMaxLength(10);
        column2.setMandatory(false);
        dbEntity.addAttribute(column2);

        map.addDbEntity(dbEntity);

        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);

        ObjEntity objEntity = new ObjEntity("NewTable");
        objEntity.setDbEntity(dbEntity);
        ObjAttribute oatr1 = new ObjAttribute("name");
        oatr1.setDbAttributePath(column2.getName());
        oatr1.setType("java.lang.String");
        objEntity.addAttribute(oatr1);
        map.addObjEntity(objEntity);

        for (int i = 0; i < 5; i++) {
            CayenneDataObject dao = (CayenneDataObject) context.newObject(objEntity
                    .getName());
            dao.writeProperty(oatr1.getName(), "test " + i);
        }
        context.commitChanges();

        // clear up
        map.removeObjEntity(objEntity.getName(), true);
        map.removeDbEntity(dbEntity.getName(), true);
        resolver.refreshMappingCache();
        assertNull(map.getObjEntity(objEntity.getName()));
        assertNull(map.getDbEntity(dbEntity.getName()));
        assertFalse(map.getDbEntities().contains(dbEntity));

        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);
    }

    @Test
    public void testAddForeignKeyWithTable() throws Exception {
        dropTableIfPresent("NEW_TABLE");

        assertTokensAndExecute(0, 0);

        DbEntity dbEntity = new DbEntity("NEW_TABLE");

        attr(dbEntity, "ID", Types.INTEGER, true, true);
        attr(dbEntity, "NAME", Types.VARCHAR, false, false).setMaxLength(10);
        attr(dbEntity, "ARTIST_ID", Types.BIGINT, false, false);

        map.addDbEntity(dbEntity);

        DbEntity artistDbEntity = map.getDbEntity("ARTIST");
        assertNotNull(artistDbEntity);

        DbJoin dbJoin = new DbJoinBuilder()
                .entities(new String[]{dbEntity.getName(), artistDbEntity.getName()})
                .names(new String[]{"toArtistR1", "toNewTableR2"})
                .toManySemantics(ToManySemantics.ONE_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .condition(new SinglePairCondition(new ColumnPair("ARTIST_ID", "ARTIST_ID")))
                .dataMap(map)
                .build();
        map.addJoin(dbJoin);
        dbJoin.compile(map);

        // relation from new_table to artist
        DbRelationship r1 = dbJoin.getRelationhsip();

        // relation from artist to new_table
        DbRelationship r2 = r1.getReverseRelationship();

        assertTokensAndExecute(2, 0);
        assertTokensAndExecute(0, 0);

        // remove relationships
        map.getDbJoinList().remove(dbJoin);
        resolver.refreshMappingCache();
        /*
         * Db -Rel 'toArtistR1' - NEW_TABLE 1 -> 1 ARTIST"
r2 =     * Db -Rel 'toNewTableR2' - ARTIST 1 -> * NEW_TABLE" -- Not generated any more
         * */
        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);

        // clear up
        // map.removeObjEntity(objEntity.getName(), true);
        map.removeDbEntity(dbEntity.getName(), true);
        resolver.refreshMappingCache();
        // assertNull(map.getObjEntity(objEntity.getName()));
        assertNull(map.getDbEntity(dbEntity.getName()));
        assertFalse(map.getDbEntities().contains(dbEntity));

        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);
    }

    @Test
    public void testAddForeignKeyAfterTable() throws Exception {
        dropTableIfPresent("NEW_TABLE");

        assertTokensAndExecute(0, 0);

        DbEntity dbEntity = new DbEntity("NEW_TABLE");
        attr(dbEntity, "ID", Types.INTEGER, true, true);
        attr(dbEntity, "NAME", Types.VARCHAR, false, false).setMaxLength(10);
        attr(dbEntity, "ARTIST_ID", Types.BIGINT, false, false);

        map.addDbEntity(dbEntity);

        DbEntity artistDbEntity = map.getDbEntity("ARTIST");
        assertNotNull(artistDbEntity);

        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);

        DbJoin dbJoin = new DbJoinBuilder()
                .entities(new String[]{dbEntity.getName(), artistDbEntity.getName()})
                .names(new String[]{"toArtistR1", "toNewTableR2"})
                .toManySemantics(ToManySemantics.ONE_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .condition(new SinglePairCondition(new ColumnPair("ARTIST_ID", "ARTIST_ID")))
                .dataMap(map)
                .build();
        map.addJoin(dbJoin);
        dbJoin.compile(map);

        // relation from new_table to artist
        DbRelationship r1 = dbJoin.getRelationhsip();

        // relation from artist to new_table
        DbRelationship r2 = r1.getReverseRelationship();

        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);

        // remove relationships
        map.getDbJoinList().remove(dbJoin);
        resolver.refreshMappingCache();
        /*
        * Add Relationship ARTIST->NEW_TABLE To Model -- Not generated any more
        * Drop Relationship NEW_TABLE->ARTIST To DB
        * */
        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);

        // clear up
        // map.removeObjEntity(objEntity.getName(), true);
        map.removeDbEntity(dbEntity.getName(), true);
        resolver.refreshMappingCache();
        // assertNull(map.getObjEntity(objEntity.getName()));
        assertNull(map.getDbEntity(dbEntity.getName()));
        assertFalse(map.getDbEntities().contains(dbEntity));

        assertTokensAndExecute(1, 0);
        assertTokensAndExecute(0, 0);
    }

    private static DbAttribute attr(DbEntity dbEntity, String name, int type, boolean mandatory, boolean primaryKey) {
        DbAttribute column1 = new DbAttribute(name, type, dbEntity);
        column1.setMandatory(mandatory);
        column1.setPrimaryKey(primaryKey);

        dbEntity.addAttribute(column1);
        return column1;
    }
}
