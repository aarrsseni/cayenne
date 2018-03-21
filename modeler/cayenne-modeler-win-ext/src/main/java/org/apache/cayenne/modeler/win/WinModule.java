package org.apache.cayenne.modeler.win;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.apache.cayenne.modeler.init.platform.PlatformInitializer;

public class WinModule implements Module{

    @Override
    public void configure(Binder binder) {
        binder.bind(PlatformInitializer.class).to(WinPlatformInitializer.class);
    }

}
