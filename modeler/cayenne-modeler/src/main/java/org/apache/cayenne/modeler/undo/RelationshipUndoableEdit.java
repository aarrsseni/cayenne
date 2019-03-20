/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.modeler.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.Relationship;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.map.event.RelationshipEvent;
import org.apache.cayenne.map.relationship.DbRelationship;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.model.DbRelationshipModelConverter;
import org.apache.cayenne.modeler.map.relationship.DbJoinModel;
import org.apache.cayenne.modeler.map.relationship.DbJoinMutable;
import org.apache.cayenne.project.extension.info.ObjectInfo;

public class RelationshipUndoableEdit extends CayenneUndoableEdit {

	private static final long serialVersionUID = -1864303176024098961L;

	private Relationship relationship;
    private Relationship prevRelationship;
    private ProjectController projectController;
    private boolean useDb;

    private DbJoinModel dbJoinModel;
    private DbJoinModel prevDbJoinModel;

	public RelationshipUndoableEdit(Relationship relationship) {
		this.projectController = Application.getInstance().getFrameController().getProjectController();
		this.relationship = relationship;
		this.useDb = relationship instanceof DbRelationship;
		this.prevRelationship = copyRelationship(relationship);
	}

	public RelationshipUndoableEdit(DbJoinModel dbJoinModel, DbRelationship relationship) {
		this.projectController = Application.getInstance().getFrameController().getProjectController();
		this.relationship = relationship;
		this.dbJoinModel = dbJoinModel;
		this.prevDbJoinModel = copyDbJoinModel(dbJoinModel);
		this.useDb = true;
	}

    @Override
	public void redo() throws CannotRedoException {
		fireEvent(dbJoinModel, relationship, prevRelationship);
	}

	@Override
	public void undo() throws CannotUndoException {
		fireEvent(prevDbJoinModel, prevRelationship, relationship);
	}

	private void fireEvent(DbJoinModel dbJoinModel,
						   Relationship prevRelationship,
						   Relationship relationship) {
		if(useDb) {
			fireDbRelationshipEvent(buildRelationshipFromModel(dbJoinModel));
		} else {
			fireObjRelationshipEvent(prevRelationship, relationship);
		}
	}

	private void fireDbRelationshipEvent(Relationship relToFire) {
		projectController
				.fireDbRelationshipEvent(
						new RelationshipEvent(this, relToFire, relToFire.getSourceEntity(), MapEvent.CHANGE));
	}

	private void fireObjRelationshipEvent(Relationship relToFire, Relationship currRel) {
		ObjEntity objEntity = ((ObjRelationship) currRel).getSourceEntity();
		objEntity.removeRelationship(currRel.getName());
		objEntity.addRelationship(relToFire);
		projectController
				.fireObjRelationshipEvent(
						new RelationshipEvent(this, relToFire, relToFire.getSourceEntity(), MapEvent.ADD));
	}

	@Override
	public String getRedoPresentationName() {
		return "Redo Edit relationship";
	}

	@Override
	public String getUndoPresentationName() {
		return "Undo Edit relationship";
	}

	private Relationship buildRelationshipFromModel(DbJoinModel dbJoinModel) {
		DbJoinMutable dbJoin = new DbRelationshipModelConverter()
				.updateDbRelationships(dbJoinModel, (DbRelationship) relationship);
		DbRelationship currRelationship = dbJoin.getRelationship(((DbRelationship) relationship).getDirection());
		ObjectInfo.putToMetaData(projectController.getApplication().getMetaData(),
				currRelationship.getDbJoin(),
				ObjectInfo.COMMENT, dbJoinModel.getComment());
		return currRelationship;
	}

	private DbJoinModel copyDbJoinModel(DbJoinModel dbJoinModel) {
		DbJoinModel copiedDbJoinModel = new DbJoinModel();
		copiedDbJoinModel.setDbEntities(dbJoinModel.getDbEntities().clone());
		copiedDbJoinModel.setNames(dbJoinModel.getNames().clone());
		copiedDbJoinModel.setToMany(dbJoinModel.getToMany().clone());
		copiedDbJoinModel.setToDepPK(dbJoinModel.getToDepPK().clone());
		copiedDbJoinModel.setComments(dbJoinModel.getComment());
		copiedDbJoinModel.setColumnPairs(dbJoinModel.getColumnPairs());
		copiedDbJoinModel.setDataMap(dbJoinModel.getDataMap());
		return copiedDbJoinModel;
	}

	private Relationship copyRelationship(Relationship relationship) {
		return getObjRelationship(relationship);
	}

	private ObjRelationship getObjRelationship(Relationship objRelationship) {
		ObjRelationship rel = new ObjRelationship();
		rel.setName(objRelationship.getName());
		rel.setTargetEntityName(objRelationship.getTargetEntityName());
		rel.setSourceEntity(objRelationship.getSourceEntity());
		rel.setDeleteRule(((ObjRelationship)objRelationship).getDeleteRule());
		rel.setUsedForLocking(((ObjRelationship)objRelationship).isUsedForLocking());
		rel.setDbRelationshipPath(((ObjRelationship)objRelationship).getDbRelationshipPath());
		rel.setCollectionType(((ObjRelationship)objRelationship).getCollectionType());
		rel.setMapKey(((ObjRelationship)objRelationship).getMapKey());
		return rel;
	}
}
