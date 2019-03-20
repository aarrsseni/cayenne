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
import java.util.Arrays;

import org.apache.cayenne.dbsync.filter.NamePatternMatcher;
import org.apache.cayenne.dbsync.merge.context.EntityMergeSupport;
import org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator;
import org.apache.cayenne.dbsync.naming.NoStemStemmer;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DeleteRule;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.junit.Test;

import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EntityMergeSupportIT extends MergeCase {

	@Test
	public void testMerging() {
		DbEntity dbEntity1 = new DbEntity("NEW_TABLE");

		DbAttribute e1col1 = new DbAttribute("ID", Types.INTEGER, dbEntity1);
		e1col1.setMandatory(true);
		e1col1.setPrimaryKey(true);
		dbEntity1.addAttribute(e1col1);

		DbAttribute e1col2 = new DbAttribute("NAME", Types.VARCHAR, dbEntity1);
		e1col2.setMaxLength(10);
		e1col2.setMandatory(false);
		dbEntity1.addAttribute(e1col2);

		map.addDbEntity(dbEntity1);

		DbEntity dbEntity2 = new DbEntity("NEW_TABLE2");
		DbAttribute e2col1 = new DbAttribute("ID", Types.INTEGER, dbEntity2);
		e2col1.setMandatory(true);
		e2col1.setPrimaryKey(true);
		dbEntity2.addAttribute(e2col1);
		DbAttribute e2col2 = new DbAttribute("FK", Types.INTEGER, dbEntity2);
		dbEntity2.addAttribute(e2col2);

		map.addDbEntity(dbEntity2);

		// create db relationships
		DbJoin dbJoin1 = new DbJoinBuilder()
				.entities(new String[]{dbEntity1.getName(), dbEntity2.getName()})
				.names(new String[]{"rel1To2", "rel2To1"})
				.toManySemantics(ToManySemantics.ONE_TO_MANY)
				.toDepPkSemantics(ToDependentPkSemantics.NONE)
				.condition(new SinglePairCondition(new ColumnPair(e1col1.getName(), e2col2.getName())))
				.dataMap(map)
				.build();
		dbJoin1.compile(map);

		DbRelationship rel1To2 = dbJoin1.getRelationhsip();
		DbRelationship rel2To1 = rel1To2.getReverseRelationship();

		assertSame(rel1To2, rel2To1.getReverseRelationship());
		assertSame(rel2To1, rel1To2.getReverseRelationship());

		ObjEntity objEntity1 = new ObjEntity("NewTable");
		objEntity1.setDbEntity(dbEntity1);
		map.addObjEntity(objEntity1);

		ObjEntity objEntity2 = new ObjEntity("NewTable2");
		objEntity2.setDbEntity(dbEntity2);
		map.addObjEntity(objEntity2);

		EntityMergeSupport entityMergeSupport = new EntityMergeSupport(
				new DefaultObjectNameGenerator(NoStemStemmer.getInstance()),
				NamePatternMatcher.EXCLUDE_ALL,
				true,
				true,
				false);
		assertTrue(entityMergeSupport.synchronizeWithDbEntities(Arrays.asList(objEntity1, objEntity2)));
		assertNotNull(objEntity1.getAttribute("name"));
		assertNotNull(objEntity1.getRelationship("newTable2s"));
		assertNotNull(objEntity2.getRelationship("newTable"));

		assertEquals(objEntity1.getRelationship("newTable2s").getDeleteRule(), DeleteRule.DEFAULT_DELETE_RULE_TO_MANY);
		assertEquals(objEntity2.getRelationship("newTable").getDeleteRule(), DeleteRule.DEFAULT_DELETE_RULE_TO_ONE);

		map.removeObjEntity(objEntity2.getName());
		map.removeObjEntity(objEntity1.getName());
		map.removeDbEntity(dbEntity2.getName());
		map.removeDbEntity(dbEntity1.getName());
	}
}
