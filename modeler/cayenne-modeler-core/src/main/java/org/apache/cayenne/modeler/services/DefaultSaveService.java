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
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.ChangePathInLastProjListEvent;
import org.apache.cayenne.modeler.event.RecentFileListEvent;
import org.apache.cayenne.pref.RenamedPreferences;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.project.ProjectSaver;
import org.apache.cayenne.resource.URLResource;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * @since 4.1
 */
public class DefaultSaveService implements SaveService {

    @Inject
    public ProjectController projectController;

    @Inject
    public ProjectService projectService;

    @Inject
    public PreferenceService preferenceService;

    @Override
    public void saveAll(Project p) {
        String oldPath = p.getConfigurationResource().getURL().getPath();

        projectController.getFileChangeTracker().pauseWatching();

        ProjectSaver saver = projectController.getInjector().getInstance(ProjectSaver.class);
        saver.save(p);

        RenamedPreferences.removeOldPreferences();

        // if change DataChanelDescriptor name - as result change name of xml file
        // we will need change preferences path
        String[] path = oldPath.split("/");
        String[] newPath = p.getConfigurationResource().getURL().getPath().split("/");

        if (!path[path.length - 1].equals(newPath[newPath.length - 1])) {
            String newName = newPath[newPath.length - 1].replace(".xml", "");
            RenamedPreferences.copyPreferences(newName, projectController.getPreferenceForProject());
            RenamedPreferences.removeOldPreferences();
        }
    }

    @Override
    public boolean saveAll(File projectDir) throws Exception{
        Project p = projectController.getProject();

        String oldPath = null;
        if (p.getConfigurationResource() != null) {
            oldPath = p.getConfigurationResource().getURL().getPath();
        }

        if (projectDir.exists() && !projectDir.canWrite()) {
            return false;
        }

        projectController.getFileChangeTracker().pauseWatching();

        URLResource res = new URLResource(projectDir.toURI().toURL());

        ProjectSaver saver = projectController.getInjector().getInstance(ProjectSaver.class);

        boolean isNewProject = p.getConfigurationResource() == null;
        Preferences tempOldPref = null;
        if (isNewProject) {
            tempOldPref = preferenceService.getMainPreferenceForProject();
        }
        saver.saveAs(p, res);

        if (oldPath != null && oldPath.length() != 0
                && !oldPath.equals(p.getConfigurationResource().getURL().getPath())) {

            String newName = p.getConfigurationResource().getURL().getPath().replace(".xml", "");
            String oldName = oldPath.replace(".xml", "");

            Preferences oldPref = projectController.getPreferenceForProject();
            String projPath = oldPref.absolutePath().replace(oldName, "");
            Preferences newPref = projectController.getPreferenceForProject().node(projPath + newName);
            RenamedPreferences.copyPreferences(newPref, projectController.getPreferenceForProject(), false);
        } else if (isNewProject) {
            if (tempOldPref != null) {

                String newProjectName = projectController.getNewProjectTemporaryName();

                if (tempOldPref.absolutePath().contains(newProjectName)) {

                    String projPath = tempOldPref.absolutePath().replace("/" + newProjectName, "");
                    String newName = p.getConfigurationResource().getURL().getPath().replace(".xml", "");

                    Preferences newPref = preferenceService.getMainPreferenceForProject().node(projPath + newName);

                    RenamedPreferences.copyPreferences(newPref, tempOldPref, false);
                    tempOldPref.removeNode();
                }
            }
        }

        RenamedPreferences.removeNewPreferences();

        File file = new File(p.getConfigurationResource().getURL().toURI());
        projectService.addToLastProjListAction(file);
        projectController.fireEvent(new RecentFileListEvent(this));

        // Reset the watcher now
        projectController.getFileChangeTracker().reconfigure();

        return true;
    }

    @Override
    public boolean saveAll() throws Exception{
        Project p = projectController.getProject();

        File oldProjectFile = new File(p.getConfigurationResource().getURL().toURI());

        saveAll(p);

        File newProjectFile = new File(p.getConfigurationResource().getURL().toURI());

        projectController.fireEvent(new ChangePathInLastProjListEvent(this, oldProjectFile, newProjectFile));
        projectController.fireEvent(new RecentFileListEvent(this));

        // Reset the watcher now
        projectController.getFileChangeTracker().reconfigure();

        return true;
    }

    @Override
    public void saveDbMapping(DbRelationship dbRelationship, DbRelationship reverseRelationship) {

    }

}
