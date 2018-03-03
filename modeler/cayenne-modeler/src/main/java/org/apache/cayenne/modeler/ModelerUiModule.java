package org.apache.cayenne.modeler;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import io.bootique.BQCoreModule;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.dbsync.DbSyncModule;
import org.apache.cayenne.di.DIBootstrap;
import org.apache.cayenne.modeler.init.CayenneModelerModule;
import org.apache.cayenne.modeler.init.platform.GenericPlatformInitializer;
import org.apache.cayenne.modeler.init.platform.PlatformInitializer;
import org.apache.cayenne.project.ProjectModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ModelerUiModule implements Module{

    private static Multibinder<org.apache.cayenne.di.Module> contributeModuleClass(Binder binder) {
        Multibinder<org.apache.cayenne.di.Module> moduleBinder = Multibinder.newSetBinder(binder, org.apache.cayenne.di.Module.class);
        moduleBinder.addBinding().to(ProjectModule.class);
        moduleBinder.addBinding().to(ServerModule.class);
        moduleBinder.addBinding().to(DbSyncModule.class);
        moduleBinder.addBinding().to(CayenneModelerModule.class);
        return moduleBinder;
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).addCommand(UiCommand.class);
        binder.bind(Launcher.class).to(GenericLauncher.class);
        binder.bind(Application.class).in(Singleton.class);
        binder.bind(PlatformInitializer.class).to(GenericPlatformInitializer.class);

        contributeModuleClass(binder);
    }

    @Provides
    @Inject
    public org.apache.cayenne.di.Injector createInjector(Set<org.apache.cayenne.di.Module> modules) {
        return DIBootstrap.createInjector(modules);
    }

}
