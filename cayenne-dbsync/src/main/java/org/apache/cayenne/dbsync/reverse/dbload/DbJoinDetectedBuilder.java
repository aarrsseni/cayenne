package org.apache.cayenne.dbsync.reverse.dbload;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;

public class DbJoinDetectedBuilder extends DbJoinBuilder {

    private String fkName;

    public DbJoinDetectedBuilder() {
        super();
    }

    public DbJoinDetectedBuilder fkNames(String fkName) {
        this.fkName = fkName;
        return this;
    }

    public DbJoinDetectedBuilder condition(DbJoinCondition dbJoinCondition) {
        return (DbJoinDetectedBuilder) super.condition(dbJoinCondition);
    }

    public DbJoinDetectedBuilder entities(String[] dbEntities) {
        return (DbJoinDetectedBuilder) super.entities(dbEntities);
    }

    public DbJoinDetectedBuilder names(String[] names) {
        return (DbJoinDetectedBuilder) super.names(names);
    }

    public DbJoinDetectedBuilder toDepPkSemantics(ToDependentPkSemantics toDependentPkSemantics) {
        return (DbJoinDetectedBuilder) super.toDepPkSemantics(toDependentPkSemantics);
    }

    public DbJoinDetectedBuilder toManySemantics(ToManySemantics toManySemantics) {
        return (DbJoinDetectedBuilder) super.toManySemantics(toManySemantics);
    }

    public DbJoinDetectedBuilder dataMap(DataMap dataMap) {
        return (DbJoinDetectedBuilder) super.dataMap(dataMap);
    }

    public String getFkName() {
        return fkName;
    }

    public DbJoinDetected build() {
        if(dbJoinCondition == null ||
                dbEntities == null ||
                names == null ||
                toDependentPkSemantics == null ||
                toManySemantics == null ||
                fkName == null ||
                dataMap == null) {
            throw new CayenneRuntimeException("Miss parameters to create dbJoin.");
        }
        return new DbJoinDetected(
                dbJoinCondition,
                dbEntities,
                names,
                toDependentPkSemantics,
                toManySemantics,
                fkName,
                dataMap);
    }

}
