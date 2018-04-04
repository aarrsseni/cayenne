/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler;


import com.google.inject.Inject;
import io.bootique.annotation.Args;
import org.apache.cayenne.modeler.action.OpenProjectAction;
import org.apache.cayenne.modeler.dialog.pref.GeneralPreferences;
import org.apache.cayenne.modeler.init.platform.PlatformInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import java.io.File;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @since 4.1
 */
public class GenericLauncher implements Launcher {

    private static Logger logger = LoggerFactory.getLogger(GenericLauncher.class);

    public GenericLauncher(){
    }

    @Inject
    private Application application;

    @Args
    @Inject
    private String[] args;

    @Inject
    private PlatformInitializer platformInitializer;

    @Override
    public void launch() {
        // TODO: use module auto-loading...
        // init look and feel before using any Swing classes...
        platformInitializer.initLookAndFeel();

        // logger should go after Look And Feel or Logger Console will be without style
        logger.info("Starting CayenneModeler.");
        logger.info("JRE v."
                + System.getProperty("java.version")
                + " at "
                + System.getProperty("java.home"));

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {


                Application.setInstance(application);
                application.startup();

                // start initial project AFTER the app startup, as we need Application
                // preferences to be bootstrapped.

                File project = initialProjectFromArgs(args);
                if (project == null) {
                    project = initialProjectFromPreferences();
                }

                if (project != null) {
                    new OpenProjectAction().openProject(project);
                }
            }
        });

    }

    protected File initialProjectFromPreferences() {

        Preferences autoLoadLastProject = Application.getInstance().getPreferencesNode(GeneralPreferences.class, "");
        if ((autoLoadLastProject != null)
                && autoLoadLastProject.getBoolean(GeneralPreferences.AUTO_LOAD_PROJECT_PREFERENCE, false)) {
            List<File> lastFiles = ModelerPreferences.getLastProjFiles();
            if (!lastFiles.isEmpty()) {
                return lastFiles.get(0);
            }
        }

        return null;
    }

    protected File initialProjectFromArgs(String[] args) {
        if (args != null && args.length == 1) {
            File f = new File(args[0]);

            if (f.isFile()
                    && f.getName().startsWith("cayenne")
                    && f.getName().endsWith(".xml")) {
                return f;
            }
        }

        return null;
    }

}
