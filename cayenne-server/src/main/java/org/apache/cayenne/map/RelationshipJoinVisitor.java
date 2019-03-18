package org.apache.cayenne.map;

public interface RelationshipJoinVisitor {

    boolean visit(DbJoin join);

    default boolean visit(ExpressionJoin expressionJoin){
        return true;
    }

}
