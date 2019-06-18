package org.apache.cayenne.dbsync.merge.token.db;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactory;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.JoinVisitor;
import org.apache.cayenne.map.relationship.RelationshipDirection;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;

public class AddJoinToDb extends AbstractToDbToken {

    private DbJoin dbJoin;

    public AddJoinToDb(DbJoin dbJoin) {
        super("Add foreign key", 120);
        this.dbJoin = dbJoin;
    }

    @Override
    public List<String> createSql(DbAdapter adapter) {
        String fksql = adapter.createFkConstraint(dbJoin, getRelationshipDirection(dbJoin));
        if(fksql != null) {
            return Collections.singletonList(fksql);
        }
        return Collections.emptyList();
    }

    @Override
    public String getTokenValue() {
        DbRelationship relationship = dbJoin.getRelationship(getRelationshipDirection(dbJoin));
        return relationship.getSourceEntity().getName() + "->" + relationship.getTargetEntityName();
    }

    @Override
    public MergerToken createReverse(MergerTokenFactory factory) {
        return factory.createDropJoinToModel(dbJoin);
    }

    public static RelationshipDirection getRelationshipDirection(DbJoin dbJoin) {
        DataMap dataMap = dbJoin.getDataMap();
        DbEntity rightEntity = dataMap.getDbEntity(dbJoin
                .getDbEntities()[RelationshipDirection.RIGHT.ordinal()]);
        DbEntity leftEntity = dataMap.getDbEntity(dbJoin
                .getDbEntities()[RelationshipDirection.LEFT.ordinal()]);
        ToDependentPkSemantics toDependentPkSemantics = dbJoin.getToDependentPkSemantics();
        if(toDependentPkSemantics == ToDependentPkSemantics.NONE) {
            return dbJoin.getDbJoinCondition().accept(new JoinVisitor<RelationshipDirection>() {

                private boolean isRightPk(ColumnPair columnPair) {
                    DbAttribute rightAttr = rightEntity.getAttribute(columnPair.getRight());
                    return rightAttr.isPrimaryKey();
                }

                private boolean isLeftPk(ColumnPair columnPair) {
                    DbAttribute leftAttr = leftEntity.getAttribute(columnPair.getLeft());
                    return leftAttr.isPrimaryKey();
                }

                @Override
                public RelationshipDirection visit(ColumnPair columnPair) {
                    if(isRightPk(columnPair)) {
                        if(isLeftPk(columnPair)) {
                            throw new CayenneRuntimeException("Try to process relationship " +
                                    "with no toDepPk flag.");
                        }
                        return RelationshipDirection.LEFT;
                    }
                    return RelationshipDirection.RIGHT;
                }

                @Override
                public RelationshipDirection visit(ColumnPair[] columnPairs) {
                    for(ColumnPair columnPair : columnPairs) {
                        boolean isRightPk = isRightPk(columnPair);
                        boolean isLeftPk = isLeftPk(columnPair);
                        if(isRightPk) {
                            if(isLeftPk) {
                                throw new CayenneRuntimeException("Try to process relationship " +
                                        "with no toDepPk flag.");
                            }
                            return RelationshipDirection.LEFT;
                        }
                    }
                    return RelationshipDirection.RIGHT;
                }
            });
        }
        return toDependentPkSemantics == ToDependentPkSemantics.LEFT ?
                RelationshipDirection.LEFT :
                RelationshipDirection.RIGHT;
    }
}
