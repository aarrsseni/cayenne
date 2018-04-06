package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;

import java.util.Collection;

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

    /**
     * This method works only for case when all inherited entities bound to same DbEntity
     * if this will ever change some additional checks should be performed.
     */
    void filterInheritedEntities(Collection<ObjEntity> entities);
}
