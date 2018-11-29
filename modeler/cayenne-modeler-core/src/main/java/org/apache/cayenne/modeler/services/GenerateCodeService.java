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

import org.apache.cayenne.gen.CgenConfiguration;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.ObjEntity;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * @since 4.1
 */
public interface GenerateCodeService {
    void generateCode();

    boolean isDefaultConfig(CgenConfiguration cgenConfiguration);

    void updateArtifactGenerationMode(Object classObj, boolean selected);

    CgenConfiguration getCurrentConfiguration();

    void loadEntity(ObjEntity objEntity);

    void loadEmbeddable(String embeddable);

    CgenValidationService getCgenValidationService();

    void initCollections();

    Set<String> getSelectedEntities();

    Set<String> getSelectedEmbeddables();

    Set<String> isDataMapSelected();

    void removeEntity(ObjEntity objEntity);

    void removeEmbeddable(Embeddable embeddable);

    CgenConfiguration getConfiguration(List<Object> classes);

    CgenConfiguration createConfiguration(Path basePath, List<Object> classes);

    void updateSelectedEntities(List<Object> classes);

    List<Object> prepareClasses(DataMap dataMap);
}
