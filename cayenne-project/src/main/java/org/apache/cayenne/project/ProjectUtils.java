package org.apache.cayenne.project;

import java.util.Arrays;

import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.ColumnPairsCondition;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.JoinVisitor;
import org.apache.cayenne.map.relationship.SinglePairCondition;

public class ProjectUtils {

    public static DbJoinCondition buildReverseCondition(DbJoinCondition condition) {
        return condition.accept(new JoinVisitor<DbJoinCondition>() {
            @Override
            public DbJoinCondition visit(ColumnPair columnPair) {
                return new SinglePairCondition(
                        new ColumnPair(
                                columnPair.getRight(),
                                columnPair.getLeft()));
            }

            @Override
            public DbJoinCondition visit(ColumnPair[] columnPairs) {
                return new ColumnPairsCondition(
                        Arrays.stream(columnPairs)
                                .map(columnPair ->
                                        new ColumnPair(columnPair.getRight(), columnPair.getLeft()))
                                .toArray(ColumnPair[]::new));
            }
        });
    }

    public static boolean needToNormalize(String leftEntity,
                                          String rightEntity,
                                          String leftName,
                                          String rightName) {
        int compare = leftEntity.compareTo(rightEntity);
        return compare == 0 ?
                rightName != null && leftName.compareTo(rightName) > 0
                : compare > 0;
    }

}
