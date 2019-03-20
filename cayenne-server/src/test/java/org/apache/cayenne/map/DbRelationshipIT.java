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

package org.apache.cayenne.map;

import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.apache.cayenne.unit.di.server.CayenneProjects;
import org.apache.cayenne.unit.di.server.ServerCase;
import org.apache.cayenne.unit.di.server.UseServerRuntime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

@UseServerRuntime(CayenneProjects.TESTMAP_PROJECT)
public class DbRelationshipIT extends ServerCase {

    @Inject
    private ServerRuntime runtime;

    protected DbEntity artistEnt;
    protected DbEntity paintingEnt;
    protected DbEntity galleryEnt;

    @Before
    public void setUp() throws Exception {
        artistEnt = runtime.getDataDomain().getEntityResolver().getDbEntity("ARTIST");
        paintingEnt = runtime.getDataDomain().getEntityResolver().getDbEntity("PAINTING");
        galleryEnt = runtime.getDataDomain().getEntityResolver().getDbEntity("GALLERY");
    }

    @Test
    public void testSrcFkSnapshotWithTargetSnapshot() throws Exception {
        Map<String, Object> map = new HashMap<>();
        Integer id = new Integer(44);
        map.put("GALLERY_ID", id);

        DbRelationship dbRel = galleryEnt.getRelationship("paintingArray");
        Map<String, Object> targetMap = dbRel.getReverseRelationship().srcFkSnapshotWithTargetSnapshot(map);
        assertEquals(id, targetMap.get("GALLERY_ID"));
    }

    @Test
    public void testGetReverseRelationship1() throws Exception {
        // start with "to many"
        DbRelationship r1 = artistEnt.getRelationship("paintingArray");
        DbRelationship r2 = r1.getReverseRelationship();

        assertNotNull(r2);
        assertSame(paintingEnt.getRelationship("toArtist"), r2);
    }

    @Test
    public void testGetReverseRelationship2() throws Exception {
        // start with "to one"
        DbRelationship r1 = paintingEnt.getRelationship("toArtist");
        DbRelationship r2 = r1.getReverseRelationship();

        assertNotNull(r2);
        assertSame(artistEnt.getRelationship("paintingArray"), r2);
    }

    @Test
    public void testGetReverseRelationshipToSelf() {

        // assemble mockup entity
        DataMap namespace = new DataMap();
        DbEntity e = new DbEntity("test");
        DbAttribute a1 = new DbAttribute("a1");
        DbAttribute a2 = new DbAttribute("a2");
        e.addAttribute(a1);
        e.addAttribute(a2);
        namespace.addDbEntity(e);
        DbJoin dbJoin1 = new DbJoinBuilder()
                .entities(new String[]{e.getName(), e.getName()})
                .names(new String[]{"rforward", null})
                .toManySemantics(ToManySemantics.ONE_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .condition(new SinglePairCondition(new ColumnPair("a1", "a2")))
                .dataMap(namespace)
                .build();
        dbJoin1.compile(namespace);
        DbRelationship rforward = dbJoin1.getRelationhsip();

        assertNotNull(rforward.getReverseRelationship());

        DbJoin dbJoin2 = new DbJoinBuilder()
                .entities(new String[]{e.getName(), e.getName()})
                .names(new String[]{"rback", null})
                .toManySemantics(ToManySemantics.ONE_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .condition(new SinglePairCondition(new ColumnPair("a2", "a1")))
                .dataMap(namespace)
                .build();
        dbJoin2.compile(namespace);
        DbRelationship rback = dbJoin2.getRelationhsip();

        assertNotNull(rback.getReverseRelationship());

        assertNotSame(rback, rforward.getReverseRelationship());
        assertNotSame(rforward, rback.getReverseRelationship());
    }
}
