package org.apache.cayenne.modeler;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericJavaFXLauncher implements JavaFXLauncher {

    private static Logger logger = LoggerFactory.getLogger(GenericJavaFXLauncher.class);

    @Inject
    public BaseApplication baseApplication;

    @Override
    public void launch(Class<? extends BQApplication> app) {
        logger.info("Starting CayenneModeler.");
        logger.info("JRE v."
                + System.getProperty("java.version")
                + " at "
                + System.getProperty("java.home"));

        baseApplication.startup(app);
    }
}
