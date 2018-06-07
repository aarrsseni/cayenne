package org.apache.cayenne.modeler;

import com.google.inject.Inject;
import javafx.application.Application;
import org.apache.cayenne.modeler.controller.JavaFxModelerController;

public class BaseApplication {

    @Inject
    public ClassLoadingService classLoadingService;

    @Inject
    public JavaFxModelerController modelerController;

    public void startup(Class<? extends BQApplication> app) {
        initClassLoader();

        //action manager initActions

        modelerController.initActionListeners();

        //log-console

        Application.launch(app);
    }

    public void initClassLoader() {

    }

    public JavaFxModelerController getModelerController() {
        return modelerController;
    }
}
