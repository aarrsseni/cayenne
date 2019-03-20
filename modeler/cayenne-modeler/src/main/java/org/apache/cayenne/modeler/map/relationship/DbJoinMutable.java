package org.apache.cayenne.modeler.map.relationship;

import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinCondition;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.map.relationship.RelationshipDirection;
import org.apache.cayenne.map.relationship.ToDependentPkSemantics;
import org.apache.cayenne.map.relationship.ToManySemantics;
import org.apache.cayenne.project.ProjectUtils;
import org.apache.cayenne.util.XMLEncoder;

public class DbJoinMutable extends DbJoin {

    private static final long serialVersionUID = 4325824892802807593L;

    DbJoinMutable(DbJoinCondition dbJoinCondition,
                  String[] dbEntities,
                  String[] names,
                  ToDependentPkSemantics toDependentPkSemantics,
                  ToManySemantics toManySemantics, DataMap dataMap) {
        super(dbJoinCondition, dbEntities, names, toDependentPkSemantics, toManySemantics, dataMap);
    }

    private void merge() {
        int leftIndex = RelationshipDirection.LEFT.ordinal();
        int rightIndex = RelationshipDirection.RIGHT.ordinal();
        DbRelationship left = dbRelationships[leftIndex];
        DbRelationship right = dbRelationships[rightIndex];

        names[leftIndex] = left.getName();
        names[rightIndex] = right.getName();

        dbEntities[leftIndex] = left.getSourceEntity().getName();
        dbEntities[rightIndex] = right.getSourceEntity().getName();

        toManySemantics = ToManySemantics.getSemantics(left.isToMany(), right.isToMany());
        toDependentPkSemantics = left.isToDependentPK() ?
                ToDependentPkSemantics.LEFT :
                right.isToDependentPK() ?
                        ToDependentPkSemantics.RIGHT :
                        ToDependentPkSemantics.NONE;
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {
        super.encodeAsXML(encoder, delegate);
    }

    public void mergeAndNormalize() {
        merge();
        normalize();
    }

    private void normalize() {
        if(ProjectUtils.needToNormalize(
                dbEntities[0],
                dbEntities[1],
                names[0],
                names[1])) {
            swap(dbRelationships);
            swap(dbEntities);
            swap(names);
            toManySemantics = toManySemantics.getReverse();
            toDependentPkSemantics = toDependentPkSemantics.getReverse();
            dbJoinCondition = ProjectUtils.buildReverseCondition(dbJoinCondition);
            for(DbRelationship relationship : dbRelationships) {
                relationship.setDirection(
                        relationship.getDirection().getOppositeDirection());
                relationship.updatePairsHandler();
            }
        }
    }

    private <T> void swap(T[] swap) {
        T temp = swap[0];
        swap[0] = swap[1];
        swap[1] = temp;
    }
}
