package org.apache.cayenne;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.apache.cayenne.configuration.server.ServerModule;
import org.apache.cayenne.dbsync.DbSyncModule;
import org.apache.cayenne.modeler.ClassLoadingService;
import org.apache.cayenne.modeler.FileClassLoadingService;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;
import org.apache.cayenne.project.ProjectModule;

public class CayenneModelerCore implements Module{

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
        binder.bind(ClassLoadingService.class).to(FileClassLoadingService.class);

        setModuleClass(binder, new ProjectModule());
        setModuleClass(binder, new DbSyncModule());
        setModuleClass(binder, new ServerModule());
    }
}
