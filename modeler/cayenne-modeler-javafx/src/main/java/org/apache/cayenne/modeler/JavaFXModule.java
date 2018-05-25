package org.apache.cayenne.modeler;

import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import io.bootique.BQCoreModule;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import org.apache.cayenne.di.DIBootstrap;
import org.apache.cayenne.modeler.action.CreateDataMapAction;
import org.apache.cayenne.modeler.action.NewProjectAction;
import org.apache.cayenne.modeler.action.SaveAction;
import org.apache.cayenne.modeler.action.SaveAsAction;
import org.apache.cayenne.modeler.controller.DbRelationshipsController;
import org.apache.cayenne.modeler.controller.ScreenController;
import org.apache.cayenne.modeler.init.CayenneModelerModule;
import org.apache.cayenne.modeler.util.AbstractCayenneAction;
import org.apache.cayenne.modeler.components.CayenneTreeHelper;
import org.apache.cayenne.project.validation.DefaultProjectValidator;
import org.apache.cayenne.project.validation.ProjectValidator;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JavaFXModule implements Module {

    private static Multibinder<Class<? extends BQApplication>> contributeApplicationClass(Binder binder) {
        TypeLiteral<Class<? extends BQApplication>> type = new TypeLiteral<Class<? extends BQApplication>>() {
        };
        return Multibinder.newSetBinder(binder, type);
    }

    public static void setApplicationClass(Binder binder, Class<? extends BQApplication> appClass) {
        contributeApplicationClass(binder).addBinding().toInstance(appClass);
    }

    private static Multibinder<org.apache.cayenne.di.Module> contributeModuleClass(Binder binder) {
        TypeLiteral<org.apache.cayenne.di.Module> type = new TypeLiteral<org.apache.cayenne.di.Module>() {
        };
        return Multibinder.newSetBinder(binder, type);
    }

    public static void setModuleClass(Binder binder, org.apache.cayenne.di.Module moduleClass) {
        contributeModuleClass(binder).addBinding().to(moduleClass.getClass());
    }

    private static Multibinder<AbstractCayenneAction> contributeActionClass(Binder binder) {
        TypeLiteral<AbstractCayenneAction> type = new TypeLiteral<AbstractCayenneAction>(){};
        return Multibinder.newSetBinder(binder, type);
    }

    public static void setActionClass(Binder binder, AbstractCayenneAction actionClass) {
        contributeActionClass(binder).addBinding().to(actionClass.getClass());
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).addCommand(UiCommand.class);

        setModuleClass(binder, new CayenneModelerModule());

        contributeApplicationClass(binder);

        binder.bind(ScreenController.class).in(Singleton.class);

        setActionClass(binder, new NewProjectAction());
        setActionClass(binder, new SaveAction());
        setActionClass(binder, new SaveAsAction());
        setActionClass(binder, new CreateDataMapAction());

        binder.bind(JavaFXLauncher.class).to(GenericJavaFXLauncher.class);
        binder.bind(BaseApplication.class).in(Singleton.class);
        binder.bind(CayenneTreeHelper.class).in(Singleton.class);

        binder.bind(ProjectValidator.class).to(DefaultProjectValidator.class);

        binder.bind(DbRelationshipsController.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    UiCommand provideUICommand(Provider<Injector> injectorProvider,
                               Provider<Class<? extends BQApplication>> appClassProvider) {
        return new UiCommand(appClassProvider, injectorProvider);
    }

    @Provides
    @Singleton
    Class<? extends BQApplication> provideAppClass(Set<Class<? extends BQApplication>> appClasses) {
        if (appClasses.isEmpty()) {
            throw new RuntimeException("No application class specified. Use JavaFXModule.setApplicationClass(..)");
        }

        if (appClasses.size() > 1) {
            throw new RuntimeException("Multiple application classes specified: " + appClasses);
        }

        return appClasses.iterator().next();
    }

    @Provides
    @Singleton
    FXMLLoaderFactory provideFXMLLoaderFactory(Injector injector) {

        return new FXMLLoaderFactory() {
            @Override
            public FXMLLoader getLoader(URL u) {
                FXMLLoader loader = new FXMLLoader(u);

                // this makes injection into JavaFX controllers possible
                loader.setControllerFactory(aClass -> injector.getInstance(aClass));

                return loader;
            }
        };
    }

    @Provides
    @Inject
    public org.apache.cayenne.di.Injector createInjector(Set<org.apache.cayenne.di.Module> modules) {
        return DIBootstrap.createInjector(modules);
    }

    @Provides
    public Map<String, EventHandler> getActionMap(Set<AbstractCayenneAction> set){
        Map<String, EventHandler> map = new HashMap<>(40);
        Iterator<AbstractCayenneAction> iterator = set.iterator();
        while(iterator.hasNext()){
            AbstractCayenneAction cayenneAction = iterator.next();
            EventHandler oldAction = map.put(cayenneAction.getClass().getName(), cayenneAction);
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
