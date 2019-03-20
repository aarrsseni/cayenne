package org.apache.cayenne.dbsync.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.cayenne.dbsync.merge.factory.MergerTokenFactory;
import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.dbsync.reverse.filters.FiltersConfig;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.ColumnPairsCondition;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.JoinVisitor;
import org.apache.cayenne.map.relationship.SinglePairCondition;

public class DbJoinMerger extends AbstractMerger<DataMap, DbJoin> {

    private final boolean skipRelationshipsTokens;
    private final FiltersConfig filtersConfig;
    private DataMap originalDataMap;
    private DataMap importedDataMap;

    DbJoinMerger(MergerTokenFactory tokenFactory,
                 boolean skipRelationshipsTokens,
                 FiltersConfig filtersConfig,
                 DataMap original,
                 DataMap imported) {
        super(tokenFactory);
        this.skipRelationshipsTokens = skipRelationshipsTokens;
        this.filtersConfig = filtersConfig;
        this.originalDataMap = original;
        this.importedDataMap = imported;
    }

    @Override
    public List<MergerToken> createMergeTokens() {
        return createMergeTokens(originalDataMap, importedDataMap);
    }

    @Override
    MergerDictionaryDiff<DbJoin> createDiff(DataMap original, DataMap imported) {
        MergerDictionaryDiff<DbJoin> diff = new MergerDictionaryDiff.Builder<DbJoin>()
                .originalDictionary(new DbJoinDictionary(original, filtersConfig))
                .importedDictionary(new DbJoinDictionary(imported, filtersConfig))
                .build();
        return diff;
    }

    @Override
    Collection<MergerToken> createTokensForMissingOriginal(DbJoin imported) {
        String[] dbEntities = imported.getDbEntities();
        DbEntity left = getOriginalName(imported.getDbEntities()[0]);
        DbEntity right = getOriginalName(imported.getDbEntities()[1]);

        if(left != null) {
            dbEntities[0] = left.getName();
        }
        if(right != null) {
            dbEntities[1] = right.getName();
        }

        List<ColumnPair> columnPairs = imported.getDbJoinCondition()
                .accept(new JoinVisitor<List<ColumnPair>>() {

                    private ColumnPair buildColumnPair(ColumnPair columnPair) {
                        DbAttribute sourceAttr = findDbAttribute(left, columnPair.getLeft());
                        DbAttribute targetAttr = findDbAttribute(right, columnPair.getRight());
                        String leftName = sourceAttr != null ? sourceAttr.getName() : columnPair.getLeft();
                        String rightName = targetAttr != null ? targetAttr.getName() : columnPair.getRight();
                        return new ColumnPair(leftName, rightName);
                    }

                    @Override
                    public List<ColumnPair> visit(ColumnPair columnPair) {
                        return Collections.singletonList(buildColumnPair(columnPair));
                    }

                    @Override
                    public List<ColumnPair> visit(ColumnPair[] columnPairs) {
                        List<ColumnPair> pairs = new ArrayList<>();
                        for(ColumnPair columnPair : columnPairs) {
                            pairs.add(buildColumnPair(columnPair));
                        }
                        return pairs;
                    }
                });
        imported.setCondition(buildCondition(columnPairs));
        return Collections.singletonList(getTokenFactory().createDropJoinToDb(imported));
    }

    @Override
    Collection<MergerToken> createTokensForMissingImported(DbJoin original) {
        if(skipRelationshipsTokens) {
            return null;
        }

        String[] dbEntities = original.getDbEntities();
        DbEntity originalLeft = getOriginalName(dbEntities[0]);
        if(originalLeft != null) {
            dbEntities[0] = originalLeft.getName();
        }
        DbEntity originalRight = getOriginalName(dbEntities[1]);
        if(originalRight != null) {
            dbEntities[1] = originalRight.getName();
        }
        Collection<MergerToken> tokens = new LinkedList<>();
        tokens.add(getTokenFactory().createAddJoinToDb(original));

        return tokens;
    }

    private DbAttribute findDbAttribute(DbEntity entity, String caseInsensitiveName) {
        if (entity == null) {
            return null;
        }

        for (DbAttribute a : entity.getAttributes()) {
            if (a.getName().equalsIgnoreCase(caseInsensitiveName)) {
                return a;
            }
        }
        return null;
    }

    private DbEntity getOriginalName(String name) {
        return originalDataMap.getDbEntity(name);
    }

    private DbJoinCondition buildCondition(List<ColumnPair> columnPairs) {
        return columnPairs.size() == 1 ?
                new SinglePairCondition(columnPairs.get(0)) :
                new ColumnPairsCondition(columnPairs.toArray(new ColumnPair[0]));
    }

    @Override
    Collection<MergerToken> createTokensForSame(MergerDiffPair<DbJoin> same) {
        return null;
    }

}
