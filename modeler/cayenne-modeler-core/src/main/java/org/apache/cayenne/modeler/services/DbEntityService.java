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
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.ProjectController;

import java.util.Collection;

/**
 * @since 4.1
 */
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
