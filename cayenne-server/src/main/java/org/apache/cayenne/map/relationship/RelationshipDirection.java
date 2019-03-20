package org.apache.cayenne.map.relationship;

public enum RelationshipDirection {

    LEFT{
        @Override
        public RelationshipDirection getOppositeDirection() {
            return RIGHT;
        }
    },
    RIGHT {
        @Override
        public RelationshipDirection getOppositeDirection() {
            return LEFT;
        }
    };

    public abstract RelationshipDirection getOppositeDirection();
}
