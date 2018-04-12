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
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.GenerateCodeEvent;
import org.apache.cayenne.project.Project;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @since 4.1
 */
public class DefaultGenerateCodeService implements GenerateCodeService {

    @Inject
    public ProjectController projectController;

    @Override
    public void generateCode() {
        Collection<DataMap> dataMaps;
        DataMap dataMap = projectController.getCurrentState().getDataMap();

        if (dataMap != null) {
            dataMaps = new ArrayList<>();
            dataMaps.add(dataMap);

            projectController.fireEvent(new GenerateCodeEvent(this, dataMaps));
        } else if (projectController.getCurrentState().getNode() != null) {
            Collection<String> nodeMaps = projectController.getCurrentState().getNode().getDataMapNames();
            Project project = projectController.getProject();
            dataMaps = ((DataChannelDescriptor) project.getRootNode()).getDataMaps();

            Collection<DataMap> resultMaps = new ArrayList<>();
            for (DataMap map : dataMaps) {
                if (nodeMaps.contains(map.getName())) {
                    resultMaps.add(map);
                }
            }

            projectController.fireEvent(new GenerateCodeEvent(this, resultMaps));
        } else {
            Project project = projectController.getProject();
            dataMaps = ((DataChannelDescriptor) project.getRootNode()).getDataMaps();

            projectController.fireEvent(new GenerateCodeEvent(this, dataMaps));
        }
    }
}
