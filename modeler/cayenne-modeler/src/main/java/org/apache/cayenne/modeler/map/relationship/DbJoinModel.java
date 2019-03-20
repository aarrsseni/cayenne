package org.apache.cayenne.modeler.map.relationship;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;

public class DbJoinModel {

    private DbJoinCondition dbJoinCondition;
    private DbEntity[] dbEntities;
    private String[] names;
    private boolean[] toMany;
    private boolean[] toDepPK;
    private String comment;
    private DataMap dataMap;

    private List<ColumnPair> columnPairs;

    public DbJoinModel() {
        dbEntities = new DbEntity[2];
        names = new String[2];
        toMany = new boolean[2];
        toDepPK = new boolean[2];
        columnPairs = new ArrayList<>();
    }

    public DbJoinMutable buildJoin() {
        return (DbJoinMutable) new DbJoinMutableBuilder()
                .entities(new String[]{dbEntities[0].getName(), dbEntities[1].getName()})
                .names(names)
                .toManySemantics(ToManySemantics.getSemantics(toMany[0], toMany[1]))
                .toDepPkSemantics(ToDependentPkSemantics.getSemantics(toDepPK[0], toDepPK[1]))
                .condition(dbJoinCondition)
                .dataMap(dataMap)
                .build();
    }

    public boolean isValidForDepPk(){
        if(columnPairs.isEmpty()) {
            return false;
        }
        for(ColumnPair columnPair : columnPairs) {
            DbAttribute srcAttr = dbEntities[0]
                    .getAttribute(columnPair.getLeft());
            DbAttribute targetAttr = dbEntities[1]
                    .getAttribute(columnPair.getRight());
            if(targetAttr != null && !targetAttr.isPrimaryKey() ||
                    srcAttr != null && !srcAttr.isPrimaryKey()) {
                return false;
            }
        }
        return true;
    }

    public void setDbJoinCondition(DbJoinCondition dbJoinCondition) {
        this.dbJoinCondition = dbJoinCondition;
    }

    public void setDbEntities(DbEntity[] dbEntities) {
        this.dbEntities = dbEntities;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public void setToMany(boolean[] toMany) {
        this.toMany = toMany;
    }

    public void setToDepPK(boolean[] toDepPK) {
        this.toDepPK = toDepPK;
    }

    public void setComments(String comment) {
        this.comment = comment;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public DbEntity[] getDbEntities() {
        return dbEntities;
    }

    public String[] getNames() {
        return names;
    }

    public boolean[] getToMany() {
        return toMany;
    }

    public boolean[] getToDepPK() {
        return toDepPK;
    }

    public String getComment() {
        return comment;
    }

    public void setLeftName(String name) {
        names[0] = name;
    }

    public void setRightName(String name) {
        names[1] = name;
    }

    public void setLeftEntity(DbEntity entity) {
        dbEntities[0] = entity;
    }

    public void setRightEntity(DbEntity entity) {
        dbEntities[1] = entity;
    }

    public void setLeftToMany(boolean toManyLeft) {
        toMany[0] = toManyLeft;
    }

    public void setRightToMany(boolean toManyRight) {
        toMany[1] = toManyRight;
    }

    public void setLeftToDepPK(boolean toDepPKLeft) {
        toDepPK[0] = toDepPKLeft;
    }

    public void setRightToDepPK(boolean toDepPKRight) {
        toDepPK[1] = toDepPKRight;
    }

    public DbEntity getLeftEntity() {
        return dbEntities[0];
    }

    public DbEntity getRightEntity() {
        return dbEntities[1];
    }

    public String getLeftName() {
        return names[0];
    }

    public String getRightName() {
        return names[1];
    }

    public boolean getLeftToMany() {
        return toMany[0];
    }

    public boolean getRightToMany() {
        return toMany[1];
    }

    public boolean getLeftToDepPK() {
        return toDepPK[0];
    }

    public boolean getRightToDepPK() {
        return toDepPK[1];
    }

    public List<ColumnPair> getColumnPairs() {
        return columnPairs;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setColumnPairs(List<ColumnPair> columnPairs) {
        this.columnPairs = columnPairs;
    }
}
