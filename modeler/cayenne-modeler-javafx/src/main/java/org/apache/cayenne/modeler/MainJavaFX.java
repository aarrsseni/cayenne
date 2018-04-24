package org.apache.cayenne.modeler;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;
import org.apache.cayenne.CayenneModelerCore;

public class MainJavaFX implements Module{

    public static void main(String[] args) {
        Bootique.app(args).autoLoadModules()
                .module(MainJavaFX.class)
                .module(CayenneModelerCore.class)
                .module(JavaFXModule.class)
                .exec();
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).setDefaultCommand(UiCommand.class);
        JavaFXModule.setApplicationClass(binder, Application.class);
    }
}
