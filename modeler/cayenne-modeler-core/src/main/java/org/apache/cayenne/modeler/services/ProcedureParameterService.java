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

import org.apache.cayenne.map.Procedure;
import org.apache.cayenne.map.ProcedureParameter;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.util.RemoveServiceStatus;

/**
 * @since 4.1
 */
public interface ProcedureParameterService {
    void createProcedureParameter();

    void createProcedureParameter(Procedure procedure, ProcedureParameter parameter);

    /**
     * Fires events when an proc parameter was added
     */
    void fireProcedureParameterEvent(Object src, ProjectController mediator, Procedure procedure, ProcedureParameter parameter);

    void removeProcedureParameters();

    void removeProcedureParameters(Procedure procedure, ProcedureParameter[] parameters);

    RemoveServiceStatus isRemove();

    void remove();
}
