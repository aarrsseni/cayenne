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
import org.apache.cayenne.configuration.event.ProcedureParameterEvent;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateProcedureParameterEvent;
import org.apache.cayenne.modeler.event.ProcedureParameterDisplayEvent;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;

/**
 * @since 4.1
 */
public class DefaultProcedureParameterService implements ProcedureParameterService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createProcedureParameter() {
        if (projectController.getCurrentState().getProcedure() != null) {
            Procedure procedure = projectController.getCurrentState().getProcedure();
            ProcedureParameter parameter = new ProcedureParameter();
            parameter.setName(NameBuilder.builder(parameter, procedure).name());

            createProcedureParameter(procedure, parameter);

            projectController.fireEvent(new CreateProcedureParameterEvent(this, projectController, procedure, parameter));
        }
    }

    public void createProcedureParameter(Procedure procedure, ProcedureParameter parameter) {
        procedure.addCallParameter(parameter);
        fireProcedureParameterEvent(this, projectController, procedure, parameter);
    }

    public void fireProcedureParameterEvent(Object src, ProjectController mediator, Procedure procedure,
                                     ProcedureParameter parameter) {
        mediator.fireEvent(new ProcedureParameterEvent(src, parameter, MapEvent.ADD));

        mediator.fireEvent(new ProcedureParameterDisplayEvent(src, parameter, procedure,
                mediator.getCurrentState().getDataMap(), (DataChannelDescriptor) mediator.getProject().getRootNode()));
    }

    public void removeProcedureParameters() {
        ProcedureParameter[] parameters = projectController.getCurrentState().getProcedureParameters();
        removeProcedureParameters(projectController.getCurrentState().getProcedure(), parameters);
    }

    public void removeProcedureParameters(
            Procedure procedure,
            ProcedureParameter[] parameters) {

        for (ProcedureParameter parameter : parameters) {

            procedure.removeCallParameter(parameter.getName());

            ProcedureParameterEvent e = new ProcedureParameterEvent(this, parameter, MapEvent.REMOVE);

            projectController.fireEvent(e);
        }
    }

    @Override
    public RemoveServiceStatus isRemove() {
        ProcedureParameter[] params = projectController
                .getCurrentState()
                .getProcedureParameters();
        if (params.length > 0) {
            if (params.length == 1) {
                return new RemoveServiceStatus("procedure parameter", params[0].getName());
            } else {
                return new RemoveServiceStatus(null, "selected procedure parameters");
            }
        }
        return null;
    }

    @Override
    public void remove() {
        ProcedureParameter[] params = projectController
                .getCurrentState()
                .getProcedureParameters();
        if (params.length > 0) {
            removeProcedureParameters();
        }
    }

}
