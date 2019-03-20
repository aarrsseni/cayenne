package org.apache.cayenne.map.relationship;

public class ColumnPairsCondition extends DbJoinCondition {

    private ColumnPair[] pairs;

    public ColumnPairsCondition(ColumnPair[] pairs) {
        this.pairs = pairs;
    }

    @Override
    public <T> T accept(JoinVisitor<T> joinVisitor) {
        return joinVisitor.visit(pairs);
    }
}
