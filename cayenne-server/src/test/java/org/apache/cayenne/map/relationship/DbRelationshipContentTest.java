package org.apache.cayenne.map.relationship;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DbRelationshipContentTest {

    private DataMap dataMap;
    private DbEntity entity1;
    private DbEntity entity2;
    private DbJoinCondition dbJoinCondition;

    @Before
    public void prepareJoin() {
        dataMap = new DataMap();

        entity1 = new DbEntity("entity1");
        DbAttribute attr1 = new DbAttribute("attr1");
        attr1.setEntity(entity1);
        entity1.addAttribute(attr1);

        dataMap.addDbEntity(entity1);

        entity2 = new DbEntity("entity2");
        DbAttribute attr2 = new DbAttribute("attr2");
        attr2.setEntity(entity2);
        attr2.setPrimaryKey(true);
        attr2.setMandatory(true);

        dataMap.addDbEntity(entity2);

        dbJoinCondition = new SinglePairCondition(new ColumnPair("attr1", "attr2"));
    }

    @Test
    public void testNames() {
        DbJoin dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test1", "test2"})
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .toManySemantics(ToManySemantics.ONE_TO_ONE)
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);

        DbRelationship relationship = dbJoin.getRelationhsip();
        DbRelationship reverseRelationship = relationship.getReverseRelationship();
        assertNotNull(relationship);
        assertNotNull(reverseRelationship);

        assertEquals(entity1, relationship.getSourceEntity());
        assertEquals(entity2, reverseRelationship.getSourceEntity());

        assertEquals("test1", relationship.getName());
        assertEquals("test2", reverseRelationship.getName());

        assertFalse(relationship.isToMany());
        assertFalse(reverseRelationship.isToMany());
        assertFalse(relationship.isToDependentPK());
        assertFalse(reverseRelationship.isToDependentPK());
    }

    @Test
    public void testOneToMany() {
        DbJoin dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test1", "test2"})
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .toManySemantics(ToManySemantics.ONE_TO_MANY)
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);

        DbRelationship relationship = dbJoin.getRelationhsip();
        DbRelationship reverseRelationship = relationship.getReverseRelationship();
        assertNotNull(relationship);
        assertNotNull(reverseRelationship);

        assertTrue(relationship.isToMany());
        assertFalse(reverseRelationship.isToMany());
    }

    @Test
    public void testManyToOne() {
        DbJoin dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test1", "test2"})
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .toManySemantics(ToManySemantics.MANY_TO_ONE)
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);

        DbRelationship relationship = dbJoin.getRelationhsip();
        DbRelationship reverseRelationship = relationship.getReverseRelationship();
        assertNotNull(relationship);
        assertNotNull(reverseRelationship);

        assertFalse(relationship.isToMany());
        assertTrue(reverseRelationship.isToMany());
    }

    @Test
    public void testManyToMany() {
        DbJoin dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test1", "test2"})
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .toManySemantics(ToManySemantics.MANY_TO_MANY)
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);

        DbRelationship relationship = dbJoin.getRelationhsip();
        DbRelationship reverseRelationship = relationship.getReverseRelationship();
        assertNotNull(relationship);
        assertNotNull(reverseRelationship);

        assertTrue(relationship.isToMany());
        assertTrue(reverseRelationship.isToMany());
    }

    @Test
    public void testToDepPkLeft() {
        DbJoin dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test1", "test2"})
                .toDepPkSemantics(ToDependentPkSemantics.LEFT)
                .toManySemantics(ToManySemantics.MANY_TO_MANY)
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);

        DbRelationship relationship = dbJoin.getRelationhsip();
        DbRelationship reverseRelationship = relationship.getReverseRelationship();
        assertNotNull(relationship);
        assertNotNull(reverseRelationship);

        assertTrue(relationship.isToDependentPK());
        assertFalse(reverseRelationship.isToDependentPK());
    }

    @Test
    public void testToDepPkRight() {
        DbJoin dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test1", "test2"})
                .toDepPkSemantics(ToDependentPkSemantics.RIGHT)
                .toManySemantics(ToManySemantics.MANY_TO_MANY)
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);

        DbRelationship relationship = dbJoin.getRelationhsip();
        DbRelationship reverseRelationship = relationship.getReverseRelationship();
        assertNotNull(relationship);
        assertNotNull(reverseRelationship);

        assertFalse(relationship.isToDependentPK());
        assertTrue(reverseRelationship.isToDependentPK());
    }

    @Test
    public void testNullName() {
        DbJoin dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test1", null})
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .toManySemantics(ToManySemantics.MANY_TO_MANY)
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);

        DbRelationship relationship = dbJoin.getRelationhsip();
        DbRelationship reverseRelationship = relationship.getReverseRelationship();
        assertNotNull(relationship);
        assertNotNull(reverseRelationship);

        assertEquals("test1", relationship.getName());
        assertTrue(reverseRelationship.getName().startsWith("runtime"));
        assertTrue(reverseRelationship.isRuntime());
    }

    @Test(expected = CayenneRuntimeException.class)
    public void testMissedArguments() {
        DbJoin dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test1", null})
                .toManySemantics(ToManySemantics.MANY_TO_MANY)
                .build();
        dbJoin.compile(dataMap);
    }
}
