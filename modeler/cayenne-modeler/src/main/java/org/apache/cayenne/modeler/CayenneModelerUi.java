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

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import io.bootique.BQCoreModule;
import org.apache.cayenne.configuration.ConfigurationNameMapper;
import org.apache.cayenne.configuration.DefaultConfigurationNameMapper;
import org.apache.cayenne.di.DIBootstrap;
import org.apache.cayenne.modeler.action.AboutAction;
import org.apache.cayenne.modeler.action.ActionManager;
import org.apache.cayenne.modeler.action.CgenAction;
import org.apache.cayenne.modeler.action.CollapseTreeAction;
import org.apache.cayenne.modeler.action.ConfigurePreferencesAction;
import org.apache.cayenne.modeler.action.CopyAction;
import org.apache.cayenne.modeler.action.CopyAttributeAction;
import org.apache.cayenne.modeler.action.CopyAttributeRelationshipAction;
import org.apache.cayenne.modeler.action.CopyCallbackMethodAction;
import org.apache.cayenne.modeler.action.CopyProcedureParameterAction;
import org.apache.cayenne.modeler.action.CopyRelationshipAction;
import org.apache.cayenne.modeler.action.CreateAttributeAction;
import org.apache.cayenne.modeler.action.CreateCallbackMethodAction;
import org.apache.cayenne.modeler.action.CreateDataMapAction;
import org.apache.cayenne.modeler.action.CreateDbEntityAction;
import org.apache.cayenne.modeler.action.CreateEmbeddableAction;
import org.apache.cayenne.modeler.action.CreateNodeAction;
import org.apache.cayenne.modeler.action.CreateObjEntityAction;
import org.apache.cayenne.modeler.action.CreateObjEntityFromDbAction;
import org.apache.cayenne.modeler.action.CreateProcedureAction;
import org.apache.cayenne.modeler.action.CreateProcedureParameterAction;
import org.apache.cayenne.modeler.action.CreateQueryAction;
import org.apache.cayenne.modeler.action.CreateRelationshipAction;
import org.apache.cayenne.modeler.action.CutAction;
import org.apache.cayenne.modeler.action.CutAttributeAction;
import org.apache.cayenne.modeler.action.CutAttributeRelationshipAction;
import org.apache.cayenne.modeler.action.CutCallbackMethodAction;
import org.apache.cayenne.modeler.action.CutProcedureParameterAction;
import org.apache.cayenne.modeler.action.CutRelationshipAction;
import org.apache.cayenne.modeler.action.DbEntityCounterpartAction;
import org.apache.cayenne.modeler.action.DbEntitySyncAction;
import org.apache.cayenne.modeler.action.DefaultActionManager;
import org.apache.cayenne.modeler.action.DocumentationAction;
import org.apache.cayenne.modeler.action.ExitAction;
import org.apache.cayenne.modeler.action.FilterAction;
import org.apache.cayenne.modeler.action.FindAction;
import org.apache.cayenne.modeler.action.GenerateCodeAction;
import org.apache.cayenne.modeler.action.GenerateDBAction;
import org.apache.cayenne.modeler.action.GetDbConnectionAction;
import org.apache.cayenne.modeler.action.ImportDataMapAction;
import org.apache.cayenne.modeler.action.ImportEOModelAction;
import org.apache.cayenne.modeler.action.InferRelationshipsAction;
import org.apache.cayenne.modeler.action.LinkDataMapAction;
import org.apache.cayenne.modeler.action.LinkDataMapsAction;
import org.apache.cayenne.modeler.action.LoadDbSchemaAction;
import org.apache.cayenne.modeler.action.MigrateAction;
import org.apache.cayenne.modeler.action.NavigateBackwardAction;
import org.apache.cayenne.modeler.action.NavigateForwardAction;
import org.apache.cayenne.modeler.action.NewProjectAction;
import org.apache.cayenne.modeler.action.ObjEntityCounterpartAction;
import org.apache.cayenne.modeler.action.ObjEntitySyncAction;
import org.apache.cayenne.modeler.action.OpenProjectAction;
import org.apache.cayenne.modeler.action.PasteAction;
import org.apache.cayenne.modeler.action.ProjectAction;
import org.apache.cayenne.modeler.action.RedoAction;
import org.apache.cayenne.modeler.action.RemoveAction;
import org.apache.cayenne.modeler.action.RemoveAttributeAction;
import org.apache.cayenne.modeler.action.RemoveAttributeRelationshipAction;
import org.apache.cayenne.modeler.action.RemoveCallbackMethodAction;
import org.apache.cayenne.modeler.action.RemoveProcedureParameterAction;
import org.apache.cayenne.modeler.action.RemoveRelationshipAction;
import org.apache.cayenne.modeler.action.ReverseEngineeringAction;
import org.apache.cayenne.modeler.action.RevertAction;
import org.apache.cayenne.modeler.action.SaveAction;
import org.apache.cayenne.modeler.action.SaveAsAction;
import org.apache.cayenne.modeler.action.ShowLogConsoleAction;
import org.apache.cayenne.modeler.action.UndoAction;
import org.apache.cayenne.modeler.action.ValidateAction;
import org.apache.cayenne.modeler.action.dbimport.AddCatalogAction;
import org.apache.cayenne.modeler.action.dbimport.AddExcludeColumnAction;
import org.apache.cayenne.modeler.action.dbimport.AddExcludeProcedureAction;
import org.apache.cayenne.modeler.action.dbimport.AddExcludeTableAction;
import org.apache.cayenne.modeler.action.dbimport.AddIncludeColumnAction;
import org.apache.cayenne.modeler.action.dbimport.AddIncludeProcedureAction;
import org.apache.cayenne.modeler.action.dbimport.AddIncludeTableAction;
import org.apache.cayenne.modeler.action.dbimport.AddSchemaAction;
import org.apache.cayenne.modeler.action.dbimport.DeleteNodeAction;
import org.apache.cayenne.modeler.action.dbimport.EditNodeAction;
import org.apache.cayenne.modeler.action.dbimport.MoveImportNodeAction;
import org.apache.cayenne.modeler.action.dbimport.MoveInvertNodeAction;
import org.apache.cayenne.modeler.action.dbimport.ReverseEngineeringToolMenuAction;
import org.apache.cayenne.modeler.graph.action.ShowGraphEntityAction;
import org.apache.cayenne.modeler.init.CayenneModelerModule;
import org.apache.cayenne.modeler.pref.CoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.CoreDbAdapterFactory;
import org.apache.cayenne.modeler.pref.DefaultCoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.DefaultCoreDbAdapterFactory;
import org.apache.cayenne.modeler.util.CayenneAction;

