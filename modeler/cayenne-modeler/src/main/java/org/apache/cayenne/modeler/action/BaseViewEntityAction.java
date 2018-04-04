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
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.DbEntityDisplayEvent;
import org.apache.cayenne.modeler.event.ObjEntityDisplayEvent;
import org.apache.cayenne.modeler.util.CayenneAction;

import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

/**
 * @since 4.0
 */
public abstract class BaseViewEntityAction extends CayenneAction {

    @Inject
    public Application application;

    abstract protected Entity getEntity();

    public BaseViewEntityAction(String name) {
        super(name);
    }

    /**
     * @see org.apache.cayenne.modeler.util.CayenneAction#performAction(ActionEvent)
     */
    @Override
    public void performAction(ActionEvent e) {
        viewEntity();
    }

    protected void viewEntity() {
        Entity entity = getEntity();
        if(entity != null) {
            navigateToEntity(entity);
        }
    }

    public void navigateToEntity(Entity entity) {
        TreePath path = buildTreePath(entity);
        editor().getProjectTreeView().getSelectionModel().setSelectionPath(path);

        if (entity instanceof DbEntity) {
            getProjectController().fireEvent(new DbEntityDisplayEvent(
                    editor().getProjectTreeView(),
                    entity,
                    entity.getDataMap(),
                    (DataChannelDescriptor) getProjectController().getProject().getRootNode()));
        } else if (entity instanceof ObjEntity){
            getProjectController().fireEvent(new ObjEntityDisplayEvent(
                    editor().getProjectTreeView(),
                    entity,
                    entity.getDataMap(),
                    (DataChannelDescriptor) getProjectController().getProject().getRootNode()));
        }
    }
}
