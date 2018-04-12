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
import org.apache.cayenne.modeler.ControllerState;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.*;

/**
 * @since 4.1
 */
public class DefaultNavigationService implements NavigationService {

    @Inject
    public ProjectController projectController;

    @Override
    public void moveForward() {
        int size = projectController.getControllerStateHistory().size();
        if (size == 0)
            return;

        int i = projectController.getControllerStateHistory().indexOf(projectController.getCurrentState());
        ControllerState cs;
        if (size == 1) {
            cs = projectController.getControllerStateHistory().get(0);
        }
        else {
            int counter = 0;
            while (true) {
                if (i < 0) {
                    // a new state got created without it being saved.
                    // just move to the beginning of the list
                    cs = projectController.getControllerStateHistory().get(0);
                } else if (i + 1 < size) {
                    // move forward
                    cs = projectController.getControllerStateHistory().get(i + 1);
                } else {
                    // wrap around
                    cs = projectController.getControllerStateHistory().get(0);
                }
                if (!cs.isEquivalent(projectController.getCurrentState())) {
                    break;
                }

                // if it doesn't find it within 5 tries it is probably stuck in
                // a loop
                if (++counter > 5) {
                    break;
                }
                i++;
            }
        }
        runDisplayEvent(cs);
    }

    @Override
    public void moveBackward() {
        int size = projectController.getControllerStateHistory().size();
        if (size == 0)
            return;

        int i = projectController.getControllerStateHistory().indexOf(projectController.getCurrentState());
        ControllerState cs;
        if (size == 1) {
            cs = projectController.getControllerStateHistory().get(0);
        }
        else {
            int counter = 0;
            while (true) {
                if (i < 0) {
                    // a new state got created without it being saved.
                    try {
                        cs = projectController.getControllerStateHistory().get(size - 2);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        cs = projectController.getControllerStateHistory().get(size - 1);
                    }
                } else if (i - 1 >= 0) {
                    // move to the previous one
                    cs = projectController.getControllerStateHistory().get(i - 1);
                } else {
                    // wrap around
                    cs = projectController.getControllerStateHistory().get(size - 1);
                }
                if (!cs.isEquivalent(projectController.getCurrentState())) {
                    break;
                }
                // if it doesn't find it within 5 tries it is probably stuck in a loop
                if (++counter > 5) {
                    break;
                }
                i--;
            }
        }
        runDisplayEvent(cs);
    }

    private void runDisplayEvent(ControllerState cs) {
        // reset the current state to the one we just navigated to
        projectController.setCurrentState(cs);
        DisplayEvent de = cs.getEvent();
        if (de == null) {
            return;
        }

        // make sure that isRefiring is turned off prior to exiting this routine
        // this flag is used to tell the controller to not create new states
        // when we are refiring the event that we saved earlier
        projectController.getCurrentState().setRefiring(true);

        // the order of the following is checked in most specific to generic
        // because of the inheritance hierarchy
        de.setRefired(true);
        if (de instanceof EntityDisplayEvent) {
            if(de instanceof DbEntityDisplayEvent) {
                DbEntityDisplayEvent ede = (DbEntityDisplayEvent) de;
                ede.setEntityChanged(true);
                projectController.fireEvent(ede);
            } else if(de instanceof ObjEntityDisplayEvent) {
                ObjEntityDisplayEvent ede = (ObjEntityDisplayEvent) de;
                ede.setEntityChanged(true);
                projectController.fireEvent(ede);
            }
        } else if (de instanceof EmbeddableDisplayEvent) {
            EmbeddableDisplayEvent ede = (EmbeddableDisplayEvent) de;
            ede.setEmbeddableChanged(true);
            projectController.fireEvent(ede);
        } else if (de instanceof ProcedureDisplayEvent) {
            ProcedureDisplayEvent pde = (ProcedureDisplayEvent) de;
            pde.setProcedureChanged(true);
            projectController.fireEvent(pde);
        } else if (de instanceof QueryDisplayEvent) {
            QueryDisplayEvent qde = (QueryDisplayEvent) de;
            qde.setQueryChanged(true);
            projectController.fireEvent(qde);
        } else if (de instanceof DataMapDisplayEvent) {
            DataMapDisplayEvent dmde = (DataMapDisplayEvent) de;
            dmde.setDataMapChanged(true);
            projectController.fireEvent(dmde);
        } else if (de instanceof DataNodeDisplayEvent) {
            DataNodeDisplayEvent dnde = (DataNodeDisplayEvent) de;
            dnde.setDataNodeChanged(true);
            projectController.fireEvent(dnde);
        } else if (de instanceof DomainDisplayEvent) {
            DomainDisplayEvent dde = (DomainDisplayEvent) de;
            dde.setDomainChanged(true);
            projectController.fireEvent(dde);
        }

        // turn off refiring
        projectController.getCurrentState().setRefiring(false);
    }


}
