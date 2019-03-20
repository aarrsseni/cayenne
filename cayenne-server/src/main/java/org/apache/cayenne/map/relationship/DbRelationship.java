package org.apache.cayenne.map.relationship;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.Relationship;
import org.apache.cayenne.util.XMLEncoder;

public class DbRelationship extends Relationship implements ConfigurationNode {

    private DbJoin dbJoin;
    private RelationshipDirection direction;
    private ColumnPairsHandler columnPairsHandler;
    protected boolean toDependentPK;

    public DbRelationship() {
        super();
    }

    public DbRelationship(String name) {
        super(name);
    }

    public DbRelationship(String name,
                          DbEntity sourceEntity,
                          String targetEntityName,
                          boolean toMany,
                          boolean toDepPk,
                          DbJoin dbJoin,
                          RelationshipDirection direction) {
        this.name = name;
        this.sourceEntity = sourceEntity;
        this.targetEntityName = targetEntityName;
        this.toMany = toMany;
        this.toDependentPK = toDepPk;
        this.dbJoin = dbJoin;
        this.direction = direction;
        this.columnPairsHandler = dbJoin.getDbJoinCondition().accept(new ColumnPairsHandlerJoinVisitor());
    }

    public <T> T accept(DirectionalJoinVisitor<T> joinContentVisitor) {
        return columnPairsHandler.handle(joinContentVisitor);
    }

    @Override
    public DbEntity getSourceEntity() {
        return (DbEntity) super.getSourceEntity();
    }

    @Override
    public DbEntity getTargetEntity() {
        return dbJoin.getTargetEntity(direction);
    }

    public String getTargetEntityName() {
        return getTargetEntity().getName();
    }

    @Override
    public DbRelationship getReverseRelationship() {
        return dbJoin.getReverseRelationship(direction);
    }

