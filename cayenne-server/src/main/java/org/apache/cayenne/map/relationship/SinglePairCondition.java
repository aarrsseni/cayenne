package org.apache.cayenne.map.relationship;

public class SinglePairCondition extends DbJoinCondition {

    private ColumnPair columnPair;

    public SinglePairCondition(ColumnPair columnPair) {
        this.columnPair = columnPair;
    }

    @Override
    public <T> T accept(JoinVisitor<T> joinVisitor) {
        return joinVisitor.visit(columnPair);
    }
}
