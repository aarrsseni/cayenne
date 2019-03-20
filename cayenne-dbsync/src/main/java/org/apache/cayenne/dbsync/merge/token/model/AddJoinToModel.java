package org.apache.cayenne.dbsync.merge.token.model;

import org.apache.cayenne.dbsync.merge.context.MergerContext;
import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactory;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.JoinVisitor;

public class AddJoinToModel extends AbstractToModelToken {

    private static final String COMMA_SEPARATOR = ", ";
    private static final int COMMA_SEPARATOR_LENGTH = COMMA_SEPARATOR.length();

    private DbJoin dbJoin;

    public AddJoinToModel(DbJoin dbJoin) {
        super("Add db-join", 125);
        this.dbJoin = dbJoin;
    }

    @Override
    public MergerToken createReverse(MergerTokenFactory factory) {
        return factory.createDropJoinToDb(dbJoin);
    }

    @Override
    public void execute(MergerContext context) {
        DataMap dataMap = context.getDataMap();
        DbJoin addedJoin = new DbJoinBuilder()
                .entities(dbJoin.getDbEntities())
                .names(dbJoin.getNames())
                .toManySemantics(dbJoin.getToManySemantics())
                .toDepPkSemantics(dbJoin.getToDependentPkSemantics())
                .condition(dbJoin.getDbJoinCondition())
                .dataMap(dataMap)
                .build();
        dataMap.addJoin(addedJoin);

        dbJoin.compile(dataMap);
        DbRelationship relationship = dbJoin.getRelationhsip();
        DbRelationship reverseRelationship = relationship.getReverseRelationship();

        DbEntity srcEntity = relationship.getSourceEntity();
        DbEntity targetEntity = reverseRelationship.getSourceEntity();
        for(ObjEntity objEntity : getMappedObjEntities(srcEntity)) {
            context.getEntityMergeSupport().synchronizeOnDbRelationshipAdded(objEntity, relationship);
        }
        for(ObjEntity objEntity : getMappedObjEntities(targetEntity)) {
            context.getEntityMergeSupport().synchronizeOnDbRelationshipAdded(objEntity, reverseRelationship);
        }
    }

    @Override
    public String getTokenValue() {
        return getTokenValue(dbJoin);
    }

    public static String getTokenValue(DbJoin dbJoin) {
        String attributes = dbJoin.getDbJoinCondition()
                .accept(new JoinVisitor<String>() {
                    @Override
                    public String visit(ColumnPair columnPair) {
                        return columnPair.getRight();
                    }

                    @Override
                    public String visit(ColumnPair[] columnPairs) {
                        StringBuilder currRes = new StringBuilder();
                        for (ColumnPair columnPair : columnPairs) {
                            currRes.append(columnPair.getRight()).append(COMMA_SEPARATOR);
                        }
                        return currRes.toString();
                    }
                });

        if(attributes.isEmpty()) {
            attributes = "{}";
        } else {
            attributes = "{" + attributes.substring(0, attributes.length() - COMMA_SEPARATOR_LENGTH) + "}";
        }
        return dbJoin.getNames()[0] + " " + dbJoin.getDbEntities()[0] + "->" +
                dbJoin.getDbEntities()[1] + "." + attributes;
    }
}
