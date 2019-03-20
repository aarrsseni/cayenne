package org.apache.cayenne.dbsync.reverse.dbload;

import java.sql.DatabaseMetaData;

import org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.JoinVisitor;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DbJoinLoaderIT extends BaseLoaderIT {

    @Test
    public void testRelationshipLoad() throws Exception {
        boolean supportsFK = accessStackAdapter.supportsFKConstraints();
        if(!supportsFK) {
            return;
        }

        DatabaseMetaData metaData = connection.getMetaData();
        DbLoaderDelegate delegate = new DefaultDbLoaderDelegate();

        // We need all data to check relationships, so simply load it all
        EntityLoader entityLoader = new EntityLoader(adapter, EMPTY_CONFIG, delegate);
        AttributeLoader attributeLoader = new AttributeLoader(adapter, EMPTY_CONFIG, delegate);
        PrimaryKeyLoader primaryKeyLoader = new PrimaryKeyLoader(EMPTY_CONFIG, delegate);
        ExportedKeyLoader exportedKeyLoader = new ExportedKeyLoader(EMPTY_CONFIG, delegate);

        entityLoader.load(metaData, store);
        attributeLoader.load(metaData, store);
        primaryKeyLoader.load(metaData, store);
        exportedKeyLoader.load(metaData, store);

        // *** TESTING THIS ***
        DbJoinLoader dbJoinLoader = new DbJoinLoader(EMPTY_CONFIG, delegate, new DefaultObjectNameGenerator());
        dbJoinLoader.load(metaData, store);

//        TODO

//        Collection<DbRelationship> rels = getDbEntity("ARTIST").getRelationships();
//        assertNotNull(rels);
//        assertTrue(!rels.isEmpty());
//
//        // test one-to-one
//        rels = getDbEntity("PAINTING").getRelationships();
//        assertNotNull(rels);
//
//        // find relationship to PAINTING_INFO
//        DbRelationship oneToOne = null;
//        for (DbRelationship rel : rels) {
//            if ("PAINTING_INFO".equalsIgnoreCase(rel.getTargetEntityName())) {
//                oneToOne = rel;
//                break;
//            }
//        }
//
//        assertNotNull("No relationship to PAINTING_INFO", oneToOne);
//        assertFalse("Relationship to PAINTING_INFO must be to-one", oneToOne.isToMany());
//        assertTrue("Relationship to PAINTING_INFO must be to-one", oneToOne.isToDependentPK());
    }

    @Test
    public void testNormalization() {
        DbLoaderDelegate delegate = new DefaultDbLoaderDelegate();
        DbJoinLoader dbJoinLoader = new DbJoinLoader(EMPTY_CONFIG, delegate, new DefaultObjectNameGenerator());

        DbJoinDetectedBuilder dbJoinBuilder = new DbJoinDetectedBuilder()
                .entities(new String[]{"b", "a"})
                .names(new String[]{"q", "w"})
                .toManySemantics(ToManySemantics.ONE_TO_MANY)
                .toDepPkSemantics(ToDependentPkSemantics.LEFT)
                .condition(new SinglePairCondition(new ColumnPair("t", "q")));

        DbJoinDetectedBuilder normalized = dbJoinLoader.normalize(dbJoinBuilder);

        assertArrayEquals(normalized.getDbEntities(), new String[]{"a", "b"});
        assertArrayEquals(normalized.getNames(), new String[]{"w", "q"});
        assertEquals(ToManySemantics.MANY_TO_ONE, normalized.getToManySemantics());
        assertEquals(ToDependentPkSemantics.RIGHT, normalized.getToDependentPkSemantics());
        normalized.getDbJoinCondition().accept(new JoinVisitor<Void>() {
            @Override
            public Void visit(ColumnPair columnPair) {
                assertEquals("q", columnPair.getLeft());
                assertEquals("t", columnPair.getRight());
                return null;
            }

            @Override
            public Void visit(ColumnPair[] columnPairs) {
                return null;
            }
        });

        DbJoinDetectedBuilder dbJoinBuilder1 = new DbJoinDetectedBuilder()
                .entities(new String[]{"b", "b"})
                .names(new String[]{"w", "a"})
                .toManySemantics(ToManySemantics.MANY_TO_ONE)
                .toDepPkSemantics(ToDependentPkSemantics.RIGHT)
                .condition(new SinglePairCondition(new ColumnPair("t", "q")));

        DbJoinDetectedBuilder normalized1 = dbJoinLoader.normalize(dbJoinBuilder1);

        assertArrayEquals(new String[]{"b", "b"}, normalized1.getDbEntities());
        assertArrayEquals(new String[]{"a", "w"}, normalized1.getNames());
        assertEquals(ToManySemantics.ONE_TO_MANY, normalized1.getToManySemantics());
        assertEquals(ToDependentPkSemantics.LEFT, normalized1.getToDependentPkSemantics());
        normalized1.getDbJoinCondition().accept(new JoinVisitor<Void>() {
            @Override
            public Void visit(ColumnPair columnPair) {
                assertEquals("q", columnPair.getLeft());
                assertEquals("t", columnPair.getRight());
                return null;
            }

            @Override
            public Void visit(ColumnPair[] columnPairs) {
                return null;
            }
        });
    }

}
