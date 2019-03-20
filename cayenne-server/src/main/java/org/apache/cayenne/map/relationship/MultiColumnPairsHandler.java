package org.apache.cayenne.map.relationship;

import java.io.Serializable;

import org.apache.cayenne.map.DbAttribute;

public class MultiColumnPairsHandler implements ColumnPairsHandler, Serializable {

    private static final long serialVersionUID = -2587502014506164621L;

    private DbAttribute[] src;
    private DbAttribute[] target;

    public MultiColumnPairsHandler(DbAttribute[] src, DbAttribute[] target) {
        this.src = src;
        this.target = target;
    }

    @Override
    public <T> T handle(DirectionalJoinVisitor<T> directionalJoinVisitor) {
        return directionalJoinVisitor.visit(src, target);
    }
}
