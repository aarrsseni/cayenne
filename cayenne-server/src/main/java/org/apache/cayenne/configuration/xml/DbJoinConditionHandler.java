package org.apache.cayenne.configuration.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.ColumnPairsCondition;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DbJoinConditionHandler extends NamespaceAwareNestedTagHandler{

    private static final String COLUMN_PAIR_TAG = "column-pair";

    private org.apache.cayenne.map.relationship.DbJoinBuilder dbJoinBuilder;
    private List<ColumnPair> columnPairs;

    public DbJoinConditionHandler(NamespaceAwareNestedTagHandler parentHandler, org.apache.cayenne.map.relationship.DbJoinBuilder dbJoinBuilder) {
        super(parentHandler);
        this.dbJoinBuilder = dbJoinBuilder;
        this.columnPairs = new ArrayList<>();
    }

    @Override
    protected boolean processElement(String namespaceURI, String localName, Attributes attributes) throws SAXException {

        switch (localName) {
            case COLUMN_PAIR_TAG:
                createColumnPair(attributes);
                return true;
        }
        return false;
    }

    private void createColumnPair(Attributes attributes) throws SAXException {
        String left = attributes.getValue("left");
        if(left == null) {
            throw new SAXException("DbJoinConditionHandler::createColumnPair() - missing \"left\" attribute.");
        }
        String right = attributes.getValue("right");
        if(right == null) {
            throw new SAXException("DbJoinConditionHandler::createColumnPair() - missing \"right\" attribute.");
        }

        columnPairs.add(new ColumnPair(left, right));
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        super.endElement(namespaceURI, localName, qName);
        DbJoinCondition condition = columnPairs.size() == 1 ?
                new SinglePairCondition(columnPairs.get(0)) :
                new ColumnPairsCondition(columnPairs.toArray(new ColumnPair[0]));
        dbJoinBuilder.condition(condition);
    }
}
