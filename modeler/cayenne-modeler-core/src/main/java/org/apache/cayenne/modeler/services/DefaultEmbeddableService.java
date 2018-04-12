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
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.event.EmbeddableEvent;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateEmbeddableEvent;
import org.apache.cayenne.modeler.event.EmbeddableDisplayEvent;

/**
 * @since 4.1
 */
public class DefaultEmbeddableService implements EmbeddableService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createEmbeddable() {
        DataMap dataMap = projectController.getCurrentState().getDataMap();

        Embeddable embeddable = new Embeddable();
        String baseName = NameBuilder.builder(embeddable, dataMap).name();
        String nameWithPackage = dataMap.getNameWithDefaultPackage(baseName);
        embeddable.setClassName(nameWithPackage);

        dataMap.addEmbeddable(embeddable);

        projectController.fireEvent(
                new EmbeddableEvent(this, embeddable, MapEvent.ADD, dataMap));
        EmbeddableDisplayEvent displayEvent = new EmbeddableDisplayEvent(
                this,
                embeddable,
                dataMap,
                (DataChannelDescriptor)projectController.getProject().getRootNode());
        displayEvent.setMainTabFocus(true);
        projectController.fireEvent(displayEvent);

        projectController.fireEvent(new CreateEmbeddableEvent(this, dataMap, embeddable));
    }

    public void createEmbeddable(DataMap dataMap, Embeddable embeddable) {
        dataMap.addEmbeddable(embeddable);
        fireEmbeddableEvent(this, projectController, dataMap, embeddable);
    }

    public void removeEmbeddable(DataMap map, Embeddable embeddable) {

        EmbeddableEvent e = new EmbeddableEvent(this, embeddable, MapEvent.REMOVE, map);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        map.removeEmbeddable(embeddable.getClassName());
        projectController.fireEvent(e);
    }

    @Override
    public void fireEmbeddableEvent(
            Object src,
            ProjectController mediator,
            DataMap dataMap,
            Embeddable embeddable) {

        mediator.fireEvent(
                new EmbeddableEvent(src, embeddable, MapEvent.ADD, dataMap));
        EmbeddableDisplayEvent displayEvent = new EmbeddableDisplayEvent(
                src,
                embeddable,
                dataMap,
                (DataChannelDescriptor)mediator.getProject().getRootNode());
        displayEvent.setMainTabFocus(true);
        mediator.fireEvent(displayEvent);

    }
}
