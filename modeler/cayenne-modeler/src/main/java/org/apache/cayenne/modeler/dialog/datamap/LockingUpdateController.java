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

package org.apache.cayenne.modeler.dialog.datamap;

import org.apache.cayenne.configuration.event.ObjAttributeEvent;
import org.apache.cayenne.configuration.event.ObjEntityEvent;
import org.apache.cayenne.configuration.event.ObjRelationshipEvent;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.util.CayenneController;
import org.apache.cayenne.swing.BindingBuilder;

import java.awt.Component;

public class LockingUpdateController extends CayenneController {

    protected LockingUpdateView view;
    protected DataMap dataMap;
    protected ProjectController projectController;

    public LockingUpdateController(ProjectController projectController, DataMap dataMap) {
        super(Application.getInstance().getFrameController());
        this.projectController = projectController;
        this.dataMap = dataMap;
    }

    public void startup() {

        view = new LockingUpdateView();

        boolean on = dataMap.getDefaultLockType() == ObjEntity.LOCK_TYPE_OPTIMISTIC;
        view.setTitle(on ? "Enable Optimistic Locking" : "Disable Optimistic Locking");

        initBindings();

        view.pack();
        view.setModal(true);
        centerView();
        makeCloseableOnEscape();
        view.setVisible(true);
    }

    public Component getView() {
        return view;
    }

    protected void initBindings() {
        BindingBuilder builder = new BindingBuilder(
                getApplication().getBindingFactory(),
                this);

        builder.bindToAction(view.getCancelButton(), "cancelAction()");
        builder.bindToAction(view.getUpdateButton(), "updateAction()");
    }

    public void cancelAction() {
        if (view != null) {
            view.dispose();
        }
    }

    public void updateAction() {
        int defaultLockType = dataMap.getDefaultLockType();
        boolean on = defaultLockType == ObjEntity.LOCK_TYPE_OPTIMISTIC;

        boolean updateEntities = view.getEntities().isSelected();
        boolean updateAttributes = view.getAttributes().isSelected();
        boolean updateRelationships = view.getRelationships().isSelected();

        for (ObjEntity entity : dataMap.getObjEntities()) {
            if (updateEntities && defaultLockType != entity.getDeclaredLockType()) {
                entity.setDeclaredLockType(defaultLockType);
                projectController.fireEvent(new ObjEntityEvent(this, entity));
            }

            if (updateAttributes) {
                for (ObjAttribute a : entity.getAttributes()) {
                    if (a.isUsedForLocking() != on) {
                        a.setUsedForLocking(on);
                        projectController.fireEvent(new ObjAttributeEvent(this, a, entity));
                    }
                }
            }

            if (updateRelationships) {
                for (ObjRelationship r : entity.getRelationships()) {
                    if (r.isUsedForLocking() != on) {
                        r.setUsedForLocking(on);
                        projectController.fireEvent(new ObjRelationshipEvent(
                                this,
                                r,
                                entity));
                    }
                }
            }
        }

        if (view != null) {
            view.dispose();
        }
    }
}
