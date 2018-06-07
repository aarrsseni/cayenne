package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.DbRelationship;

public interface DataBaseMappingService {
    void handleNameUpdate(DbRelationship dbRelationship, String name);

    void save(DbRelationship relationship, DbRelationship reverseRelationship, String reverserName);
}
