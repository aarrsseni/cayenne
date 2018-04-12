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
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.map.Relationship;
import org.apache.cayenne.modeler.dialog.ConfirmRemoveDialog;
import org.apache.cayenne.modeler.services.RelationshipService;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;

import java.awt.event.ActionEvent;

/**
 * Removes currently selected relationship from either the DbEntity or
 * ObjEntity.
 * 
 */
public class RemoveRelationshipAction extends RemoveAction implements
		MultipleObjectsAction {

	@Inject
	public RelationshipService relationshipService;

	private final static String ACTION_NAME = "Remove Relationship";

	/**
	 * Name of action if multiple rels are selected
	 */
	private final static String ACTION_NAME_MULTIPLE = "Remove Relationships";

	public static String getActionName() {
		return ACTION_NAME;
	}

	public String getActionName(boolean multiple) {
		return multiple ? ACTION_NAME_MULTIPLE : ACTION_NAME;
	}

	public RemoveRelationshipAction() {
		super(ACTION_NAME);
	}

	/**
	 * Returns <code>true</code> if last object in the path contains a removable
	 * relationship.
	 */
	@Override
	public boolean enableForPath(ConfigurationNode object) {
		if (object == null) {
			return false;
		}

		return object instanceof Relationship;
	}

	@Override
	public void performAction(ActionEvent e, boolean allowAsking) {
		ConfirmRemoveDialog dialog = getConfirmDeleteDialog(allowAsking);

		RemoveServiceStatus status = relationshipService.isRemove();

		if(status != null && dialog.shouldDelete(status.getType(), status.getName())) {
			relationshipService.remove();
		}
	}
}
