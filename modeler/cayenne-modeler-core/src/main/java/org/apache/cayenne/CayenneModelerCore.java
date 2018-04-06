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

package org.apache.cayenne;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.dbsync.DbSyncModule;
import org.apache.cayenne.modeler.ClassLoadingService;
import org.apache.cayenne.modeler.FileClassLoadingService;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.*;
import org.apache.cayenne.pref.CayenneProjectPreferences;
import org.apache.cayenne.project.*;

/**
 * @since 4.1
 */
public class CayenneModelerCore implements Module{

    private static Multibinder<org.apache.cayenne.di.Module> contributeModuleClass(Binder binder) {
        TypeLiteral<org.apache.cayenne.di.Module> type = new TypeLiteral<org.apache.cayenne.di.Module>() {
        };
        return Multibinder.newSetBinder(binder, type);
    }

    public static void addModuleClass(Binder binder, org.apache.cayenne.di.Module moduleClass) {
        contributeModuleClass(binder).addBinding().toInstance(moduleClass);
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(ClassLoadingService.class).to(FileClassLoadingService.class).in(Singleton.class);
        binder.bind(CayenneProjectPreferences.class).in(Singleton.class);
        binder.bind(ConfigurationNodeParentGetter.class).to(DefaultConfigurationNodeParentGetter.class);
        binder.bind(ProjectController.class).in(Singleton.class);

        binder.bind(ProjectService.class).to(DefaultProjectService.class);
        binder.bind(ExitService.class).to(DefaultExitService.class);
        binder.bind(AttributeService.class).to(DefaultAttributeService.class);
        binder.bind(DataMapService.class).to(DefaultDataMapService.class);
        binder.bind(DbEntityService.class).to(DefaultDbEntityService.class);
        binder.bind(EmbeddableService.class).to(DefaultEmbeddableService.class);
        binder.bind(NodeService.class).to(DefaultNodeService.class);
        binder.bind(ObjEntityService.class).to(DefaultObjEntityService.class);
        binder.bind(ProcedureService.class).to(DefaultProcedureService.class);
        binder.bind(ProcedureParameterService.class).to(DefaultProcedureParameterService.class);
        binder.bind(RelationshipService.class).to(DefaultRelationshipService.class);
        binder.bind(DocumentationService.class).to(DefaultDocumentationService.class);
        binder.bind(FindService.class).to(DefaultFindService.class);
        binder.bind(PasteService.class).to(DefaultPasteService.class);
        binder.bind(CallbackMethodService.class).to(DefaultCallbackMethodService.class);
        binder.bind(QueryService.class).to(DefaultQueryService.class);
        binder.bind(GenerateDbService.class).to(DefaultGenerateDbService.class);
        binder.bind(GenerateCodeService.class).to(DefaultGenerateCodeService.class);
        binder.bind(SaveService.class).to(DefaultSaveService.class);
        binder.bind(EOModelService.class).to(DefaultEOModelService.class);
        binder.bind(DbService.class).to(DefaultDbService.class).in(Singleton.class);
        binder.bind(NavigationService.class).to(DefaultNavigationService.class);

        contributeModuleClass(binder).addBinding().to(ProjectModule.class);
        addModuleClass(binder, new ProjectModule());
        addModuleClass(binder, new DbSyncModule());
        addModuleClass(binder, new ServerModule());
    }
}
