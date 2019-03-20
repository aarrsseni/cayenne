package org.apache.cayenne.configuration.xml;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.map.relationship.RelationshipDirection;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DbJoinHandler extends NamespaceAwareNestedTagHandler{

    private static final String DB_JOIN_TAG = "db-join";
    private static final String DB_JOIN_CONDITION = "db-join-condition";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";

    private DataMap map;
    private DbJoinBuilder dbJoinBuilder;
    private DbJoin dbJoin;

    private String[] entities;
    private String[] names;

    public DbJoinHandler(NamespaceAwareNestedTagHandler parentHandler, DataMap map) {
        super(parentHandler);
        this.map = map;
        this.dbJoinBuilder = new DbJoinBuilder();
        this.names = new String[2];
        this.entities = new String[2];
    }

    @Override
    protected boolean processElement(String namespaceURI, String localName, Attributes attributes) throws SAXException {
        switch (localName) {
            case DB_JOIN_TAG:
                createDbJoin(attributes);
                return true;
            case LEFT:
                addContent(attributes, RelationshipDirection.LEFT);
                return true;
            case RIGHT:
                addContent(attributes, RelationshipDirection.RIGHT);
                return true;
        }
        return false;
    }

    @Override
    protected ContentHandler createChildTagHandler(String namespaceURI, String localName,
                                                   String qName, Attributes attributes) {

        if(namespaceURI.equals(targetNamespace)) {
            switch (localName) {
                case DB_JOIN_CONDITION:
                    return new DbJoinConditionHandler(this, dbJoinBuilder);
            }
        }

        return super.createChildTagHandler(namespaceURI, localName, qName, attributes);
    }

    private void createDbJoin(Attributes attributes) throws SAXException {
        String toMany = attributes.getValue("toMany");
        if(toMany == null) {
            throw new SAXException("DbJoinHandler::createDbJoin() - missing \"toMany\" attribute.");
        }
        String toDepPk = attributes.getValue("toDependentPK");
        if(toDepPk == null) {
            throw new SAXException("DbJoinHandler::createDbJoin() - missing \"toDependentPK\" attribute.");
        }
        dbJoinBuilder
                .toManySemantics(ToManySemantics.getSemantics(toMany))
                .toDepPkSemantics(ToDependentPkSemantics.getSemantics(toDepPk));
    }

    private void addContent(Attributes attributes, RelationshipDirection direction) throws SAXException {
        String entity = attributes.getValue("entity");
        if(entity == null) {
            throw new SAXException("DbJoinHandler::addContent() - missing \"entity\" attribute.");
        }
        String name = attributes.getValue("name");

        int index = direction.ordinal();
        entities[index] = entity;
        names[index] = name;
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        super.endElement(namespaceURI, localName, qName);
        map.addJoin(createDbJoin());
    }

    public DbJoin getDbJoin() {
        return createDbJoin();
    }

    private DbJoin createDbJoin() {
        if(dbJoin == null) {
            dbJoinBuilder
                    .entities(entities)
                    .names(names)
                    .dataMap(map);
            dbJoin = dbJoinBuilder.build();
        }
        return dbJoin;
    }
}
