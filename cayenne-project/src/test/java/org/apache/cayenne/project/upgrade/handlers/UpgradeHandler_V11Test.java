package org.apache.cayenne.project.upgrade.handlers;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UpgradeHandler_V11Test extends BaseUpgradeHandlerTest{

    @Override
    UpgradeHandler newHandler() {
        return new UpgradeHandler_V11();
    }

    @Test
    public void testProjectDomUpgrade() throws Exception {
        Document document = processProjectDom("cayenne-project-v10.xml");

        Element root = document.getDocumentElement();
        assertEquals("11", root.getAttribute("project-version"));
        assertEquals("http://cayenne.apache.org/schema/11/domain", root.getAttribute("xmlns"));
    }

    @Test
    public void testDataMapDomUpgrade() throws Exception {
        Document document = processDataMapDom("test-map-v10.map.xml");

        Element root = document.getDocumentElement();
        assertEquals("11", root.getAttribute("project-version"));
        assertEquals("http://cayenne.apache.org/schema/11/modelMap", root.getAttribute("xmlns"));
        assertEquals(0, root.getElementsByTagName("db-relationship").getLength());

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList joins = (NodeList) xpath.evaluate("/data-map/db-join", document, XPathConstants.NODESET);

        assertEquals(5, joins.getLength());

        Element join = (Element) joins.item(0);

        assertEquals(ToManySemantics.ONE_TO_MANY.name(), join.getAttribute("toMany"));
        assertEquals(ToDependentPkSemantics.NONE.name(), join.getAttribute("toDependentPK"));

        Element left = (Element) join.getElementsByTagName("left").item(0);
        assertNotNull(left);
        assertEquals("ARTIST", left.getAttribute("entity"));
        assertEquals("paintings", left.getAttribute("name"));

        Element right = (Element) join.getElementsByTagName("right").item(0);
        assertNotNull(right);
        assertEquals("PAINTING", right.getAttribute("entity"));
        assertEquals("toArtist", right.getAttribute("name"));

        Element dbJoinCondition = (Element) join.getElementsByTagName("db-join-condition").item(0);
        assertNotNull(dbJoinCondition);
        NodeList columnPairs = dbJoinCondition.getElementsByTagName("column-pair");
        assertEquals(1, columnPairs.getLength());

        Element columnPair = (Element) columnPairs.item(0);
        assertNotNull(columnPair);
        assertEquals("ID", columnPair.getAttribute("left"));
        assertEquals("ARTIST_ID", columnPair.getAttribute("right"));

        Element comment = (Element) join.getElementsByTagName("info:property").item(0);
        assertNotNull(comment);
        assertEquals("LEFT:test paintings, RIGHT:test toArtist", comment.getAttribute("value"));

        Element join1 = (Element) joins.item(1);
        Element comment1 = (Element) join1.getElementsByTagName("info:property").item(0);
        assertNotNull(comment1);
        assertEquals("RIGHT:comment", comment1.getAttribute("value"));
        assertEquals("http://cayenne.apache.org/schema/11/info", comment.getAttribute("xmlns:info"));
        assertEquals(ToManySemantics.MANY_TO_MANY.name(), join1.getAttribute("toMany"));
        assertEquals(ToDependentPkSemantics.LEFT.name(), join1.getAttribute("toDependentPK"));

        Element join2 = (Element) joins.item(2);
        Element comment2 = (Element) join2.getElementsByTagName("info:property").item(0);
        assertNull(comment2);
        assertEquals(ToManySemantics.MANY_TO_ONE.name(), join2.getAttribute("toMany"));
        assertEquals(ToDependentPkSemantics.RIGHT.name(), join2.getAttribute("toDependentPK"));

        Element join3 = (Element) joins.item(3);
        assertEquals(ToManySemantics.ONE_TO_ONE.name(), join3.getAttribute("toMany"));

        Element join4 = (Element) joins.item(4);
        assertEquals(ToManySemantics.ONE_TO_MANY.name(), join4.getAttribute("toMany"));
        assertEquals(ToDependentPkSemantics.NONE.name(), join4.getAttribute("toDependentPK"));

        Element left4 = (Element) join4.getElementsByTagName("left").item(0);
        assertNotNull(left4);
        assertEquals("TEST7", left4.getAttribute("entity"));
        assertEquals("test8", left4.getAttribute("name"));

        Element right4 = (Element) join4.getElementsByTagName("right").item(0);
        assertNotNull(right4);
        assertEquals("TEST8", right4.getAttribute("entity"));
        assertTrue(right4.getAttribute("name").isEmpty());

        Element dbJoinCondition4 = (Element) join4.getElementsByTagName("db-join-condition").item(0);
        assertNotNull(dbJoinCondition);
        NodeList columnPairs4 = dbJoinCondition4.getElementsByTagName("column-pair");
        assertEquals(2, columnPairs4.getLength());
    }
}
