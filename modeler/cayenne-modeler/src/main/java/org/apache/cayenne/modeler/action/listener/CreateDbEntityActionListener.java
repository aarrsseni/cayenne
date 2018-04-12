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

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateDbEntityEvent;
import org.apache.cayenne.modeler.event.listener.CreateDbEntityListener;
import org.apache.cayenne.modeler.undo.CreateDbEntityUndoableEdit;

/**
 * @since 4.1
 */
public class CreateDbEntityActionListener implements CreateDbEntityListener {

    @Override
    public void createDbEntity(CreateDbEntityEvent e) {
        Application.getInstance().getUndoManager().addEdit(new CreateDbEntityUndoableEdit(e.getDataMap(), e.getDbEntity()));
    }

}
