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
import org.apache.cayenne.pref.CayenneProjectPreferences;
import org.apache.cayenne.project.ConfigurationNodeParentGetter;
import org.apache.cayenne.project.DefaultConfigurationNodeParentGetter;
import org.apache.cayenne.project.ProjectModule;

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
        binder.bind(ClassLoadingService.class).to(FileClassLoadingService.class);
        binder.bind(CayenneProjectPreferences.class).in(Singleton.class);
        binder.bind(ConfigurationNodeParentGetter.class).to(DefaultConfigurationNodeParentGetter.class);
        binder.bind(ProjectController.class).in(Singleton.class);

        contributeModuleClass(binder).addBinding().to(ProjectModule.class);
        addModuleClass(binder, new ProjectModule());
        addModuleClass(binder, new DbSyncModule());
        addModuleClass(binder, new ServerModule());
    }
}
