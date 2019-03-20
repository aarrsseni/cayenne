package org.apache.cayenne.map.relationship;

import java.io.Serializable;

import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.MappingNamespace;
import org.apache.cayenne.util.XMLEncoder;
import org.apache.cayenne.util.XMLSerializable;

public class DbJoin implements XMLSerializable, Serializable, ConfigurationNode {

    protected DbRelationship[] dbRelationships;

    protected DbJoinCondition dbJoinCondition;

    protected String[] dbEntities;
    protected String[] names;
    protected ToDependentPkSemantics toDependentPkSemantics;
    protected ToManySemantics toManySemantics;

    protected DataMap dataMap;

    protected DbJoin(DbJoinCondition dbJoinCondition,
                     String[] dbEntities,
                     String[] names,
                     ToDependentPkSemantics toDependentPkSemantics,
                     ToManySemantics toManySemantics,
                     DataMap dataMap) {
        this.dbJoinCondition = dbJoinCondition;
        this.dbRelationships = new DbRelationship[2];
        this.dbEntities = dbEntities;
        this.names = names;
        this.toDependentPkSemantics = toDependentPkSemantics;
        this.toManySemantics = toManySemantics;
        this.dataMap = dataMap;
    }

    public void compile(MappingNamespace mappingNamespace) {
        createRelationship(mappingNamespace, RelationshipDirection.LEFT);
        createRelationship(mappingNamespace, RelationshipDirection.RIGHT);
    }

    protected void createRelationship(MappingNamespace mappingNamespace,
                                    RelationshipDirection relationshipDirection) {
        int index = relationshipDirection.ordinal();
        DbEntity srcEntity = mappingNamespace.getDbEntity(dbEntities[index]);
        dbRelationships[index] = new DbRelationship(
                names[index],
                srcEntity,
                dbEntities[1 - index],
                toManySemantics.isToMany(relationshipDirection),
                toDependentPkSemantics.isToDepPk(relationshipDirection),
                this,
                relationshipDirection);
        if(names[index] == null) {
            dbRelationships[index].setName("runtimeRelationship" +
                    EntityResolver.incrementer.getAndIncrement());
            dbRelationships[index].setRuntime(true);
            return;
        }
        srcEntity.addRelationship(dbRelationships[index]);
    }

    public void setCondition(DbJoinCondition dbJoinCondition) {
        this.dbJoinCondition = dbJoinCondition;
    }

    public DbRelationship getRelationhsip() {
        return dbRelationships[RelationshipDirection.LEFT.ordinal()];
    }

    public DbJoinCondition getDbJoinCondition() {
        return dbJoinCondition;
    }

    DbRelationship getReverseRelationship(RelationshipDirection direction) {
        return dbRelationships[direction.getOppositeDirection().ordinal()];
    }

    DbEntity getTargetEntity(RelationshipDirection direction) {
        return dbRelationships[direction.getOppositeDirection().ordinal()]
                .getSourceEntity();
    }

    public DbRelationship getRelationship(RelationshipDirection relationshipDirection) {
        return dbRelationships[relationshipDirection.ordinal()];
    }

    public String[] getDbEntities() {
        return dbEntities;
    }

    public String[] getNames() {
        return names;
    }

    public ToDependentPkSemantics getToDependentPkSemantics() {
        return toDependentPkSemantics;
    }

    public ToManySemantics getToManySemantics() {
        return toManySemantics;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public int compareTo(DbJoin dbJoin) {
        String o1Entity = getDbEntities()[0];
        String o2Entity = dbJoin.getDbEntities()[0];
        String o1Name = getNames()[0];
        String o2Name = dbJoin.getNames()[0];
        int compare = o1Entity.compareTo(o2Entity);
        return compare == 0 && o1Name != null && o2Name != null ?
                o1Name.compareTo(o2Name) :
                compare;
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {
        encoder.start("db-join")
                    .attribute("toMany", toManySemantics.name())
                    .attribute("toDependentPK", toDependentPkSemantics.name())
                .start("left")
                    .attribute("entity", dbEntities[0])
                    .attribute("name", names[0])
                .end()
                .start("right")
                    .attribute("entity", dbEntities[1])
                    .attribute("name", names[1])
                .end();

        dbJoinCondition.accept(new JoinVisitor<Void>() {

            @Override
            public Void visit(ColumnPair columnPair) {
                encoder.start("db-join-condition");
                encoder.start("column-pair")
                        .attribute("left", columnPair.getLeft())
                        .attribute("right", columnPair.getRight())
                        .end();
                encoder.end();
                return null;
            }

            @Override
            public Void visit(ColumnPair[] columnPairs) {
                encoder.start("db-join-condition");
                for(ColumnPair columnPair : columnPairs) {
                    encoder.start("column-pair")
                            .attribute("left", columnPair.getLeft())
                            .attribute("right", columnPair.getRight())
                            .end();
                }
                encoder.end();
                return null;
            }
        });

        delegate.visitDbJoin(this);
        encoder.end();
    }

    @Override
    public <T> T acceptVisitor(ConfigurationNodeVisitor<T> visitor) {
        return visitor.visitDbJoin(this);
    }

    public String toString() {
        StringBuilder res = new StringBuilder("DbJoin : ");
        res.append(toManySemantics.name());

        return dbJoinCondition.accept(new JoinVisitor<StringBuilder>() {

            private StringBuilder build(ColumnPair columnPair) {
                return res.append(" (")
                        .append(dbEntities[RelationshipDirection.LEFT.ordinal()])
                        .append(".").append(columnPair.getLeft())
                        .append(", ")
                        .append(dbEntities[RelationshipDirection.RIGHT.ordinal()])
                        .append(".").append(columnPair.getRight()).append(")");
            }

            @Override
            public StringBuilder visit(ColumnPair columnPair) {
                return build(columnPair);
            }

            @Override
            public StringBuilder visit(ColumnPair[] columnPairs) {
                for(ColumnPair columnPair : columnPairs) {
                    build(columnPair);
                }
                return res;
            }
        }).toString();
    }
}
