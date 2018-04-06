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
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.ObjEntityEvent;
import org.apache.cayenne.dbsync.merge.context.EntityMergeSupport;
import org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

/**
 * Action that synchronizes all ObjEntities with the current state of the
 * selected DbEntity.
 */
public class DbEntitySyncAction extends CayenneAction {

	@Inject
	public Application application;

	@Inject
	public DbEntityService dbEntityService;

	@Inject
	public ProjectController projectController;

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
		syncDbEntity();
	}

	protected void syncDbEntity() {

		DbEntity dbEntity = projectController.getCurrentState().getDbEntity();

		if (dbEntity != null) {

			Collection<ObjEntity> entities = dbEntity.getDataMap().getMappedEntities(dbEntity);
			if (entities.isEmpty()) {
				return;
			}

			EntityMergeSupport merger = new EntitySyncController(Application.getInstance().getFrameController(), dbEntity)
					.createMerger();

			if (merger == null) {
				return;
			}

			merger.setNameGenerator(new PreserveRelationshipNameGenerator());

			DbEntitySyncUndoableEdit undoableEdit = new DbEntitySyncUndoableEdit((DataChannelDescriptor) projectController
					.getProject().getRootNode(), projectController.getCurrentState().getDataMap());

			// filter out inherited entities, as we need to add attributes only to the roots
			dbEntityService.filterInheritedEntities(entities);

			for(ObjEntity entity : entities) {

				DbEntitySyncUndoableEdit.EntitySyncUndoableListener listener = undoableEdit.new EntitySyncUndoableListener(
						entity);

				merger.addEntityMergeListener(listener);

				// TODO: addition or removal of model objects should be reflected in listener callbacks...
				// we should not be trying to introspect the merger
				if (merger.isRemovingMeaningfulFKs()) {
					undoableEdit.addEdit(undoableEdit.new MeaningfulFKsUndoableEdit(entity, merger
							.getMeaningfulFKs(entity)));
				}

				if (merger.synchronizeWithDbEntity(entity)) {
					projectController.fireEvent(new ObjEntityEvent(this, entity, MapEvent.CHANGE));
				}

				merger.removeEntityMergeListener(listener);
			}

			application.getUndoManager().addEdit(undoableEdit);
		}
	}

	static class PreserveRelationshipNameGenerator extends DefaultObjectNameGenerator {

		@Override
		public String relationshipName(DbRelationship... relationshipChain) {
			if(relationshipChain.length == 0) {
				return super.relationshipName(relationshipChain);
			}
			DbRelationship last = relationshipChain[relationshipChain.length - 1];
			// must be in sync with DefaultBaseNameVisitor.visitDbRelationship
			if(last.getName().startsWith("untitledRel")) {
				return super.relationshipName(relationshipChain);
			}

			// keep manually set relationship name
			return last.getName();
		}
	}
}
