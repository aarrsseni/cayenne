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
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.SaveService;
import org.apache.cayenne.project.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * An action that saves a project using to its default location.
 */
public class SaveAction extends SaveAsAction {

    @Inject
    public Application application;

    @Inject
    public ProjectController projectController;

    @Inject
    public SaveService saveService;

    public static String getActionName() {
        return "Save";
    }

    public SaveAction() {
        super(getActionName());
    }

    @Override
    public KeyStroke getAcceleratorKey() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }

    @Override
    public String getIconName() {
        return "icon-save.png";
    }

    @Override
    protected boolean saveAll() throws Exception {
        Project p = getCurrentProject();
        if (p == null || p.getConfigurationResource() == null) {
            return super.saveAll();
        }

        return saveService.saveAll();
    }
}
