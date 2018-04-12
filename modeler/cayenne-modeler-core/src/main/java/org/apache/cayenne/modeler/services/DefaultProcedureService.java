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
package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.event.ProcedureEvent;
import org.apache.cayenne.configuration.event.ProcedureParameterEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateProcedureEvent;
import org.apache.cayenne.modeler.event.ProcedureDisplayEvent;

/**
 * @since 4.1
 */
public class DefaultProcedureService implements ProcedureService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createProcedure() {
        DataMap map = projectController.getCurrentState().getDataMap();

        Procedure procedure = new Procedure();
        procedure.setName(NameBuilder.builder(procedure, map).name());

        procedure.setSchema(map.getDefaultSchema());
        procedure.setCatalog(map.getDefaultCatalog());
        map.addProcedure(procedure);

        projectController.fireEvent(new ProcedureEvent(this, procedure, MapEvent.ADD));
        projectController.fireEvent(new ProcedureDisplayEvent(this, procedure, projectController.getCurrentState().getDataMap(),
                (DataChannelDescriptor) projectController.getProject().getRootNode()));

        projectController.fireEvent(new CreateProcedureEvent(this, map, procedure));
    }

    public void createProcedure(DataMap map, Procedure procedure) {
        procedure.setSchema(map.getDefaultSchema());
        procedure.setCatalog(map.getDefaultCatalog());
        map.addProcedure(procedure);
        fireProcedureEvent(this, projectController, map, procedure);
    }

    public void removeProcedureParameters(Procedure procedure, ProcedureParameter[] parameters) {
        for (ProcedureParameter parameter : parameters) {
            procedure.removeCallParameter(parameter.getName());
            ProcedureParameterEvent e = new ProcedureParameterEvent(this, parameter, MapEvent.REMOVE);
            projectController.fireEvent(e);
        }
    }

    /**
     * Removes current Procedure from its DataMap and fires "remove" ProcedureEvent.
     */
    public void removeProcedure(DataMap map, Procedure procedure) {

        ProcedureEvent e = new ProcedureEvent(this, procedure, MapEvent.REMOVE);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        map.removeProcedure(procedure.getName());
        projectController.fireEvent(e);
    }

    @Override
    public void fireProcedureEvent(Object src, ProjectController mediator, DataMap dataMap, Procedure procedure) {
        mediator.fireEvent(new ProcedureEvent(src, procedure, MapEvent.ADD));
        mediator.fireEvent(new ProcedureDisplayEvent(src, procedure, mediator.getCurrentState().getDataMap(),
                (DataChannelDescriptor) mediator.getProject().getRootNode()));
    }

}
