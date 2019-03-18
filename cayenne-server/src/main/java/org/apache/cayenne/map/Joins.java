package org.apache.cayenne.map;

import java.io.Serializable;

public abstract class Joins implements Serializable {

    public abstract boolean accept(RelationshipJoinVisitor relationshipJoinVisitor);

}