import javax.swing.Action;
import java.util.HashMap;
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

    private static void setModuleClass(Binder binder, org.apache.cayenne.di.Module moduleClass) {
        contributeModuleClass(binder).addBinding().to(moduleClass.getClass());
    }

    private static Multibinder<CayenneAction> contributeActionClass(Binder binder) {
        TypeLiteral<CayenneAction> type = new TypeLiteral<CayenneAction>(){};
        return Multibinder.newSetBinder(binder, type);
    }

    private static void setActionClass(Binder binder, CayenneAction actionClass) {
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
        setActionClass(binder, new LoadDbSchemaAction());
        setActionClass(binder, new ReverseEngineeringToolMenuAction());
        setActionClass(binder, new MoveImportNodeAction());
        setActionClass(binder, new MoveInvertNodeAction());
        setActionClass(binder, new AddSchemaAction());
        setActionClass(binder, new AddCatalogAction());
        setActionClass(binder, new AddIncludeTableAction());
        setActionClass(binder, new AddExcludeTableAction());
        setActionClass(binder, new AddIncludeColumnAction());
        setActionClass(binder, new AddExcludeColumnAction());
        setActionClass(binder, new AddIncludeProcedureAction());
        setActionClass(binder, new AddExcludeProcedureAction());
        setActionClass(binder, new EditNodeAction());
        setActionClass(binder, new DeleteNodeAction());
        setActionClass(binder, new GetDbConnectionAction());
        setActionClass(binder, new CreateObjEntityFromDbAction());
        setActionClass(binder, new CgenAction());
    }

    @Provides
    @Singleton
    @Inject
    public org.apache.cayenne.di.Injector createInjector(Set<org.apache.cayenne.di.Module> modules) {
        return DIBootstrap.createInjector(modules);
    }

    @Provides
    @Singleton
    public Map<String, Action> getActionMap(Set<CayenneAction> actionSet){
        Map<String, Action> map = new HashMap<>(40);
        for (CayenneAction cayenneAction : actionSet) {
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


