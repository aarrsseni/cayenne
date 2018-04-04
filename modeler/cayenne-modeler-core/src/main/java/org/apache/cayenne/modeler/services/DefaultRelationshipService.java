package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.DbRelationshipEvent;
import org.apache.cayenne.configuration.event.ObjRelationshipEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateRelationshipEvent;
import org.apache.cayenne.modeler.event.DbRelationshipDisplayEvent;
import org.apache.cayenne.modeler.event.ObjRelationshipDisplayEvent;
import org.apache.cayenne.modeler.util.ProjectUtil;
import org.apache.cayenne.util.DeleteRuleUpdater;

public class DefaultRelationshipService implements RelationshipService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createRelationship() {
        ObjEntity objEnt = projectController.getCurrentState().getObjEntity();
        if (objEnt != null) {

            ObjRelationship rel = new ObjRelationship();
            rel.setName(NameBuilder.builder(rel, objEnt).name());
            createObjRelationship(objEnt, rel);

            projectController.fireEvent(new CreateRelationshipEvent(this, objEnt, rel));
        } else {
            DbEntity dbEnt = projectController.getCurrentState().getDbEntity();
            if (dbEnt != null) {

                DbRelationship rel = new DbRelationship();
                rel.setName(NameBuilder.builder(rel, dbEnt).name());
                createDbRelationship(dbEnt, rel);

                projectController.fireEvent(new CreateRelationshipEvent(this, dbEnt, rel));
            }
        }
    }

    public void createObjRelationship(ObjEntity objEntity, ObjRelationship rel) {
        rel.setSourceEntity(objEntity);
        DeleteRuleUpdater.updateObjRelationship(rel);

        objEntity.addRelationship(rel);
        fireObjRelationshipEvent(this, projectController, objEntity, rel);
    }

    public void createDbRelationship(DbEntity dbEntity, DbRelationship rel) {
        rel.setSourceEntity(dbEntity);
        dbEntity.addRelationship(rel);

        fireDbRelationshipEvent(this, projectController, dbEntity, rel);
    }

    public void removeObjRelationships(ObjEntity entity, ObjRelationship[] rels) {

        for (ObjRelationship rel : rels) {
            entity.removeRelationship(rel.getName());
            ObjRelationshipEvent e = new ObjRelationshipEvent(this,
                    rel, entity, MapEvent.REMOVE);
            projectController.fireEvent(e);
        }
    }

    public void removeDbRelationships(DbEntity entity, DbRelationship[] rels) {

        for (DbRelationship rel : rels) {
            entity.removeRelationship(rel.getName());

            DbRelationshipEvent e = new DbRelationshipEvent(this,
                    rel, entity, MapEvent.REMOVE);
            projectController.fireEvent(e);
        }

        ProjectUtil.cleanObjMappings(projectController.getCurrentState().getDataMap());
    }

    /**
     * Fires events when a obj rel was added
     */
    public void fireObjRelationshipEvent(Object src, ProjectController mediator, ObjEntity objEntity,
                                         ObjRelationship rel) {

        mediator.fireEvent(new ObjRelationshipEvent(src, rel, objEntity, MapEvent.ADD));

        ObjRelationshipDisplayEvent rde = new ObjRelationshipDisplayEvent(src, rel, objEntity, mediator.getCurrentState().getDataMap(),
                (DataChannelDescriptor) mediator.getProject().getRootNode());

        mediator.fireEvent(rde);
    }

    /**
     * Fires events when a db rel was added
     */
    public void fireDbRelationshipEvent(Object src, ProjectController mediator, DbEntity dbEntity, DbRelationship rel) {

        mediator.fireEvent(new DbRelationshipEvent(src, rel, dbEntity, MapEvent.ADD));

        DbRelationshipDisplayEvent rde = new DbRelationshipDisplayEvent(src, rel, dbEntity, mediator.getCurrentState().getDataMap(),
                (DataChannelDescriptor) mediator.getProject().getRootNode());

        mediator.fireEvent(rde);
    }
}
