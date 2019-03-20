package org.apache.cayenne.map.relationship;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.configuration.EmptyConfigurationNodeVisitor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.util.XMLEncoder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DbRelationshipTest {

    private DbRelationship[] relationships;
    private DbJoin dbJoin;

    @Before
    public void prepareJoin() {
        this.relationships = new DbRelationship[2];
        DataMap dataMap = new DataMap();

        DbEntity entity1 = new DbEntity("entity1");
        DbAttribute attr1 = new DbAttribute("attr1");
        attr1.setEntity(entity1);

        DbAttribute attr1A = new DbAttribute("attr1A");
        attr1A.setEntity(entity1);
        entity1.addAttribute(attr1);
        entity1.addAttribute(attr1A);

        dataMap.addDbEntity(entity1);

        DbEntity entity2 = new DbEntity("entity2");
        DbAttribute attr2 = new DbAttribute("attr2");
        attr2.setEntity(entity2);
        attr2.setPrimaryKey(true);
        attr2.setMandatory(true);

        DbAttribute attr2A = new DbAttribute("attr2A");
        attr2A.setPrimaryKey(true);
        attr2A.setEntity(entity2);
        entity2.addAttribute(attr2);
        entity2.addAttribute(attr2A);

        dataMap.addDbEntity(entity2);

        ColumnPair[] columnPairs = new ColumnPair[] {
                new ColumnPair("attr1", "attr2"),
                new ColumnPair("attr1A", "attr2A")
        };
        ColumnPairsCondition dbJoinCondition = new ColumnPairsCondition(columnPairs);

        dbJoin = new DbJoinBuilder()
                .condition(dbJoinCondition)
                .entities(new String[]{"entity1", "entity2"})
                .names(new String[]{"test", "test2"})
                .toDepPkSemantics(ToDependentPkSemantics.NONE)
                .toManySemantics(ToManySemantics.ONE_TO_ONE)
                .dataMap(dataMap)
                .build();
        dbJoin.compile(dataMap);

        relationships[0] = dbJoin.getRelationhsip();
        relationships[1] = relationships[0].getReverseRelationship();
    }

    @Test
    public void baseTest() {
        assertEquals("entity1", relationships[0].getSourceEntity().getName());
        assertEquals("entity2", relationships[0].getTargetEntity().getName());

        assertEquals("entity2", relationships[1].getSourceEntity().getName());
        assertEquals("entity1", relationships[1].getTargetEntity().getName());
    }

    @Test
    public void testReverseRelationship() {
        assertEquals(relationships[1], relationships[0].getReverseRelationship());
        assertEquals(relationships[0], relationships[1].getReverseRelationship());
    }

    @Test
    public void testIsToPk() {
        assertTrue(relationships[0].isToPK());
        assertFalse(relationships[1].isToPK());
    }

    @Test
    public void testIsFromPk() {
        assertFalse(relationships[0].isFromPK());
        assertTrue(relationships[1].isFromPK());
    }

    @Test
    public void testMandatory() {
        assertFalse(relationships[0].isMandatory());
        assertTrue(relationships[1].isMandatory());
    }

    @Test
    public void testIsToMasterPk() {
        assertFalse(relationships[0].isToMasterPK());
        assertFalse(relationships[1].isToMasterPK());
    }

    @Test
    public void testIsSourceIndependentFromTargetChange() {
        assertFalse(relationships[0].isSourceIndependentFromTargetChange());
        assertTrue(relationships[1].isSourceIndependentFromTargetChange());
    }

    @Test
    public void testSrcFkSnapshotWithTargetSnapshot() {
        Map<String, Object> map = new HashMap<>();
        Integer id = 44;
        map.put("attr1", id);
        map.put("attr1A", id);

        Map<String, Object> targetMap = relationships[0].getReverseRelationship()
                .srcFkSnapshotWithTargetSnapshot(map);
        assertEquals(id, targetMap.get("attr2"));
        assertEquals(id, targetMap.get("attr2A"));
    }

    @Test
    public void testMappingAttributes() {
        Collection<DbAttribute> srcAttributes1 = relationships[0].getSourceAttributes();
        Collection<DbAttribute> targetAttributes1 = relationships[0].getTargetAttributes();

        assertEquals(2, srcAttributes1.size());
        assertEquals(2, targetAttributes1.size());
        assertEquals("attr1", ((List<DbAttribute>) srcAttributes1).get(0).getName());
        assertEquals("attr1A", ((List<DbAttribute>) srcAttributes1).get(1).getName());
        assertEquals("attr2", ((List<DbAttribute>) targetAttributes1).get(0).getName());
        assertEquals("attr2A", ((List<DbAttribute>) targetAttributes1).get(1).getName());

        Collection<DbAttribute> srcAttributes2 = relationships[1].getSourceAttributes();
        Collection<DbAttribute> targetAttributes2 = relationships[1].getTargetAttributes();
        assertEquals(2, srcAttributes2.size());
        assertEquals(2, targetAttributes2.size());
        assertEquals("attr1", ((List<DbAttribute>) targetAttributes2).get(0).getName());
        assertEquals("attr1A", ((List<DbAttribute>) targetAttributes2).get(1).getName());
        assertEquals("attr2", ((List<DbAttribute>) srcAttributes2).get(0).getName());
        assertEquals("attr2A", ((List<DbAttribute>) srcAttributes2).get(1).getName());
    }

    @Test
    public void testTargetPkSnapshotWithSrcSnapshot() {
        Map<String, Object> map = new HashMap<>();
        Integer id = 44;
        map.put("attr1", id);
        map.put("attr1A", id);

        Map<String, Object> targetMap = relationships[0]
                .targetPkSnapshotWithSrcSnapshot(map);
        assertEquals(id, targetMap.get("attr2"));
        assertEquals(id, targetMap.get("attr2A"));
    }

    @Test
    public void testEncodeDbJoinAsXml() {
        StringWriter w = new StringWriter();
        XMLEncoder encoder = new XMLEncoder(new PrintWriter(w));

        dbJoin.encodeAsXML(encoder, new EmptyConfigurationNodeVisitor());
        assertEquals("<db-join toMany=\"ONE_TO_ONE\" toDependentPK=\"NONE\">\n" +
                "<left entity=\"entity1\" name=\"test\"/>\n" +
                "<right entity=\"entity2\" name=\"test2\"/>\n" +
                "<db-join-condition>\n" +
                "<column-pair left=\"attr1\" right=\"attr2\"/>\n" +
                "<column-pair left=\"attr1A\" right=\"attr2A\"/>\n" +
                "</db-join-condition>\n" +
                "</db-join>\n", w.toString());
    }
}
