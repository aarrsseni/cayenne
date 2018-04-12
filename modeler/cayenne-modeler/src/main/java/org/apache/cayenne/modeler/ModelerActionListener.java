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
package org.apache.cayenne.modeler;

import org.apache.cayenne.modeler.action.listener.*;
import org.apache.cayenne.modeler.event.listener.*;

/**
 * @since 4.1
 */
public class ModelerActionListener {

    protected CayenneModelerController cayenneModelerController;

    public ModelerActionListener(CayenneModelerController cayenneModelerController) {
        this.cayenneModelerController = cayenneModelerController;
        initListeners();
    }

    public void initListeners() {
        cayenneModelerController.getProjectController().getEventController().addListener(ProjectOpenListener.class, new ProjectOpenActionListener(cayenneModelerController));
        cayenneModelerController.getProjectController().getEventController().addListener(CreateAttributeListener.class, new CreateAttributeActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateDataMapListener.class, new CreateDataMapActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateDbEntityListener.class, new CreateDbEntityActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateEmbeddableListener.class, new CreateEmbeddableActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateNodeListener.class, new CreateNodeActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateProcedureListener.class, new CreateProcedureActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateProcedureParameterListener.class, new CreateProcedureParameterActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateRelationshipListener.class, new CreateRelationshipActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(LinkDataMapsListener.class, new LinkDataMapsActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateObjEntityListener.class, new CreateObjEntityActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(CreateCallbackMethodListener.class, new CreateCallbackMethodActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(GenerateDbListener.class, new GenerateDbActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(LinkDataMapListener.class, new LinkDataMapActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(GenerateCodeListener.class, new GenerateCodeActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(DbLoaderExceptionListener.class, new DbLoaderExceptionProcessListener());
        cayenneModelerController.getProjectController().getEventController().addListener(ProcessUpgradesListener.class, new ProcessUpgradesActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(ChangePathInLastProjListListener.class, new ChangePathInLastProjListActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(DbEntitySyncListener.class, new DbEntitySyncActionListener());
        cayenneModelerController.getProjectController().getEventController().addListener(ConfirmMeaningfulFKsListener.class, new ConfirmMeaningfulFKsActionListener());

        RemoveActionListener removeActionListener = new RemoveActionListener(cayenneModelerController);

        cayenneModelerController.getProjectController().getEventController().addListener(RemoveObjEntityListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveDbEntityListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveQueryListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveProcedureListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveEmbeddableListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveDataMapListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveDataNodeListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemovePathsListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveCallbackMethodListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveObjRelationshipsListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveDbRelationshipsListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveEmbeddableAttributeListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveDbAttributesListener.class, removeActionListener);
        cayenneModelerController.getProjectController().getEventController().addListener(RemoveObjAttributesListener.class, removeActionListener);
    }
}
