package org.apache.cayenne.modeler.win;

import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQCoreModule;
import io.bootique.Bootique;
import org.apache.cayenne.CayenneModelerCore;
import org.apache.cayenne.modeler.CayenneModelerUi;
import org.apache.cayenne.modeler.UiCommand;

public class WinMain implements Module{

    public static void main(String[] args) {
        Bootique.app(args).autoLoadModules()
                .module(WinMain.class)
                .module(CayenneModelerCore.class)
                .module(CayenneModelerUi.class)
                .module(WinModule.class)
                .exec();
    }

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder).setDefaultCommand(UiCommand.class);
    }
}
