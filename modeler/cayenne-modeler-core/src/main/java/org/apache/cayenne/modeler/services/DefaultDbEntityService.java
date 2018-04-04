package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.DbEntityEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateDbEntityEvent;
import org.apache.cayenne.modeler.event.DbEntityDisplayEvent;

public class DefaultDbEntityService implements DbEntityService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createDbEntity() {
        DataMap map = projectController.getCurrentState().getDataMap();
        DbEntity entity = new DbEntity();
        entity.setName(NameBuilder.builder(entity, map).name());

        entity.setCatalog(map.getDefaultCatalog());
        entity.setSchema(map.getDefaultSchema());
        map.addDbEntity(entity);

        projectController.fireEvent(new DbEntityEvent(this, entity, MapEvent.ADD));
        DbEntityDisplayEvent displayEvent = new DbEntityDisplayEvent(this, entity, projectController.getCurrentState().getDataMap(),
                projectController.getCurrentState().getNode(), (DataChannelDescriptor) projectController.getProject().getRootNode());
        displayEvent.setMainTabFocus(true);
        projectController.fireEvent(displayEvent);

        projectController.fireEvent(new CreateDbEntityEvent(this, map, entity));
    }

    public void createEntity(DataMap map, DbEntity entity) {
        entity.setCatalog(map.getDefaultCatalog());
        entity.setSchema(map.getDefaultSchema());
        map.addDbEntity(entity);
        fireDbEntityEvent(this, projectController, entity);
    }

    /**
     * Removes current DbEntity from its DataMap and fires "remove" EntityEvent.
     */
    public void removeDbEntity(DataMap map, DbEntity ent) {

        DbEntityEvent e = new DbEntityEvent(this, ent, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        map.removeDbEntity(ent.getName(), true);
        projectController.fireEvent(e);
    }

    @Override
    public void fireDbEntityEvent(Object src, ProjectController mediator, DbEntity entity) {
        mediator.fireEvent(new DbEntityEvent(src, entity, MapEvent.ADD));
        DbEntityDisplayEvent displayEvent = new DbEntityDisplayEvent(src, entity, mediator.getCurrentState().getDataMap(),
                mediator.getCurrentState().getNode(), (DataChannelDescriptor) mediator.getProject().getRootNode());
        displayEvent.setMainTabFocus(true);
        mediator.fireEvent(displayEvent);
    }

    @Override
    public void syncDbEntity() {

    }
}

