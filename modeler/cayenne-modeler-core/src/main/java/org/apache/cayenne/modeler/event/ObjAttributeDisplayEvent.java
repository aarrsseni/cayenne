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

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.modeler.event.listener.ObjAttributeDisplayListener;

import java.util.EventListener;

/**
 * @since 4.1
 */
public class ObjAttributeDisplayEvent extends AttributeDisplayEvent{

    public ObjAttributeDisplayEvent(Object src, Attribute attribute, Entity entity, DataMap dataMap, DataChannelDescriptor domain) {
        super(src, attribute, entity, dataMap, domain);
    }

    public ObjAttributeDisplayEvent(Object src, Attribute[] attributes, Entity entity, DataMap dataMap, DataChannelDescriptor domain) {
        super(src, attributes, entity, dataMap, domain);
    }

    public Class<? extends EventListener> getEventListener() {
        return ObjAttributeDisplayListener.class;
    }
}
