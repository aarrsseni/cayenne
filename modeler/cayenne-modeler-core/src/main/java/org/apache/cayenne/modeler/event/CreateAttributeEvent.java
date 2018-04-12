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

import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.event.listener.CreateAttributeListener;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @since 4.1
 */
public class CreateAttributeEvent extends EventObject{

    private Embeddable embeddable;
    private EmbeddableAttribute embeddableAttribute;
    private String type;

    private DbAttribute dbAttribute;
    private DbEntity dbEntity;

    private ObjAttribute objAttribute;
    private ObjEntity objEntity;

    private static final String EMBEDDABLE_ATTR = "EmbeddableAttr";
    private static final String DB_ATTR = "DbAttr";
    private static final String OBJ_ATTR = "ObjAttr";

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateAttributeEvent(Object source) {
        super(source);
    }

    public CreateAttributeEvent(Object source, Embeddable embeddable, EmbeddableAttribute embeddableAttribute) {
        this(source);
        this.embeddable = embeddable;
        this.embeddableAttribute = embeddableAttribute;
        this.type = EMBEDDABLE_ATTR;
    }

    public CreateAttributeEvent(Object source, DbEntity dbEntity, DbAttribute dbAttribute) {
        this(source);
        this.dbEntity = dbEntity;
        this.dbAttribute = dbAttribute;
        this.type = DB_ATTR;
    }

    public CreateAttributeEvent(Object source, ObjEntity objEntity, ObjAttribute objAttribute) {
        this(source);
        this.objEntity = objEntity;
        this.objAttribute = objAttribute;
        this.type = OBJ_ATTR;
    }

    public Embeddable getEmbeddable() {
        return embeddable;
    }

    public EmbeddableAttribute getEmbeddableAttribute() {
        return embeddableAttribute;
    }

    public String getType() {
        return type;
    }

    public DbAttribute getDbAttribute() {
        return dbAttribute;
    }

    public DbEntity getDbEntity() {
        return dbEntity;
    }

    public ObjAttribute getObjAttribute() {
        return objAttribute;
    }

    public ObjEntity getObjEntity() {
        return objEntity;
    }

    public Class<? extends EventListener> getEventListener() {
        return CreateAttributeListener.class;
    }
}
