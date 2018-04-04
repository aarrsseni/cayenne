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

package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import org.apache.cayenne.dbsync.merge.context.EntityMergeSupport;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.objentity.EntitySyncController;
import org.apache.cayenne.modeler.services.ObjEntityService;
import org.apache.cayenne.modeler.util.CayenneAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action that synchronizes a given ObjEntity with the current state of the underlying
 * DbEntity.
 * 
 */
public class ObjEntitySyncAction extends CayenneAction {

    @Inject
    public Application application;

    @Inject
    public ObjEntityService objEntityService;

    public static String getActionName() {
        return "Sync ObjEntity with DbEntity";
    }

    public ObjEntitySyncAction() {
        super(getActionName());
    }

    public String getIconName() {
        return "icon-sync.png";
    }

    public KeyStroke getAcceleratorKey() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_U, Toolkit
                .getDefaultToolkit()
                .getMenuShortcutKeyMask());
    }

    /**
     * @see org.apache.cayenne.modeler.util.CayenneAction#performAction(ActionEvent)
     */
    public void performAction(ActionEvent e) {
        syncObjEntity();
    }

    protected void syncObjEntity() {
        ProjectController mediator = getProjectController();
        ObjEntity entity = mediator.getCurrentState().getObjEntity();

        if (entity != null && entity.getDbEntity() != null) {
            EntityMergeSupport merger = new EntitySyncController(Application
                    .getInstance()
                    .getFrameController(), entity).createMerger();

            if (merger == null) {
                return;
            }

            merger.setNameGenerator(new DbEntitySyncAction.PreserveRelationshipNameGenerator());

            if (merger.synchronizeWithDbEntity(entity)) {
                objEntityService.syncObjEntity(entity);
            }
        }
    }
}
