package org.apache.cayenne.dbsync.merge.token.db;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.QuotingStrategy;
import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactory;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.dbsync.reverse.dbload.DbJoinDetected;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.RelationshipDirection;
import org.apache.cayenne.map.relationship.ToManySemantics;

public class DropJoinToDb extends AbstractToDbToken {

    private DbJoin dbJoin;

    public DropJoinToDb(DbJoin dbJoin) {
        super("Drop foreign key", 10);
        this.dbJoin = dbJoin;
    }

    public String getFkName() {
        DbJoinDetected dbJoinDetected = (DbJoinDetected) dbJoin;
        return dbJoinDetected.getFkName();
    }

    @Override
    public List<String> createSql(DbAdapter adapter) {
        RelationshipDirection direction = AddJoinToDb.getRelationshipDirection(dbJoin);
        DataMap dataMap = dbJoin.getDataMap();
        DbEntity entity = dataMap.getDbEntity(
                dbJoin.getDbEntities()[direction.ordinal()]);
        QuotingStrategy context = adapter.getQuotingStrategy();
        return Collections.singletonList(
                "ALTER TABLE " + context.quotedFullyQualifiedName(entity) + " DROP CONSTRAINT " + getFkName());
    }

    @Override
    public MergerToken createReverse(MergerTokenFactory factory) {
        return factory.createAddJoinToModel(dbJoin);
    }

    @Override
    public String getTokenValue() {
        ToManySemantics toManySemantics = dbJoin.getToManySemantics();
        if(toManySemantics == ToManySemantics.MANY_TO_MANY) {
            return "Skip. No sql representation.";
        }
        String left = dbJoin.getDbEntities()[RelationshipDirection.LEFT.ordinal()];
        String right = dbJoin.getDbEntities()[RelationshipDirection.RIGHT.ordinal()];
        if(toManySemantics == ToManySemantics.ONE_TO_MANY) {
            return right + "->" + left;
        }
        return left + "->" + right;
    }
}
