package org.apache.cayenne.map;

public class ExpressionJoin extends Joins{

    @Override
    public boolean accept(RelationshipJoinVisitor relationshipJoinVisitor) {
        return relationshipJoinVisitor.visit(this);
    }
}
