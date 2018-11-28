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
package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.ObjEntityEvent;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.DbEntitySyncEvent;
import org.apache.cayenne.modeler.event.listener.DbEntitySyncListener;
import org.apache.cayenne.modeler.undo.DbEntitySyncUndoableEdit;

/**
 * @since 4.1
 */
public class DbEntitySyncActionListener implements DbEntitySyncListener{
    @Override
    public void dbEntitySyncUndoableEdit(DbEntitySyncEvent e) {
        DbEntitySyncUndoableEdit undoableEdit = new DbEntitySyncUndoableEdit((DataChannelDescriptor) Application.getInstance()
                .getProjectController().getProject().getRootNode(), Application.getInstance().getProjectController().getCurrentState().getDataMap());

        for(ObjEntity entity : e.getEntities()) {

            DbEntitySyncUndoableEdit.EntitySyncUndoableListener listener = undoableEdit.new EntitySyncUndoableListener(
                    entity);

            e.getMerger().addEntityMergeListener(listener);

            // TODO: addition or removal of model objects should be reflected in listener callbacks...
            // we should not be trying to introspect the merger
            if (e.getMerger().isRemovingMeaningfulFKs()) {
                undoableEdit.addEdit(undoableEdit.new MeaningfulFKsUndoableEdit(entity, e.getMerger()
                        .getMeaningfulFKs(entity)));
            }

            if (e.getMerger().synchronizeWithDbEntity(entity)) {
                Application.getInstance().getProjectController().fireEvent(new ObjEntityEvent(this, entity, MapEvent.CHANGE));
            }

            e.getMerger().removeEntityMergeListener(listener);
        }

        Application.getInstance().getUndoManager().addEdit(undoableEdit);
    }
}
