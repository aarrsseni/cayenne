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
import org.apache.cayenne.configuration.ConfigurationTree;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.modeler.ModelerPreferences;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.ProjectDirtyEvent;
import org.apache.cayenne.modeler.event.ProjectOpenEvent;
import org.apache.cayenne.project.Project;

import java.io.File;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @since 4.1
 */
public class DefaultProjectService implements ProjectService {

    @Inject
    protected ProjectController projectController;

    public DefaultProjectService(){}

    @Override
    public void newProject() {
        DataChannelDescriptor dataChannelDescriptor = new DataChannelDescriptor();

        dataChannelDescriptor.setName(NameBuilder
                .builder(dataChannelDescriptor)
                .name());

        Project project = new Project(
                new ConfigurationTree<DataChannelDescriptor>(dataChannelDescriptor));

        //TODO FIX IT!!! Controller state must be service!
//        projectController.getCurrentState().currentDomainChanged(new DomainDisplayEvent(this, dataChannelDescriptor));

        projectController.fireEvent(new ProjectOpenEvent(this, project));

        // select default domain
        projectController.fireEvent(new DomainDisplayEvent(this, dataChannelDescriptor));
    }

    @Override
    public String createNameProject(Project p) {
        StringBuilder nameProject = new StringBuilder("cayenne");
        if(((DataChannelDescriptor)p.getRootNode()).getName()!=null){
            nameProject.append("-").append(((DataChannelDescriptor)p.getRootNode()).getName());
        }
        nameProject.append(".xml");
        return nameProject.toString();
    }

    @Override
    public void projectClosed() {
        projectController.setProject(null);

        projectController.fireEvent(new ProjectDirtyEvent(this, false));

        projectController.reset();

    }

    @Override
    public void addToLastProjListAction(File file) {
        Preferences prefLastProjFiles = ModelerPreferences.getLastProjFilesPref();
        List<File> arr = ModelerPreferences.getLastProjFiles();
        // Add proj path to the preferences
        // Prevent duplicate entries.
        if (arr.contains(file)) {
            arr.remove(file);
        }

        arr.add(0, file);
        while (arr.size() > ModelerPreferences.LAST_PROJ_FILES_SIZE) {
            arr.remove(arr.size() - 1);
        }

        try {
            prefLastProjFiles.clear();
        } catch (BackingStoreException ignored) {
            // ignore exception
        }

        int size = arr.size();
        for (int i = 0; i < size; i++) {
            prefLastProjFiles.put(String.valueOf(i), arr.get(i).getAbsolutePath());
        }
    }
}
