package org.apache.cayenne.configuration.xml;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.DirectionalJoinVisitor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DbJoinHandlerTest extends BaseHandlerTest {

    @Test
    public void testParsing() throws Exception {
        final DataMap map = new DataMap();
        DbEntity entity = new DbEntity("ARTIST");
        DbAttribute id = new DbAttribute("ID");
        entity.addAttribute(id);
        map.addDbEntity(entity);
        DbEntity entity1 = new DbEntity("PAINTING");
        DbAttribute artistId = new DbAttribute("ARTIST_ID");
        entity1.addAttribute(artistId);
        map.addDbEntity(entity1);
        assertEquals(0, entity.getRelationships().size());
        assertEquals(0, entity1.getRelationships().size());

        parse("db-join", parent -> new DbJoinHandler(parent, map));
        map.getDbJoinList().forEach(dbJoin -> dbJoin.compile(map));

        assertEquals(1, entity.getRelationships().size());

        DbRelationship relationship = entity.getRelationship("test1");
        assertNotNull(relationship);
        DbRelationship reverseRelationship = relationship.getReverseRelationship();
        assertNotNull(reverseRelationship);
        assertTrue(reverseRelationship.isRuntime());

        assertEquals("PAINTING", relationship.getTargetEntityName());
        assertEquals("ARTIST", reverseRelationship.getTargetEntityName());
        assertFalse(relationship.isToDependentPK());
        assertFalse(relationship.isToMany());
        relationship.accept(new DirectionalJoinVisitor<Void>() {
            @Override
            public Void visit(DbAttribute[] source, DbAttribute[] target) {
                return null;
            }

            @Override
            public Void visit(DbAttribute source, DbAttribute target) {
                assertEquals("ID", source.getName());
                assertEquals("ARTIST_ID", target.getName());
                return null;
            }
        });
    }
}
