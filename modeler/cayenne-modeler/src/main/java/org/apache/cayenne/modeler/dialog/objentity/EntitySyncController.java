/*
 *    Licensed to the Apache Software Foundation (ASF) under one
 *    or more contributor license agreements.  See the NOTICE file
 *    distributed with this work for additional information
 *    regarding copyright ownership.  The ASF licenses this file
 *    to you under the Apache License, Version 2.0 (the
 *    "License"); you may not use this file except in compliance
 *    with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */

package org.apache.cayenne.modeler.dialog.objentity;

import org.apache.cayenne.dbsync.filter.NamePatternMatcher;
import org.apache.cayenne.dbsync.merge.context.EntityMergeSupport;
import org.apache.cayenne.dbsync.naming.ObjectNameGenerator;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.services.NameGeneratorService;
import org.apache.cayenne.modeler.util.CayenneController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;

public class EntitySyncController extends CayenneController {

    private final DbEntity dbEntity;
    private ObjEntity objEntity;
    private EntitySyncDialog view;

    private NameGeneratorService nameGeneratorService;

    /**
     * Creates a controller for synchronizing all ObjEntities mapped to a given DbEntity.
     */
    public EntitySyncController(CayenneController parent, DbEntity dbEntity) {
        super(parent);
        this.dbEntity = dbEntity;
        this.nameGeneratorService = Application.getInstance().getProjectController().getBootiqueInjector().getInstance(NameGeneratorService.class);
    }

    /**
     * Creates a controller for synchronizing a single ObjEntity with its parent DbEntity.
     */
    public EntitySyncController(CayenneController parent, ObjEntity objEntity) {
        this(parent, objEntity.getDbEntity());
        this.objEntity = objEntity;
    }

    public EntityMergeSupport createMerger() {
        Collection<ObjEntity> entities = getObjEntities();
        if (entities.isEmpty()) {
            return null;
        }

        ObjectNameGenerator namingStrategy;
        try {
            namingStrategy = nameGeneratorService.createNamingStrategy();
        } catch (Throwable e) {
            namingStrategy = nameGeneratorService.defaultNameGenerator();
        }

        // TODO: Modeler-controlled defaults for all the hardcoded boolean flags here.
        EntityMergeSupport merger = new EntityMergeSupport(namingStrategy, NamePatternMatcher.EXCLUDE_ALL, true, true, false);

        // see if we need to remove meaningful attributes...
        for (ObjEntity entity : entities) {
            if (!merger.getMeaningfulFKs(entity).isEmpty()) {
                return confirmMeaningfulFKs(namingStrategy);
            }
        }

        return merger;
    }

    /**
     * Displays merger config dialog, returning a merger configured by the user. Returns
     * null if the dialog was canceled.
     */
    protected EntityMergeSupport confirmMeaningfulFKs(ObjectNameGenerator namingStrategy) {

        final boolean[] cancel = {false};
        final boolean[] removeFKs = {true};

        view = new EntitySyncDialog();

        view.getUpdateButton().addActionListener(e -> {
            removeFKs[0] = view.getRemoveFKs().isSelected();
            view.dispose();
        });

        view.getCancelButton().addActionListener(e -> {
            cancel[0] = true;
            view.dispose();
        });

        view.pack();
        view.setModal(true);
        centerView();
        makeCloseableOnEscape();
        view.setVisible(true);

        // TODO: Modeler-controlled defaults for all the hardcoded flags here.
        return cancel[0] ? null : new EntityMergeSupport(namingStrategy, NamePatternMatcher.EXCLUDE_ALL, removeFKs[0], true, false);
    }

    @Override
    public Component getView() {
        return view;
    }

    protected Collection<ObjEntity> getObjEntities() {
        return objEntity == null ? dbEntity.getDataMap().getMappedEntities(dbEntity)
                : Collections.singleton(objEntity);
    }

}
