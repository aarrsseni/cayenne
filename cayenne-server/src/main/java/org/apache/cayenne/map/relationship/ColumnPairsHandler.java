package org.apache.cayenne.map.relationship;

public interface ColumnPairsHandler {

    <T> T handle(DirectionalJoinVisitor<T> directionalJoinVisitor);

}