    @Override
    public boolean isMandatory() {
        boolean mandatoryNotFound = accept(new DirectionalJoinVisitor<Boolean>() {
            @Override
            public Boolean visit(DbAttribute[] source, DbAttribute[] target) {
                for(DbAttribute dbAttribute : source) {
                    if(dbAttribute.isMandatory()) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public Boolean visit(DbAttribute source, DbAttribute target) {
                return !source.isMandatory();
            }
        });
        return !mandatoryNotFound;
    }

    public boolean isToDependentPK() {
        return toDependentPK;
    }

    public void setToDependentPK(boolean toDependentPK) {
        this.toDependentPK = toDependentPK;
    }

    public void setToMany(boolean toMany) {
        this.toMany = toMany;
    }

    public boolean isToPK() {
        return accept(new DirectionalJoinVisitor<Boolean>() {
            @Override
            public Boolean visit(DbAttribute[] source, DbAttribute[] target) {
                for (DbAttribute attribute : target) {
                    if (attribute == null) {
                        return false;
                    }

                    if (!attribute.isPrimaryKey()) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public Boolean visit(DbAttribute source, DbAttribute target) {
                if (target == null) {
                    return false;
                }
                return target.isPrimaryKey();
            }
        });
    }

    public boolean isFromPK() {
        return accept(new DirectionalJoinVisitor<Boolean>() {
            @Override
            public Boolean visit(DbAttribute[] src, DbAttribute[] target) {
                for (DbAttribute attribute : src) {
                    if (attribute == null) {
                        return false;
                    }

                    if (attribute.isPrimaryKey()) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public Boolean visit(DbAttribute src, DbAttribute target) {
                if (src == null) {
                    return false;
                }

                return src.isPrimaryKey();
            }
        });
    }

    public boolean isToMasterPK() {
        return !(isToMany() || isToDependentPK()) &&
                getReverseRelationship().isToDependentPK();
    }

    public boolean isSourceIndependentFromTargetChange() {
        // note - call "isToPK" at the end of the chain, since
        // if it is to a dependent PK, we still should return true...
        return isToMany() || isToDependentPK() || !isToPK();
    }

    public Map<String, Object> srcFkSnapshotWithTargetSnapshot(Map<String, Object> targetSnapshot) {

        if (isToMany()) {
            throw new CayenneRuntimeException("Only 'to one' relationships support this method.");
        }

        return srcSnapshotWithTargetSnapshot(targetSnapshot);
    }

    private Map<String, Object> srcSnapshotWithTargetSnapshot(Map<String, Object> targetSnapshot) {
        return accept(new MapDirectionalJoinVisitorSrc(targetSnapshot));
    }

    public Collection<DbAttribute> getTargetAttributes() {
        return mapJoinsToAttributes(false);
    }

    /**
     * Returns a Collection of source attributes.
     *
     * @since 1.1
     */
    public Collection<DbAttribute> getSourceAttributes() {
        return mapJoinsToAttributes(true);
    }

    private Collection<DbAttribute> mapJoinsToAttributes(boolean getSource) {
        return accept(new DirectionalJoinVisitor<List<DbAttribute>>() {
            @Override
            public List<DbAttribute> visit(DbAttribute[] src, DbAttribute[] target) {
                return getSource ? Arrays.asList(src) : Arrays.asList(target);
            }

            @Override
            public List<DbAttribute> visit(DbAttribute src, DbAttribute target) {
                return Collections.singletonList(getSource ? src : target);
            }
        });
    }

    public Map<String, Object> targetPkSnapshotWithSrcSnapshot(Map<String, Object> srcSnapshot) {

        if (isToMany()) {
            throw new CayenneRuntimeException("Only 'to one' relationships support this method.");
        }

        return accept(new MapDirectionalJoinVisitorTarget(srcSnapshot));
    }

    public boolean isValidForDepPk() {
        // handle case with no joins

        return accept(new DirectionalJoinVisitor<Boolean>() {
            @Override
            public Boolean visit(DbAttribute[] source, DbAttribute[] target) {
                int length = source.length;
                for(int i = 0; i < length; i++) {
                    DbAttribute targetAttr = target[i];
                    DbAttribute sourceAttr = source[i];

                    if (targetAttr != null && !targetAttr.isPrimaryKey() ||
                            sourceAttr != null && !sourceAttr.isPrimaryKey()) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public Boolean visit(DbAttribute source, DbAttribute target) {
                return (target == null || target.isPrimaryKey()) &&
                        (source == null || source.isPrimaryKey());
            }
        });
    }

    public DbJoin getDbJoin() {
        return dbJoin;
    }

    public RelationshipDirection getDirection() {
        return direction;
    }

    public void setDirection(RelationshipDirection direction) {
        this.direction = direction;
    }

    public void updatePairsHandler() {
        this.columnPairsHandler = dbJoin.getDbJoinCondition().accept(new ColumnPairsHandlerJoinVisitor());
    }

    @Override
    public <T> T acceptVisitor(ConfigurationNodeVisitor<T> visitor) {
        return visitor.visitDbRelationship(this);
    }

    public String toString() {
        StringBuilder res = new StringBuilder("Db Relationship : ");
        res.append(toMany ? "toMany" : "toOne ");

        String sourceEntityName = sourceEntity.getName();
        return accept(new DirectionalJoinVisitor<StringBuilder>() {
            @Override
            public StringBuilder visit(DbAttribute[] src, DbAttribute[] target) {
                for(int i = 0; i < src.length; i++) {
                    res.append(" (")
                            .append(sourceEntityName).append(".")
                            .append(src[i].getName()).append(", ")
                            .append(getTargetEntity().getName()).append(".")
                            .append(target[i].getName()).append(")");
                }

                return res;
            }

            @Override
            public StringBuilder visit(DbAttribute src, DbAttribute target) {
                return res.append(" (").append(sourceEntityName)
                        .append(".").append(src.getName())
                        .append(", ").append(getTargetEntity().getName()).append(".")
                        .append(target.getName()).append(")");
            }
        }).toString();
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {}

    private static class MapDirectionalJoinVisitorTarget implements DirectionalJoinVisitor<Map<String, Object>> {
        private final Map<String, Object> srcSnapshot;

        MapDirectionalJoinVisitorTarget(Map<String, Object> srcSnapshot) {
            this.srcSnapshot = srcSnapshot;
        }

        @Override
        public Map<String, Object> visit(DbAttribute[] src, DbAttribute[] target) {
            int size = src.length;
            int foundNulls = 0;
            Map<String, Object> resMap = new HashMap<>(size * 2);
            for(int i = 0; i < size; i++) {
                Object val = srcSnapshot.get(src[i].getName());
                if (val == null) {
                    // some keys may be nulls and some not in case of multi-key
                    // relationships where PK and FK partially overlap (see
                    // CAY-284)
                    if (!src[i].isMandatory()) {
                        return null;
                    }
                    foundNulls++;
                } else {
                    resMap.put(target[i].getName(), val);
                }
            }

            if(foundNulls == 0) {
                return resMap;
            } else if(foundNulls == size) {
                return null;
            } else {
                throw new CayenneRuntimeException("Some parts of FK are missing in snapshot, relationship: %s", this);
            }
        }

        @Override
        public Map<String, Object> visit(DbAttribute source, DbAttribute target) {
            Object val = srcSnapshot.get(source.getName());
            return val != null ?
                    Collections.singletonMap(target.getName(), val) :
                    null;
        }
    }

    private static class MapDirectionalJoinVisitorSrc implements DirectionalJoinVisitor<Map<String, Object>> {
        private final Map<String, Object> targetSnapshot;

        public MapDirectionalJoinVisitorSrc(Map<String, Object> targetSnapshot) {
            this.targetSnapshot = targetSnapshot;
        }

        @Override
        public Map<String, Object> visit(DbAttribute[] src, DbAttribute[] target) {
            int size = src.length;
            Map<String, Object> idMap = new HashMap<>(size * 2);
            for(int i = 0; i < size; i++) {
                Object val = targetSnapshot.get(target[i].getName());
                idMap.put(src[i].getName(), val);
            }

            return idMap;
        }

        @Override
        public Map<String, Object> visit(DbAttribute src, DbAttribute target) {
            return Collections.singletonMap(src.getName(),
                    targetSnapshot.get(target.getName()));
        }
    }

    private class ColumnPairsHandlerJoinVisitor implements JoinVisitor<ColumnPairsHandler> {

        private DbAttribute getAttribute(Entity entity, String attrName) {
            return (DbAttribute) entity.getAttribute(attrName);
        }

        @Override
        public ColumnPairsHandler visit(ColumnPair columnPair) {
            DbEntity targetEntity = sourceEntity.getDataMap().getDbEntity(targetEntityName);
            DbAttribute srcAttr, targetAttr;
            if(direction == RelationshipDirection.LEFT) {
                srcAttr = getAttribute(sourceEntity, columnPair.getLeft());
                targetAttr = getAttribute(targetEntity, columnPair.getRight());
            } else {
                srcAttr = getAttribute(sourceEntity, columnPair.getRight());
                targetAttr = getAttribute(targetEntity, columnPair.getLeft());
            }
            return new SingleColumnPairHandler(srcAttr, targetAttr);
        }

        @Override
        public ColumnPairsHandler visit(ColumnPair[] columnPairs) {
            DbEntity targetEntity = sourceEntity.getDataMap().getDbEntity(targetEntityName);
            DbAttribute[] src = new DbAttribute[columnPairs.length];
            DbAttribute[] target = new DbAttribute[columnPairs.length];
            for(int i = 0; i < columnPairs.length; i++) {
                if(direction == RelationshipDirection.LEFT) {
                    src[i] = getAttribute(sourceEntity, columnPairs[i].getLeft());
                    target[i] = getAttribute(targetEntity, columnPairs[i].getRight());
                } else {
                    src[i] = getAttribute(sourceEntity, columnPairs[i].getRight());
                    target[i] = getAttribute(targetEntity, columnPairs[i].getLeft());
                }
            }
            return new MultiColumnPairsHandler(src, target);
        }
    }
}
