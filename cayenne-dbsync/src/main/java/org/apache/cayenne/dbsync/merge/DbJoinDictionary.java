package org.apache.cayenne.dbsync.merge;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import org.apache.cayenne.dbsync.reverse.filters.CatalogFilter;
import org.apache.cayenne.dbsync.reverse.filters.FiltersConfig;
import org.apache.cayenne.dbsync.reverse.filters.PatternFilter;
import org.apache.cayenne.dbsync.reverse.filters.SchemaFilter;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.relationship.ColumnPair;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.JoinVisitor;

public class DbJoinDictionary extends MergerDictionary<DbJoin> {

    private final DataMap container;

    private final FiltersConfig filtersConfig;

    DbJoinDictionary(DataMap container, FiltersConfig filtersConfig) {
        this.container = container;
        this.filtersConfig = filtersConfig;
    }

    @Override
    String getName(DbJoin entity) {
        return new Signature(entity).getName();
    }

    @Override
    Collection<DbJoin> getAll() {
        if(filtersConfig == null) {
            return container.getDbJoinList();
        }

        Collection<DbJoin> existingFiltered = new LinkedList<>();

        for(DbJoin dbJoin : container.getDbJoinList()) {
            String srcEntity = dbJoin.getDbEntities()[0];
            String targetEntity = dbJoin.getDbEntities()[1];
            for(CatalogFilter catalogFilter : filtersConfig.getCatalogs()) {
                for(SchemaFilter schemaFilter : catalogFilter.schemas) {
                    if(schemaFilter.tables.isIncludeTable(srcEntity) &&
                            schemaFilter.tables.isIncludeTable(targetEntity)) {
                        PatternFilter srcFilter = schemaFilter.tables
                                .getIncludeTableRelationshipFilter(dbJoin.getDbEntities()[0]);
                        PatternFilter targetFilter = schemaFilter.tables
                                .getIncludeTableRelationshipFilter(dbJoin.getDbEntities()[1]);
                        String[] names = dbJoin.getNames();
                        if(srcFilter.isIncluded(names[0]) || targetFilter.isIncluded(names[1])) {
                            existingFiltered.add(dbJoin);
                        }
                    }
                }
            }
        }

        return existingFiltered;
    }



    private static class Signature {
        private final DbJoin dbJoin;

        private String[] joinSignature;

        private Signature(DbJoin dbJoin) {
            this.dbJoin = dbJoin;
            build();
        }

        public String getName() {
            if(joinSignature.length == 0) {
                return "";
            }
            String name = joinSignature[0];
            for(int i=1; i<joinSignature.length; i++) {
                name += "|" + joinSignature[i];
            }
            return name;
        }

        private void build() {
            String[] dbEntities = dbJoin.getDbEntities();
            joinSignature = dbJoin.getDbJoinCondition().accept(
                    new TreeSetJoinVisitor(dbEntities[0], dbEntities[1]))
                    .toArray(new String[0]);
        }

        private class TreeSetJoinVisitor implements JoinVisitor<TreeSet<String>> {

            private String left;
            private String right;
            private int compare;

            public TreeSetJoinVisitor(String left, String right) {
                this.left = left;
                this.right = right;
                this.compare = left.compareTo(right);
            }

            private String buildSignature(String source, String target) {
                if(compare < 0) {
                    return left + "." + source + ">" + right + "." + target;
                } else {
                    return right + "." + target + ">" + left + "." + source;
                }
            }

            @Override
            public TreeSet<String> visit(ColumnPair columnPair) {
                TreeSet<String> joins = new TreeSet<>();
                joins.add(buildSignature(columnPair.getLeft(), columnPair.getRight()));
                return joins;
            }

            @Override
            public TreeSet<String> visit(ColumnPair[] columnPairs) {
                Arrays.sort(columnPairs, (o1, o2) -> compare < 0 ? o1.getLeft().compareTo(o2.getLeft()) :
                        o1.getRight().compareTo(o2.getRight()));
                TreeSet<String> joins = new TreeSet<>();
                for (ColumnPair columnPair : columnPairs) {
                    joins.add(buildSignature(columnPair.getLeft(), columnPair.getRight()));
                }
                return joins;
            }
        }
    }
}
