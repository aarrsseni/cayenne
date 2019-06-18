package org.apache.cayenne.modeler.dialog.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.ColumnPairsCondition;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.JoinVisitor;
import org.apache.cayenne.map.relationship.RelationshipDirection;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.map.relationship.DbJoinModel;
import org.apache.cayenne.modeler.map.relationship.DbJoinMutable;
import org.apache.cayenne.modeler.map.relationship.DbJoinMutableBuilder;
import org.apache.cayenne.project.extension.info.ObjectInfo;

public class DbRelationshipModelConverter {

    public DbJoinMutable createDbJoin(DbJoinModel dbJoinModel, DataMap dataMap) {
        List<ColumnPair> columnPairs = dbJoinModel.getColumnPairs();
        DbJoinCondition dbJoinCondition = columnPairs.size() == 1 ?
                new SinglePairCondition(columnPairs.get(0)) :
                new ColumnPairsCondition(columnPairs.toArray(new ColumnPair[0]));
        DbJoinMutable dbJoin = (DbJoinMutable)new DbJoinMutableBuilder()
                .entities(new String[]{dbJoinModel.getLeftEntity().getName(),
                        dbJoinModel.getRightEntity().getName()})
                .names(new String[]{dbJoinModel.getLeftName(), dbJoinModel.getRightName()})
                .toManySemantics(ToManySemantics
                        .getSemantics(dbJoinModel.getLeftToMany(), dbJoinModel.getRightToMany()))
                .toDepPkSemantics(ToDependentPkSemantics
                        .getSemantics(dbJoinModel.getLeftToDepPK(), dbJoinModel.getRightToDepPK()))
                .condition(dbJoinCondition)
                .dataMap(dataMap)
                .build();
        dataMap.addJoin(dbJoin);
        return dbJoin;
    }

    public DbJoinModel getModel(DbRelationship relationship) {
        DbJoin joinFromRelationship = relationship.getDbJoin();
        DbJoinMutable dbJoin;
        if(!(joinFromRelationship instanceof DbJoinMutable)) {
            DbJoinMutableBuilder builder = new DbJoinMutableBuilder();
            dbJoin = builder.buildFromJoin(joinFromRelationship);
        } else {
            dbJoin = (DbJoinMutable) relationship.getDbJoin();
        }
        DbJoinCondition dbJoinCondition = dbJoin.getDbJoinCondition();

        DbJoinModel dbJoinModel = createModel(relationship);
        dbJoinModel.setColumnPairs(buildPairs(dbJoinCondition, relationship.getDirection()));
        return dbJoinModel;
    }

    private DbJoinModel createModel(DbRelationship relationship) {
        DbJoinModel dbJoinModel = new DbJoinModel();
        DbRelationship reverseRelationship = relationship.getReverseRelationship();
        dbJoinModel.setNames(new String[]{
                relationship.getName(),
                reverseRelationship != null ?
                        reverseRelationship.getName() :
                        null});
        dbJoinModel.setDbEntities(new DbEntity[]{relationship.getSourceEntity(),
                relationship.getTargetEntity()});
        dbJoinModel.setToMany(new boolean[]{relationship.isToMany(),
                reverseRelationship != null && reverseRelationship.isToMany()});
        dbJoinModel.setLeftToDepPK(relationship.isToDependentPK());
        dbJoinModel.setRightToDepPK(reverseRelationship != null && reverseRelationship.isToDependentPK());
        dbJoinModel.setComments(ObjectInfo
                .getFromMetaData(Application.getInstance().getMetaData(),
                        relationship.getDbJoin(),
                        ObjectInfo.COMMENT));
        dbJoinModel.setDataMap(relationship.getSourceEntity().getDataMap());
        return dbJoinModel;
    }

    private List<ColumnPair> buildPairs(DbJoinCondition condition, RelationshipDirection direction) {
        return condition.accept(new JoinVisitor<List<ColumnPair>>() {

            private ColumnPair buildPair(ColumnPair columnPair) {
                return direction == RelationshipDirection.LEFT ?
                        new ColumnPair(columnPair.getLeft(), columnPair.getRight()) :
                        new ColumnPair(columnPair.getRight(), columnPair.getLeft());
            }

            @Override
            public List<ColumnPair> visit(ColumnPair columnPair) {
                List<ColumnPair> columnPairs = new ArrayList<>();
                columnPairs.add(buildPair(columnPair));
                return columnPairs;
            }

            @Override
            public List<ColumnPair> visit(ColumnPair[] columnPairs) {
                List<ColumnPair> columnPairsList = new ArrayList<>();
                for(ColumnPair columnPair : columnPairs) {
                    columnPairsList.add(buildPair(columnPair));
                }
                return columnPairsList;
            }
        });
    }

    public DbJoinMutable updateDbRelationships(DbJoinModel dbJoinModel,
                                               DbRelationship prevRelationship) {
        DbEntity prevSrcEntity = prevRelationship.getSourceEntity();
        DbEntity prevTargetEntity = prevRelationship.getTargetEntity();
        if(dbJoinModel.getLeftEntity() != prevSrcEntity ||
                dbJoinModel.getRightEntity() != prevTargetEntity) {
            prevSrcEntity.removeRelationship(prevRelationship.getName());
            prevTargetEntity.removeRelationship(prevRelationship.getReverseRelationship().getName());
            DataMap dataMap = prevSrcEntity.getDataMap();
            dataMap.getDbJoinList().remove(prevRelationship.getDbJoin());
            DbJoinMutable dbJoin = createDbJoin(dbJoinModel, dataMap);
            dbJoin.compile(dataMap);
            return dbJoin;
        }
        prevRelationship.setName(dbJoinModel.getLeftName());
        prevRelationship.setToMany(dbJoinModel.getLeftToMany());
        prevRelationship.setToDependentPK(dbJoinModel.getLeftToDepPK());

        List<ColumnPair> columnPairs = dbJoinModel.getColumnPairs();
        if(prevRelationship.getDirection() == RelationshipDirection.RIGHT) {
            columnPairs = columnPairs.stream()
                    .map(pair ->
                            new ColumnPair(pair.getRight(), pair.getLeft()))
                    .collect(Collectors.toList());
        }

        DbJoinCondition dbJoinCondition = getDbJoinCondition(columnPairs);
        prevRelationship.getDbJoin().setCondition(dbJoinCondition);
        prevRelationship.updatePairsHandler();

        DbRelationship prevReverseRelationship = prevRelationship.getReverseRelationship();
        prevReverseRelationship.updatePairsHandler();
        prevReverseRelationship.setName(dbJoinModel.getRightName());
        prevReverseRelationship.setToDependentPK(dbJoinModel.getRightToDepPK());
        ObjectInfo.putToMetaData(Application.getInstance().getMetaData(),
                prevRelationship.getDbJoin(),
                ObjectInfo.COMMENT,
                dbJoinModel.getComment());
        return (DbJoinMutable) prevRelationship.getDbJoin();
    }

    private DbJoinCondition getDbJoinCondition(List<ColumnPair> columnPairs) {
        return columnPairs.size() == 1 ?
                new SinglePairCondition(columnPairs.get(0)) :
                new ColumnPairsCondition(columnPairs.toArray(new ColumnPair[0]));
    }

}
