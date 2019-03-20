package org.apache.cayenne.map.relationship;

import java.io.Serializable;

import org.apache.cayenne.map.DbAttribute;

public class SingleColumnPairHandler implements ColumnPairsHandler, Serializable {

    private static final long serialVersionUID = 7980691924597575959L;

    private DbAttribute srcAttribute;
    private DbAttribute targetAttribute;

    public SingleColumnPairHandler(DbAttribute srcAttribute, DbAttribute targetAttribute) {
        this.srcAttribute = srcAttribute;
        this.targetAttribute = targetAttribute;
    }

    @Override
    public <T> T handle(DirectionalJoinVisitor<T> directionalJoinVisitor) {
        return directionalJoinVisitor.visit(srcAttribute, targetAttribute);
    }
}
