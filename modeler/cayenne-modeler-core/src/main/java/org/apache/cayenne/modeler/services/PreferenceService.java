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
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.pref.CayennePreference;

import java.util.prefs.Preferences;

/**
 * @since 4.1
 */
public class PreferenceService {

    @Inject
    public ProjectController projectController;

    private CayennePreference cayennePreference;

    public PreferenceService(){
        cayennePreference = new CayennePreference();
    }

    public Preferences getPreferencesNode(Class<?> className, String path) {
        return cayennePreference.getNode(className, path);
    }

    public Preferences getMainPreferenceForProject() {

        DataChannelDescriptor descriptor = (DataChannelDescriptor) projectController
                .getProject()
                .getRootNode();

        // if new project
        if (descriptor.getConfigurationSource() == null) {
            return getPreferencesNode(
                    projectController
                    .getProject().getClass(),
                    projectController.getNewProjectTemporaryName());
        }

        String path = CayennePreference.filePathToPrefereceNodePath(descriptor
                .getConfigurationSource()
                .getURL()
                .getPath());
        Preferences pref = getPreferencesNode(projectController.getProject().getClass(), "");
        return pref.node(pref.absolutePath() + path);
    }

    public CayennePreference getCayennePreference() {
        return cayennePreference;
    }
}
