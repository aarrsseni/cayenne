package org.apache.cayenne.modeler;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import io.bootique.BQCoreModule;
import org.apache.cayenne.di.DIBootstrap;
import org.apache.cayenne.modeler.action.MigrateAction;
import org.apache.cayenne.modeler.dialog.db.gen.DBGeneratorOptions;
import org.apache.cayenne.modeler.init.CayenneModelerModule;
import org.apache.cayenne.modeler.init.platform.GenericPlatformInitializer;
import org.apache.cayenne.modeler.init.platform.PlatformInitializer;
import org.apache.cayenne.modeler.pref.helpers.CoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.helpers.CoreDbAdapterFactory;
import org.apache.cayenne.modeler.pref.helpers.DefaultCoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.helpers.DefaultCoreDbAdapterFactory;
import org.apache.cayenne.modeler.util.CayenneController;
import org.apache.cayenne.swing.BoundComponent;

import java.util.Set;

public class ModelerUiModule implements Module{

    private static Multibinder<org.apache.cayenne.di.Module> contributeModuleClass(Binder binder) {
        TypeLiteral<org.apache.cayenne.di.Module> type = new TypeLiteral<org.apache.cayenne.di.Module>() {
        };
        return Multibinder.newSetBinder(binder, type);
    }

    public static void setModuleClass(Binder binder, org.apache.cayenne.di.Module moduleClass) {
        contributeModuleClass(binder).addBinding().to(moduleClass.getClass());
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).addCommand(UiCommand.class);
        binder.bind(Launcher.class).to(GenericLauncher.class);
        binder.bind(Application.class).in(Singleton.class);
        binder.bind(PlatformInitializer.class).to(GenericPlatformInitializer.class);
        binder.bind(CoreDbAdapterFactory.class).to(DefaultCoreDbAdapterFactory.class);
        binder.bind(CoreDataSourceFactory.class).to(DefaultCoreDataSourceFactory.class);

        setModuleClass(binder, new CayenneModelerModule());
    }

    @Provides
    @Inject
    public org.apache.cayenne.di.Injector createInjector(Set<org.apache.cayenne.di.Module> modules) {
        return DIBootstrap.createInjector(modules);
    }

}
