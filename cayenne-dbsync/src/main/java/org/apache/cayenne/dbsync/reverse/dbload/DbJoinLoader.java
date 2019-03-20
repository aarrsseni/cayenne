package org.apache.cayenne.dbsync.reverse.dbload;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.dbsync.naming.ObjectNameGenerator;
import org.apache.cayenne.dbsync.reverse.filters.CatalogFilter;
import org.apache.cayenne.dbsync.reverse.filters.SchemaFilter;
import org.apache.cayenne.dbsync.reverse.filters.TableFilter;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.ColumnPairsCondition;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.JoinVisitor;
import org.apache.cayenne.map.relationship.RelationshipDirection;
import org.apache.cayenne.map.relationship.SinglePairCondition;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.apache.cayenne.project.ProjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbJoinLoader extends AbstractLoader{

    private static final Logger LOGGER = LoggerFactory.getLogger(DbLoader.class);

    private final ObjectNameGenerator nameGenerator;

    DbJoinLoader(DbLoaderConfiguration config,
                 DbLoaderDelegate delegate,
                 ObjectNameGenerator nameGenerator) {
        super(null, config, delegate);
        this.nameGenerator = nameGenerator;
    }

    @Override
    public void load(DatabaseMetaData metaData, DbLoadDataStore map) throws SQLException {
        if (config.isSkipRelationshipsLoading()) {
            return;
        }

        for (Map.Entry<String, Set<ExportedKey>> entry : map.getExportedKeysEntrySet()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Process keys for: " + entry.getKey());
            }

            Set<ExportedKey> exportedKeys = entry.getValue();
            ExportedKey key = exportedKeys.iterator().next();
            if (key == null) {
                throw new IllegalStateException();
            }

            ExportedKey.KeyData PK = key.getPk();
            ExportedKey.KeyData FK = key.getFk();
            DbEntity pkEntity = map.getDbEntity(PK.getTable());
            DbEntity fkEntity = map.getDbEntity(FK.getTable());
            if (pkEntity == null || fkEntity == null) {
                // Check for existence of this entities were made in creation of ExportedKey
                throw new IllegalStateException();
            }

            List<ColumnPair> columnPairList = new ArrayList<>();
            for (ExportedKey exportedKey : exportedKeys) {
                // Create and append joins
                String pkName = exportedKey.getPk().getColumn();
                String fkName = exportedKey.getFk().getColumn();

                // skip invalid joins...
                DbAttribute pkAtt = pkEntity.getAttribute(pkName);
                if (pkAtt == null) {
                    LOGGER.info("no attribute for declared primary key: " + pkName);
                    continue;
                }

                DbAttribute fkAtt = fkEntity.getAttribute(fkName);
                if (fkAtt == null) {
                    LOGGER.info("no attribute for declared foreign key: " + fkName);
                    continue;
                }

                columnPairList.add(new ColumnPair(pkAtt.getName(), fkAtt.getName()));
            }

            DbJoinCondition dbJoinCondition = columnPairList.size() == 1 ?
                    new SinglePairCondition(columnPairList.get(0)) :
                    new ColumnPairsCondition(columnPairList.toArray(new ColumnPair[0]));
            boolean toDependentPK = isToDependentPK(dbJoinCondition, fkEntity);
            boolean toMany = isToMany(toDependentPK, fkEntity, dbJoinCondition);

            // forwardRelationship is a reference from table with primary key
            // it is what exactly we load from db
            DbJoinDetectedBuilder dbJoinBuilder = new DbJoinDetectedBuilder()
                    .entities(new String[]{pkEntity.getName(), fkEntity.getName()})
                    .names(new String[]{"tempName", "tempName1"})
                    .toManySemantics(toMany ? ToManySemantics.ONE_TO_MANY : ToManySemantics.ONE_TO_ONE)
                    .toDepPkSemantics(toDependentPK ? ToDependentPkSemantics.LEFT :
                            ToDependentPkSemantics.NONE)
                    .condition(dbJoinCondition)
                    .fkNames(FK.getName())
                    .dataMap(map);

            DbJoinDetected dbJoin = dbJoinBuilder.build();
            String forwardRelName = NameBuilder
                    .builder(dbJoin, dbJoin)
                    .baseName(nameGenerator.relationshipName(dbJoin, RelationshipDirection.LEFT))
                    .name();
            String reverseRelName = NameBuilder
                    .builder(dbJoin, dbJoin)
                    .baseName(nameGenerator.relationshipName(dbJoin, RelationshipDirection.RIGHT))
                    .name();

            dbJoin = normalize(dbJoinBuilder
                    .names(new String[]{forwardRelName, reverseRelName}))
                    .build();

            checkAndAddJoin(dbJoin);
        }
    }

    private void checkAndAddJoin(DbJoin dbJoin) {
        DataMap dataMap = dbJoin.getDataMap();
        int leftIndex = RelationshipDirection.LEFT.ordinal();
        int rightIndex = RelationshipDirection.RIGHT.ordinal();
        DbEntity leftEntity = dataMap.getDbEntity(dbJoin.getDbEntities()[leftIndex]);
        DbEntity rightEntity = dataMap.getDbEntity(dbJoin.getDbEntities()[rightIndex]);
        if(!isTableIncluded(dbJoin.getDbEntities()[leftIndex]) ||
                !isTableIncluded(dbJoin.getDbEntities()[rightIndex])) {
            return;
        }

        TableFilter sourceTableFilter = config.getFiltersConfig()
                .tableFilter(leftEntity.getCatalog(), leftEntity.getSchema());

        TableFilter targetTableFilter = config.getFiltersConfig()
                .tableFilter(rightEntity.getCatalog(), rightEntity.getSchema());

        if(!sourceTableFilter.getIncludeTableRelationshipFilter(leftEntity.getName())
                .isIncluded(dbJoin.getNames()[leftIndex])) {
            return;
        }

        if(!targetTableFilter.getIncludeTableRelationshipFilter(rightEntity.getName())
                .isIncluded(dbJoin.getNames()[rightIndex])) {
            return;
        }

        boolean filtersFound = dbJoin.getDbJoinCondition().accept(new JoinVisitor<Boolean>() {

            private boolean checkJoin(String source, String target) {
                return sourceTableFilter.getIncludeTableColumnFilter(
                        leftEntity.getName()).isIncluded(source) &&
                        targetTableFilter.getIncludeTableColumnFilter(
                                rightEntity.getName()).isIncluded(target);
            }

            @Override
            public Boolean visit(ColumnPair columnPair) {
                return checkJoin(columnPair.getLeft(), columnPair.getRight());
            }

            @Override
            public Boolean visit(ColumnPair[] columnPairs) {
                for(ColumnPair columnPair : columnPairs) {
                    if(!checkJoin(columnPair.getLeft(), columnPair.getRight())) {
                        return false;
                    }
                }
                return true;
            }
        });

        if(!filtersFound) {
            return;
        }


        if (delegate.dbJoinLoaded(dbJoin)) {
            dataMap.addJoin(dbJoin);
        }
    }

    public DbJoinDetectedBuilder normalize(DbJoinDetectedBuilder dbJoinBuilder) {
        if(ProjectUtils.needToNormalize(
                dbJoinBuilder.getDbEntities()[0],
                dbJoinBuilder.getDbEntities()[1],
                dbJoinBuilder.getNames()[0],
                dbJoinBuilder.getNames()[1])) {
            return new DbJoinDetectedBuilder()
                    .entities(new String[]{
                            dbJoinBuilder.getDbEntities()[1],
                            dbJoinBuilder.getDbEntities()[0]})
                    .names(new String[]{dbJoinBuilder.getNames()[1], dbJoinBuilder.getNames()[0]})
                    .toManySemantics(dbJoinBuilder.getToManySemantics().getReverse())
                    .toDepPkSemantics(dbJoinBuilder.getToDependentPkSemantics().getReverse())
                    .fkNames(dbJoinBuilder.getFkName())
                    .dataMap(dbJoinBuilder.getDataMap())
                    .condition(ProjectUtils
                            .buildReverseCondition(dbJoinBuilder.getDbJoinCondition()));
        }
        return dbJoinBuilder;
    }

    private boolean isTableIncluded(String name) {
        for(CatalogFilter catalogFilter : config.getFiltersConfig().getCatalogs()) {
            for(SchemaFilter schemaFilter : catalogFilter.schemas) {
                if(schemaFilter.tables.isIncludeTable(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isToMany(boolean toDependentPK, DbEntity fkEntity, DbJoinCondition dbJoinCondition) {
        Integer forwardRelationshipJoinSize = dbJoinCondition.accept(new JoinVisitor<Integer>() {
            @Override
            public Integer visit(ColumnPair columnPair) {
                return 1;
            }

            @Override
            public Integer visit(ColumnPair[] columnPairs) {
                return columnPairs.length;
            }
        });
        return !toDependentPK || fkEntity.getPrimaryKeys().size() != forwardRelationshipJoinSize;
    }

    private boolean isToDependentPK(DbJoinCondition dbJoinCondition,
                                    DbEntity targetEntity) {
        return dbJoinCondition.accept(new JoinVisitor<Boolean>() {

            private DbAttribute getAttribute(DbEntity dbEntity, String name) {
                return dbEntity.getAttribute(name);
            }

            @Override
            public Boolean visit(ColumnPair columnPair) {
                return getAttribute(targetEntity, columnPair.getRight())
                        .isPrimaryKey();
            }

            @Override
            public Boolean visit(ColumnPair[] columnPairs) {
                for(ColumnPair columnPair : columnPairs) {
                    if(!getAttribute(targetEntity, columnPair.getRight())
                            .isPrimaryKey()) {
                        return false;
                    }
                }
                return true;
            }
        });
    }
}
