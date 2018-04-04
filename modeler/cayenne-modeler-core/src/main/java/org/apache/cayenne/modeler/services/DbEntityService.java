package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.modeler.ProjectController;

public interface DbEntityService {
    void createDbEntity();

    /**
     * Constructs and returns a new DbEntity. Entity returned is added to the
     * DataMap.
     */
    void createEntity(DataMap map, DbEntity entity);

    void removeDbEntity(DataMap map, DbEntity ent);

    void fireDbEntityEvent(Object src, ProjectController mediator, DbEntity entity);

    void syncDbEntity();
}
