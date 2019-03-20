package org.apache.cayenne.dbsync.reverse.dbload;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;

public class DbJoinDetected extends DbJoin {

    private String fkName;

    DbJoinDetected(DbJoinCondition dbJoinCondition,
                   String[] dbEntities,
                   String[] names,
                   ToDependentPkSemantics toDependentPkSemantics,
                   ToManySemantics toManySemantics, String fkName,
                   DataMap dataMap) {
        super(dbJoinCondition,
                dbEntities,
                names,
                toDependentPkSemantics,
                toManySemantics,
                dataMap);
        this.fkName = fkName;
    }

    public String getFkName() {
        return fkName;
    }
}
