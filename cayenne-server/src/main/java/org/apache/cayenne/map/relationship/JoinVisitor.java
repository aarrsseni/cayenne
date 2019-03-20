package org.apache.cayenne.map.relationship;

public interface JoinVisitor<T> {

    T visit(ColumnPair columnPair);

    T visit(ColumnPair[] columnPairs);
}
