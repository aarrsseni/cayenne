package org.apache.cayenne.modeler;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.apache.cayenne.modeler.init.platform.GenericPlatformInitializer;
import org.apache.cayenne.modeler.init.platform.PlatformInitializer;

public class BaseUiModule implements Module{

    @Override
    public void configure(Binder binder) {
        binder.bind(PlatformInitializer.class).to(GenericPlatformInitializer.class);
    }

}
