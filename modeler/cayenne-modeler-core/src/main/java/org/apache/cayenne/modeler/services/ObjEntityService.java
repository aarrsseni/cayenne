package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;

public interface ObjEntityService {
    void createObjEntity();

    void createObjEntity(DataMap dataMap, ObjEntity entity);

    void syncObjEntity(ObjEntity objEntity);

    void removeObjEntity(DataMap map, ObjEntity entity);

    void fireObjEntityEvent(Object src, ProjectController mediator, DataMap dataMap, ObjEntity entity);
}
