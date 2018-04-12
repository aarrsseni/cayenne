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
package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateAttributeEvent;
import org.apache.cayenne.modeler.event.listener.CreateAttributeListener;
import org.apache.cayenne.modeler.undo.CreateAttributeUndoableEdit;
import org.apache.cayenne.modeler.undo.CreateEmbAttributeUndoableEdit;

/**
 * @since 4.1
 */
public class CreateAttributeActionListener implements CreateAttributeListener {

    private static final String EMBEDDABLE_ATTR = "EmbeddableAttr";
    private static final String DB_ATTR = "DbAttr";
    private static final String OBJ_ATTR = "ObjAttr";

    @Override
    public void addAttr(CreateAttributeEvent e) {
        switch (e.getType()) {
            case EMBEDDABLE_ATTR:
                Application.getInstance().getUndoManager().addEdit(
                        new CreateEmbAttributeUndoableEdit(e.getEmbeddable(), new EmbeddableAttribute[]{e.getEmbeddableAttribute()}));
                break;
            case OBJ_ATTR:
                Application.getInstance().getUndoManager().addEdit(
                        new CreateAttributeUndoableEdit((DataChannelDescriptor) Application.getInstance().getProjectController().getProject().getRootNode(),
                                Application.getInstance().getProjectController().getCurrentState().getDataMap(), e.getObjEntity(), e.getObjAttribute()));
                break;
            case DB_ATTR:
                Application.getInstance().getUndoManager().addEdit(
                        new CreateAttributeUndoableEdit((DataChannelDescriptor) Application.getInstance().getProjectController().getProject().getRootNode(),
                                Application.getInstance().getProjectController().getCurrentState().getDataMap(), e.getDbEntity(), e.getDbAttribute()));
                break;
            default:
                throw new IllegalArgumentException("No attributes for " + e.getType());
        }

    }

}
