package org.apache.cayenne.modeler.generic;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;
import org.apache.cayenne.CayenneModelerCore;
import org.apache.cayenne.modeler.CayenneModelerUi;
import org.apache.cayenne.modeler.UiCommand;

public class GenericMain implements Module{
    public static void main(String[] args) {
        Bootique.app(args).autoLoadModules()
                .module(GenericMain.class)
                .module(CayenneModelerCore.class)
                .module(CayenneModelerUi.class)
                .module(GenericModule.class)
                .exec();
    }

    public void configure(Binder binder) {
        BQCoreModule.extend(binder).setDefaultCommand(UiCommand.class);
    }
}
