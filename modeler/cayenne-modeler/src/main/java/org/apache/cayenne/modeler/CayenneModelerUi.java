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

package org.apache.cayenne.modeler;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import io.bootique.BQCoreModule;
import org.apache.cayenne.configuration.ConfigurationNameMapper;
import org.apache.cayenne.configuration.DefaultConfigurationNameMapper;
import org.apache.cayenne.di.DIBootstrap;
import org.apache.cayenne.modeler.action.*;
import org.apache.cayenne.modeler.graph.action.ShowGraphEntityAction;
import org.apache.cayenne.modeler.init.CayenneModelerModule;
import org.apache.cayenne.modeler.pref.CoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.CoreDbAdapterFactory;
import org.apache.cayenne.modeler.pref.DefaultCoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.DefaultCoreDbAdapterFactory;
import org.apache.cayenne.modeler.util.CayenneAction;

import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @since 4.1
 */
public class CayenneModelerUi implements com.google.inject.Module{

    private static Multibinder<org.apache.cayenne.di.Module> contributeModuleClass(Binder binder) {
        TypeLiteral<org.apache.cayenne.di.Module> type = new TypeLiteral<org.apache.cayenne.di.Module>() {
        };
        return Multibinder.newSetBinder(binder, type);
    }

    public static void setModuleClass(Binder binder, org.apache.cayenne.di.Module moduleClass) {
        contributeModuleClass(binder).addBinding().to(moduleClass.getClass());
    }

    private static Multibinder<CayenneAction> contributeActionClass(Binder binder) {
        TypeLiteral<CayenneAction> type = new TypeLiteral<CayenneAction>(){};
        return Multibinder.newSetBinder(binder, type);
    }

    public static void setActionClass(Binder binder, CayenneAction actionClass) {
        contributeActionClass(binder).addBinding().to(actionClass.getClass());
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).addCommand(UiCommand.class);
        binder.bind(Launcher.class).to(GenericLauncher.class);
        binder.bind(Application.class).in(Singleton.class);
        binder.bind(CoreDbAdapterFactory.class).to(DefaultCoreDbAdapterFactory.class);
        binder.bind(CoreDataSourceFactory.class).to(DefaultCoreDataSourceFactory.class);
        binder.bind(ActionManager.class).to(DefaultActionManager.class);
        binder.bind(ConfigurationNameMapper.class).to(DefaultConfigurationNameMapper.class);

        setModuleClass(binder, new CayenneModelerModule());



        setActionClass(binder, new ExitAction());
        setActionClass(binder, new ProjectAction());
        setActionClass(binder, new NewProjectAction());
        setActionClass(binder, new OpenProjectAction());
        setActionClass(binder, new ImportDataMapAction());
        setActionClass(binder, new SaveAsAction());
        setActionClass(binder, new SaveAction());
        setActionClass(binder, new RevertAction());
        setActionClass(binder, new ValidateAction());
        setActionClass(binder, new RemoveAction());
        setActionClass(binder, new CreateCallbackMethodAction());
        setActionClass(binder, new CreateNodeAction());
        setActionClass(binder, new CreateDataMapAction());
        setActionClass(binder, new GenerateCodeAction());
        setActionClass(binder, new CreateObjEntityAction());
        setActionClass(binder, new CreateDbEntityAction());
        setActionClass(binder, new CreateProcedureAction());
        setActionClass(binder, new CreateProcedureParameterAction());
        setActionClass(binder, new RemoveProcedureParameterAction());
        setActionClass(binder, new CreateQueryAction());
        setActionClass(binder, new CreateAttributeAction());
        setActionClass(binder, new RemoveAttributeAction());
        setActionClass(binder, new CreateRelationshipAction());
        setActionClass(binder, new RemoveRelationshipAction());
        setActionClass(binder, new RemoveAttributeRelationshipAction());
        // start callback-related actions
        setActionClass(binder, new CreateCallbackMethodAction());
        setActionClass(binder, new RemoveCallbackMethodAction());
        // end callback-related actions
        setActionClass(binder, new DbEntitySyncAction());
        setActionClass(binder, new ObjEntitySyncAction());
        setActionClass(binder, new DbEntityCounterpartAction());
        setActionClass(binder, new ObjEntityCounterpartAction());
        setActionClass(binder, new ReverseEngineeringAction());
        setActionClass(binder, new InferRelationshipsAction());
        setActionClass(binder, new ImportEOModelAction());
        setActionClass(binder, new GenerateDBAction());
        setActionClass(binder, new MigrateAction());
        setActionClass(binder, new AboutAction());
        setActionClass(binder, new DocumentationAction());
        setActionClass(binder, new ConfigurePreferencesAction());
        setActionClass(binder, new NavigateBackwardAction());
        setActionClass(binder, new NavigateForwardAction());
        setActionClass(binder, new FindAction());
        setActionClass(binder, new ShowLogConsoleAction());
        setActionClass(binder, new CutAction());
        setActionClass(binder, new CutAttributeAction());
        setActionClass(binder, new CutRelationshipAction());
        setActionClass(binder, new CutAttributeRelationshipAction());
        setActionClass(binder, new CutProcedureParameterAction());
        setActionClass(binder, new CopyAction());
        setActionClass(binder, new CopyAttributeAction());
        setActionClass(binder, new CopyRelationshipAction());
        setActionClass(binder, new CopyAttributeRelationshipAction());
        setActionClass(binder, new CopyCallbackMethodAction());
        setActionClass(binder, new CopyProcedureParameterAction());
        setActionClass(binder, new PasteAction());
        setActionClass(binder, new UndoAction());
        setActionClass(binder, new RedoAction());
        setActionClass(binder, new CreateEmbeddableAction());
        setActionClass(binder, new ShowGraphEntityAction());
        setActionClass(binder, new CollapseTreeAction());
        setActionClass(binder, new FilterAction());
        setActionClass(binder, new LinkDataMapAction());
        setActionClass(binder, new LinkDataMapsAction());
        setActionClass(binder, new CutCallbackMethodAction());
    }

    @Provides
    @Inject
    public org.apache.cayenne.di.Injector createInjector(Set<org.apache.cayenne.di.Module> modules) {
        return DIBootstrap.createInjector(modules);
    }

    @Provides
    public Map<String, Action> getActionMap(Set<CayenneAction> set){
        Map<String, Action> map = new HashMap<>(40);
        Iterator<CayenneAction> iterator = set.iterator();
        while(iterator.hasNext()){
            CayenneAction cayenneAction = iterator.next();
            Action oldAction = map.put(cayenneAction.getClass().getName(), cayenneAction);
            if (oldAction != null && oldAction != cayenneAction) {

                map.put(cayenneAction.getClass().getName(), oldAction);
                throw new IllegalArgumentException("There is already an action of type "
                        + cayenneAction.getClass().getName()
                        + ", attempt to register a second instance.");
            }
        }
        return map;
    }
}


