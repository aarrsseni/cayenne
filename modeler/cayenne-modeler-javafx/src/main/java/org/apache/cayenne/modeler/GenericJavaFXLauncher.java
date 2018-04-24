package org.apache.cayenne.modeler;

import com.google.inject.Inject;

public class GenericJavaFXLauncher implements JavaFXLauncher {

    @Inject
    public BaseApplication baseApplication;

    @Override
    public void launch(Class<? extends BQApplication> app) {
        baseApplication.startup(app);
    }
}
