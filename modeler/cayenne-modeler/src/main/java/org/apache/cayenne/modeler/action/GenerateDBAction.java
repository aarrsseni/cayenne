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
import org.apache.cayenne.modeler.services.GenerateDbService;
import org.apache.cayenne.modeler.util.CayenneAction;

import java.awt.event.ActionEvent;

/**
 * Action that generates database tables from a DataMap.
 */
public class GenerateDBAction extends CayenneAction {

    @Inject
    public GenerateDbService dbService;

    public static String getActionName() {
        return "Generate Database Schema";
    }

    public GenerateDBAction() {
        super(getActionName());
    }

    public void performAction(ActionEvent e) {
        dbService.generateDb();
    }
}
