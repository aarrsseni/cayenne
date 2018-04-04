package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;

public interface AttributeService {

    void createDbAttribute();

    void createObjAttribute();

    void createEmbAttribute();

    void fireEmbeddableAttributeEvent(Object src, ProjectController mediator, Embeddable embeddable, EmbeddableAttribute attr);

    void fireDbAttributeEvent(Object src, ProjectController mediator, DataMap map, DbEntity dbEntity, DbAttribute attr);

    void fireObjAttributeEvent(Object src, ProjectController mediator, DataMap map, ObjEntity objEntity, ObjAttribute attr);

    void createEmbAttribute(Embeddable embeddable, EmbeddableAttribute attr);

    void createObjAttribute(DataMap map, ObjEntity objEntity, ObjAttribute attr);

    void createDbAttribute(DataMap map, DbEntity dbEntity, DbAttribute attr);

    void removeDbAttributes(DataMap dataMap, DbEntity entity, DbAttribute[] attribs);

    void removeObjAttributes(ObjEntity entity, ObjAttribute[] attribs);

    void removeObjAttributes(ObjAttribute[] attribs);

    void removeEmbeddableAttributes(Embeddable embeddable, EmbeddableAttribute[] attrs);
}
