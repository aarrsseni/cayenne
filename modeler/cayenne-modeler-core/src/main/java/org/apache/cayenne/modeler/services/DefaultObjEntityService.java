package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.ObjEntityEvent;
import org.apache.cayenne.dbsync.filter.NamePatternMatcher;
import org.apache.cayenne.dbsync.merge.context.EntityMergeSupport;
import org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.dbsync.naming.NoStemStemmer;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateObjEntityEvent;
import org.apache.cayenne.modeler.event.ObjEntityDisplayEvent;
import org.apache.cayenne.util.DeleteRuleUpdater;

import java.util.ArrayList;

public class DefaultObjEntityService implements ObjEntityService{

    @Inject
    public ProjectController projectController;

    @Inject
    public QueryService queryService;

    @Override
    public void createObjEntity() {
        DataMap dataMap = projectController.getCurrentState().getDataMap();
        ObjEntity entity = new ObjEntity();
        entity.setName(NameBuilder.builder(entity, dataMap).name());

        // init defaults
        entity.setSuperClassName(dataMap.getDefaultSuperclass());
        entity.setDeclaredLockType(dataMap.getDefaultLockType());

        DbEntity dbEntity = projectController.getCurrentState().getDbEntity();
        if (dbEntity != null) {
            entity.setDbEntity(dbEntity);

            // TODO: use injectable name generator
            String baseName = new DefaultObjectNameGenerator(NoStemStemmer.getInstance()).objEntityName(dbEntity);
            entity.setName(NameBuilder
                    .builder(entity, dbEntity.getDataMap())
                    .baseName(baseName)
                    .name());
        }
        entity.setClassName(dataMap.getNameWithDefaultPackage(entity.getName()));

        if (dataMap.isClientSupported()) {
            entity.setClientClassName(dataMap.getNameWithDefaultClientPackage(entity.getName()));
            entity.setClientSuperClassName(dataMap.getDefaultClientSuperclass());
        }

        dataMap.addObjEntity(entity);

        // TODO: Modeler-controlled defaults for all the hardcoded boolean flags here.
        EntityMergeSupport merger = new EntityMergeSupport(new DefaultObjectNameGenerator(NoStemStemmer.getInstance()),
                NamePatternMatcher.EXCLUDE_ALL, true, true, false);
        merger.setNameGenerator(new DefaultDbEntitySyncService.PreserveRelationshipNameGenerator());
        merger.addEntityMergeListener(DeleteRuleUpdater.getEntityMergeListener());
        merger.synchronizeWithDbEntity(entity);

        fireObjEntityEvent(this, projectController, dataMap, entity);

        projectController.fireEvent(new CreateObjEntityEvent(this, dataMap, entity));
    }

    public void createObjEntity(DataMap dataMap, ObjEntity entity) {
        dataMap.addObjEntity(entity);
        fireObjEntityEvent(this, projectController, dataMap, entity);
    }

    @Override
    public void syncObjEntity(ObjEntity objEntity) {
        projectController
                .fireEvent(new ObjEntityEvent(this, objEntity, MapEvent.CHANGE));
        projectController.fireEvent(new ObjEntityDisplayEvent(
                this,
                objEntity,
                objEntity.getDataMap(),
                (DataChannelDescriptor)projectController.getProject().getRootNode()));
    }

    /**
     * Removes current object entity from its DataMap.
     */
    public void removeObjEntity(DataMap map, ObjEntity entity) {

        ObjEntityEvent e = new ObjEntityEvent(this, entity, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        map.removeObjEntity(entity.getName(), true);
        projectController.fireEvent(e);

        // remove queries that depend on entity
        // TODO: (Andrus, 09/09/2005) show warning dialog?

        // clone to be able to remove within iterator...
        for (QueryDescriptor query : new ArrayList<>(map.getQueryDescriptors())) {
            if (!QueryDescriptor.EJBQL_QUERY.equals(query.getType())) {
                Object root = query.getRoot();

                if (root == entity || (root instanceof String && root.toString().equals(entity.getName()))) {
                    queryService.removeQuery(map, query);
                }
            }
        }
    }

    @Override
    public void fireObjEntityEvent(
            Object src,
            ProjectController mediator,
            DataMap dataMap,
            ObjEntity entity) {
        mediator.fireEvent(new ObjEntityEvent(src, entity, MapEvent.ADD));
        ObjEntityDisplayEvent displayEvent = new ObjEntityDisplayEvent(
                src,
                entity,
                dataMap,
                mediator.getCurrentState().getNode(),
                (DataChannelDescriptor) mediator.getProject().getRootNode());
        displayEvent.setMainTabFocus(true);
        mediator.fireEvent(displayEvent);
    }
}
