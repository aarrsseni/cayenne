package org.apache.cayenne.map.relationship;

public enum ToManySemantics {
    ONE_TO_ONE {
        @Override
        public boolean isToMany(RelationshipDirection direction) {
            return false;
        }

        @Override
        public ToManySemantics getReverse() {
            return ONE_TO_ONE;
        }
    },
    ONE_TO_MANY {
        @Override
        public boolean isToMany(RelationshipDirection direction) {
            return direction == RelationshipDirection.LEFT;
        }

        @Override
        public ToManySemantics getReverse() {
            return MANY_TO_ONE;
        }
    },
    MANY_TO_ONE {
        @Override
        public boolean isToMany(RelationshipDirection direction) {
            return direction == RelationshipDirection.RIGHT;
        }

        @Override
        public ToManySemantics getReverse() {
            return ONE_TO_MANY;
        }
    },
    MANY_TO_MANY {
        @Override
        public boolean isToMany(RelationshipDirection direction) {
            return true;
        }

        @Override
        public ToManySemantics getReverse() {
            return MANY_TO_MANY;
        }
    };

    public abstract boolean isToMany(RelationshipDirection direction);

    public abstract ToManySemantics getReverse();

    public static ToManySemantics getSemantics(String name) {
        for(ToManySemantics toManySemantics : ToManySemantics.values()) {
            if(toManySemantics.name().equals(name)) {
                return toManySemantics;
            }
        }
        return null;
    }

    public static ToManySemantics getSemantics(boolean toMany, boolean reverseToMany) {
        return toMany ?
                reverseToMany ? MANY_TO_MANY : ONE_TO_MANY :
                reverseToMany ? MANY_TO_ONE : ONE_TO_ONE;
    }
}
