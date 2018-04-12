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
import org.apache.cayenne.modeler.services.EntitySyncService;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.ObjEntityEvent;
import org.apache.cayenne.dbsync.merge.context.EntityMergeSupport;
import org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.objentity.EntitySyncController;
import org.apache.cayenne.modeler.services.DbEntityService;
import org.apache.cayenne.modeler.undo.DbEntitySyncUndoableEdit;
import org.apache.cayenne.modeler.util.CayenneAction;

import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action that synchronizes all ObjEntities with the current state of the
 * selected DbEntity.
 */
public class DbEntitySyncAction extends CayenneAction {

	@Inject
	public EntitySyncService entitySyncService;

	public static String getActionName() {
		return "Sync Dependent ObjEntities with DbEntity";
	}

	public DbEntitySyncAction() {
		super(getActionName());
	}

	@Override
	public KeyStroke getAcceleratorKey() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_U, Toolkit
				.getDefaultToolkit()
				.getMenuShortcutKeyMask());
	}

	public String getIconName() {
		return "icon-sync.png";
	}

	/**
	 * @see org.apache.cayenne.modeler.util.CayenneAction#performAction(ActionEvent)
	 */
	public void performAction(ActionEvent e) {
		entitySyncService.syncDbEntity();
	}

}

