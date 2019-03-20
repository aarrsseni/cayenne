package org.apache.cayenne.map.relationship;

import java.io.Serializable;

public abstract class DbJoinCondition implements Serializable {

    public abstract<T> T accept(JoinVisitor<T> joinVisitor);

}
