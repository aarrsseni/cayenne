package org.apache.cayenne.map;

import java.util.ArrayList;
import java.util.List;

public class EquiJoin extends Joins{

    private List<DbJoin> joins;

    public EquiJoin() {
        this.joins = new ArrayList<>();
    }

    public List<DbJoin> getJoins() {
        return joins;
    }

    @Override
    public boolean accept(RelationshipJoinVisitor relationshipJoinVisitor) {
        for(DbJoin join : joins) {
            if(!relationshipJoinVisitor.visit(join)) {
                return false;
            }
        }

        return true;
    }
}
