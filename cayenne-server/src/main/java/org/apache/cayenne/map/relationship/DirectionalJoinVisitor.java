package org.apache.cayenne.map.relationship;

import org.apache.cayenne.map.DbAttribute;

public interface DirectionalJoinVisitor<T> {

    T visit(DbAttribute[] source, DbAttribute[] target);

    T visit(DbAttribute source, DbAttribute target);

}
