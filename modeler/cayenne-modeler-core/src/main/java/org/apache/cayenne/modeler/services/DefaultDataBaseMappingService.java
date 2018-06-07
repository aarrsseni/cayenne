package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.event.DbRelationshipEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DbJoin;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultDataBaseMappingService implements DataBaseMappingService {

    @Inject
    public ProjectController projectController;

    @Override
    public void handleNameUpdate(DbRelationship relationship, String userInputName) {
        if(Util.nullSafeEquals(relationship.getName(), userInputName)) {
            return;
        }

        String sourceEntityName = NameBuilder
                .builder(relationship, relationship.getSourceEntity())
                .baseName(userInputName)
                .name();

        if (Util.nullSafeEquals(sourceEntityName, relationship.getName())) {
            return;
        }
        String oldName = relationship.getName();
        relationship.setName(sourceEntityName);
//        undo.addNameUndo(relationship, oldName, sourceEntityName);

        projectController.fireEvent(
                new DbRelationshipEvent(this, relationship, relationship.getSourceEntity(), oldName));
    }

    @Override
    public void save(DbRelationship dbRelationship, DbRelationship reverseRelationship, String reverseName) {
        if (dbRelationship.isToDependentPK() && !dbRelationship.isValidForDepPk()) {
            dbRelationship.setToDependentPK(false);
        }
        if (true) {
            if (reverseRelationship == null) {
                reverseRelationship = new DbRelationship();
                reverseRelationship.setName(NameBuilder
                        .builder(reverseRelationship, dbRelationship.getTargetEntity())
                        .baseName(reverseName)
                        .name());

                reverseRelationship.setSourceEntity(dbRelationship.getTargetEntity());
                reverseRelationship.setTargetEntityName(dbRelationship.getSourceEntity());
                reverseRelationship.setToMany(!dbRelationship.isToMany());
                dbRelationship.getTargetEntity().addRelationship(reverseRelationship);

                // fire only if the relationship is to the same entity...
                // this is needed to update entity view...
                if (dbRelationship.getSourceEntity() == dbRelationship.getTargetEntity()) {
                    projectController.fireEvent(
                            new DbRelationshipEvent(
                                    this,
                                    reverseRelationship,
                                    reverseRelationship.getSourceEntity(),
                                    MapEvent.ADD));
                }
            } else {
                handleNameUpdate(reverseRelationship, reverseName);
            }


            reverseRelationship.setJoins(getReverseJoins(dbRelationship, reverseRelationship));

            // check if joins map to a primary key of this entity
            if (!dbRelationship.isToDependentPK() && reverseRelationship.isValidForDepPk()) {
                reverseRelationship.setToDependentPK(true);
            }
        }
        projectController.fireEvent(
                new DbRelationshipEvent(this, dbRelationship, dbRelationship.getSourceEntity()));
    }

    private Collection<DbJoin> getReverseJoins(DbRelationship dbRelationship, DbRelationship reverseRelationship) {
        Collection<DbJoin> joins = dbRelationship.getJoins();

        if ((joins == null) || (joins.size() == 0)) {
            return Collections.emptyList();
        }

        List<DbJoin> reverseJoins = new ArrayList<>(joins.size());

        // Loop through the list of attribute pairs, create reverse pairs
        // and put them to the reverse list.
        for (DbJoin pair : joins) {
            DbJoin reverseJoin = pair.createReverseJoin();

            // since reverse relationship is not yet initialized,
            // reverse join will not have it set automatically
            reverseJoin.setRelationship(reverseRelationship);
            reverseJoins.add(reverseJoin);
        }

        return reverseJoins;
    }
}
