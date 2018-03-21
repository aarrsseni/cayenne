package org.apache.cayenne.modeler.osx;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;
import org.apache.cayenne.CayenneModelerCore;
import org.apache.cayenne.modeler.CayenneModelerUi;
import org.apache.cayenne.modeler.UiCommand;

public class OSXMain implements Module{

    public static void main(String[] args) {
        Bootique.app(args).autoLoadModules()
                .module(OSXMain.class)
                .module(CayenneModelerCore.class)
                .module(CayenneModelerUi.class)
                .module(OSXModule.class)
                .exec();
    }

    public void configure(Binder binder) {
        BQCoreModule.extend(binder).setDefaultCommand(UiCommand.class);
    }

}
