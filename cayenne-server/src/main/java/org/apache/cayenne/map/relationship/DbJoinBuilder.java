package org.apache.cayenne.map.relationship;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.map.DataMap;

public class DbJoinBuilder {

    protected DbJoinCondition dbJoinCondition;
    protected String[] dbEntities;
    protected String[] names;
    protected ToDependentPkSemantics toDependentPkSemantics;
    protected ToManySemantics toManySemantics;

    protected DataMap dataMap;

    public DbJoinBuilder() {}

    public DbJoinBuilder condition(DbJoinCondition dbJoinCondition) {
        this.dbJoinCondition = dbJoinCondition;
        return this;
    }

    public DbJoinBuilder entities(String[] dbEntities) {
        if(dbEntities.length != 2) {
            throw new CayenneRuntimeException("Invalid number of relationship's entities");
        }
        this.dbEntities = dbEntities;
        return this;
    }

    public DbJoinBuilder names(String[] names) {
        if(names.length != 2) {
            throw new CayenneRuntimeException("Invalid number of relationship's names");
        }
        this.names = names;
        return this;
    }

    public DbJoinBuilder toDepPkSemantics(ToDependentPkSemantics toDependentPkSemantics) {
        this.toDependentPkSemantics = toDependentPkSemantics;
        return this;
    }

    public DbJoinBuilder toManySemantics(ToManySemantics toManySemantics) {
        this.toManySemantics = toManySemantics;
        return this;
    }

    public DbJoinBuilder dataMap(DataMap dataMap) {
        this.dataMap = dataMap;
        return this;
    }

    public String[] getDbEntities(){
        return dbEntities;
    }

    public String[] getNames() {
        return names;
    }

    public ToManySemantics getToManySemantics() {
        return toManySemantics;
    }

    public ToDependentPkSemantics getToDependentPkSemantics() {
        return toDependentPkSemantics;
    }

    public DbJoinCondition getDbJoinCondition() {
        return dbJoinCondition;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public DbJoin build() {
        if(dbJoinCondition == null ||
                dbEntities == null ||
                names == null ||
                toDependentPkSemantics == null ||
                toManySemantics == null ||
                dataMap == null) {
            throw new CayenneRuntimeException("Miss parameters to create dbJoin.");
        }
        return new DbJoin(
                dbJoinCondition,
                dbEntities,
                names,
                toDependentPkSemantics,
                toManySemantics,
                dataMap);
    }

}
