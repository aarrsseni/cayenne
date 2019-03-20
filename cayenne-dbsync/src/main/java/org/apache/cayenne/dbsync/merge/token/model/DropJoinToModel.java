package org.apache.cayenne.dbsync.merge.token.model;

import org.apache.cayenne.dbsync.merge.context.MergerContext;
import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactory;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.map.relationship.DbJoin;

public class DropJoinToModel extends AbstractToModelToken {

    private final DbJoin dbJoin;

    public DropJoinToModel(DbJoin dbJoin) {
        super("Drop db-join ", 15);
        this.dbJoin = dbJoin;
    }

    @Override
    public String getTokenValue() {
        return AddJoinToModel.getTokenValue(dbJoin);
    }

    @Override
    public MergerToken createReverse(MergerTokenFactory factory) {
        return factory.createAddJoinToDb(dbJoin);
    }

    @Override
    public void execute(MergerContext context) {
        remove(context.getDelegate(), dbJoin);
    }
}
