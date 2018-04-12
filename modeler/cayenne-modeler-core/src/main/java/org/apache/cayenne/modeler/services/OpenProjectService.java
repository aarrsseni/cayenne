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
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.ProjectOpenEvent;
import org.apache.cayenne.modeler.services.util.OpenProjectStatus;
import org.apache.cayenne.modeler.services.util.ProjectStatus;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.project.ProjectLoader;
import org.apache.cayenne.project.upgrade.UpgradeMetaData;
import org.apache.cayenne.project.upgrade.UpgradeService;
import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.resource.URLResource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apache.cayenne.project.upgrade.UpgradeType.DOWNGRADE_NEEDED;
import static org.apache.cayenne.project.upgrade.UpgradeType.INTERMEDIATE_UPGRADE_NEEDED;
import static org.apache.cayenne.project.upgrade.UpgradeType.UPGRADE_NEEDED;

/**
 * @since 4.1
 */
public class OpenProjectService {

    @Inject
    public ProjectService projectService;

    @Inject
    public ProjectController projectController;

    private static final Map<String, String> PROJECT_TO_MODELER_VERSION;
    static {
        // Correspondence between project version and latest Modeler version that can upgrade it.
        // Modeler v4.1 can handle versions from 3.1 and 4.0 (including intermediate versions) modeler.
        Map<String, String> map = new HashMap<>();
        map.put("1.0",      "v3.0");
        map.put("1.1",      "v3.0");
        map.put("1.2",      "v3.0");
        map.put("2.0",      "v3.0");
        map.put("3.0.0.1",  "v3.1");
        PROJECT_TO_MODELER_VERSION = Collections.unmodifiableMap(map);
    }

    public OpenProjectStatus canOpen(File file) throws MalformedURLException {
        if(!file.exists()) {
            return new OpenProjectStatus(ProjectStatus.ERROR, "Can't open project - file \"" + file.getPath() + "\" does not exist",
                    "Can't Open Project");
        }

        projectService.addToLastProjListAction(file);

        URL url = file.toURI().toURL();
        Resource rootSource = new URLResource(url);

        UpgradeService upgradeService = projectController.getInjector().getInstance(UpgradeService.class);
        UpgradeMetaData metaData = upgradeService.getUpgradeType(rootSource);

        if(metaData.getUpgradeType() == INTERMEDIATE_UPGRADE_NEEDED) {
            String modelerVersion = PROJECT_TO_MODELER_VERSION.get(metaData.getProjectVersion());
            if(modelerVersion == null) {
                modelerVersion = "";
            }
            return new OpenProjectStatus(ProjectStatus.ERROR, "Open the project in the older Modeler " + modelerVersion
                    + " to do an intermediate upgrade\nbefore you can upgrade to latest version.",
                    "Can't Upgrade Project");
        } else if(metaData.getUpgradeType() == DOWNGRADE_NEEDED) {
            return new OpenProjectStatus(ProjectStatus.ERROR, "Can't open project - it was created using a newer version of the Modeler",
                    "Can't Open Project");
        } else if(metaData.getUpgradeType() == UPGRADE_NEEDED){
            return new OpenProjectStatus(ProjectStatus.UPGRADE_NEEDED, null, null);
        }
        return new OpenProjectStatus(ProjectStatus.OK, null, null);
    }

    public Resource getRootSource(File file) throws MalformedURLException {
        return  new URLResource(file.toURI().toURL());
    }

    public Project openProjectResourse(Resource resource) {
        Project project = projectController.getInjector().getInstance(ProjectLoader.class).loadProject(resource);
        projectController.fireEvent(new ProjectOpenEvent(this, project));
        return project;
    }

    public Resource upgradeResource(Resource rootSource) {
        UpgradeService upgradeService = projectController.getInjector().getInstance(UpgradeService.class);
        return upgradeService.upgradeProject(rootSource);
    }
}
