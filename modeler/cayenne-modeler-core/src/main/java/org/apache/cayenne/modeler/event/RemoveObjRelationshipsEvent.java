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
package org.apache.cayenne.modeler.event;

import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.modeler.event.listener.RemoveObjRelationshipsListener;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @since 4.1
 */
public class RemoveObjRelationshipsEvent extends EventObject{

    private ObjEntity entity;
    private ObjRelationship[] rels;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @param entity
     * @param rels
     * @throws IllegalArgumentException if source is null.
     */
    public RemoveObjRelationshipsEvent(Object source, ObjEntity entity, ObjRelationship[] rels) {
        super(source);
        this.entity = entity;
        this.rels = rels;
    }


    public ObjEntity getEntity() {
        return entity;
    }

    public ObjRelationship[] getRels() {
        return rels;
    }

    public Class<? extends EventListener> getEventListener() {
        return RemoveObjRelationshipsListener.class;
    }
}
