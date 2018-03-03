package org.apache.cayenne.modeler;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;
import org.apache.cayenne.CayenneModelerCore;

public class UiMain implements Module{

    //not use exit because UI is in another thread
    public static void main(String[] args) {
        Bootique.app(args).autoLoadModules()
                .module(UiMain.class)
                .module(CayenneModelerCore.class)
                .module(ModelerUiModule.class)
                .exec();
    }

    public void configure(Binder binder) {
        BQCoreModule.extend(binder).setDefaultCommand(UiCommand.class);
    }

}
