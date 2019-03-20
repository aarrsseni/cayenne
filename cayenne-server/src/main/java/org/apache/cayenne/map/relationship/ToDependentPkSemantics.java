package org.apache.cayenne.map.relationship;

import org.apache.cayenne.CayenneRuntimeException;

public enum ToDependentPkSemantics {
    LEFT{
        @Override
        public boolean isToDepPk(RelationshipDirection direction) {
            return direction == RelationshipDirection.LEFT;
        }

        @Override
        public ToDependentPkSemantics getReverse() {
            return RIGHT;
        }
    },
    RIGHT{
        @Override
        public boolean isToDepPk(RelationshipDirection direction) {
            return direction == RelationshipDirection.RIGHT;
        }

        @Override
        public ToDependentPkSemantics getReverse() {
            return LEFT;
        }
    },
    NONE{
        @Override
        public boolean isToDepPk(RelationshipDirection direction) {
            return false;
        }

        @Override
        public ToDependentPkSemantics getReverse() {
            return NONE;
        }
    };

    public abstract boolean isToDepPk(RelationshipDirection direction);

    public abstract ToDependentPkSemantics getReverse();

    public static ToDependentPkSemantics getSemantics(String name) {
        for(ToDependentPkSemantics toDependentPkSemantics : ToDependentPkSemantics.values()) {
            if(toDependentPkSemantics.name().equals(name)) {
                return toDependentPkSemantics;
            }
        }
        return null;
    }

    public static ToDependentPkSemantics getSemantics(boolean toDepPK1, boolean toDepPK2) {
        if(toDepPK1) {
            if(toDepPK2) {
                throw new CayenneRuntimeException("Error in setting dependentPK semantics");
            }
            return LEFT;
        } else if(toDepPK2) {
            return RIGHT;
        }
        return NONE;
    }
}
