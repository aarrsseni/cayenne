/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.modeler.services;

import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;

/**
 * @since 4.1
 */
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

    RemoveServiceStatus isRemove();

    void remove();
}
