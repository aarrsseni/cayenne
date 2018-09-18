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

import org.apache.cayenne.configuration.xml.DataChannelMetaData;
import org.apache.cayenne.di.Injector;
import org.apache.cayenne.modeler.action.ActionManager;
import org.apache.cayenne.modeler.dialog.LogConsole;
import org.apache.cayenne.modeler.dialog.pref.ClasspathPreferences;
import org.apache.cayenne.modeler.init.platform.PlatformInitializer;
import org.apache.cayenne.modeler.services.PreferenceService;
import org.apache.cayenne.modeler.undo.CayenneUndoManager;
import org.apache.cayenne.modeler.util.WidgetFactory;
import org.apache.cayenne.pref.CayenneProjectPreferences;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.swing.BindingFactory;

import javax.swing.SwingUtilities;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * A main modeler application class that provides a number of services to the Modeler
 * components. Configuration properties:
 * <ul>
 * <li>cayenne.modeler.application.name - name of the application, 'CayenneModeler' is
 * default. Used to locate preferences domain among other things.</li>
 * <li>cayenne.modeler.pref.version - a version of the preferences DB schema. Default is
 * "1.1".</li>
 * </ul>
 */
public class Application {

    public static final String DEFAULT_MESSAGE_BUNDLE = "org.apache.cayenne.modeler.cayennemodeler-strings";

    private static final String APPLICATION_NAME_PROPERTY = "cayenne.modeler.application.name";
    private static final String DEFAULT_APPLICATION_NAME = "CayenneModeler";

    private static Application instance;

    @com.google.inject.Inject
    protected ClassLoadingService modelerClassLoader;

    @com.google.inject.Inject
    protected ProjectController projectController;

    private CayenneModelerController frameController;

    protected String name;

    private BindingFactory bindingFactory;

    private CayenneUndoManager undoManager;

    @com.google.inject.Inject
    protected CayenneProjectPreferences cayenneProjectPreferences;

    @com.google.inject.Inject
    protected PreferenceService preferenceService;

    @com.google.inject.Inject
    protected Injector cayenneInjector;

    @com.google.inject.Inject
    protected com.google.inject.Injector bootiqueInjector;

    @com.google.inject.Inject
    protected PlatformInitializer platformInitializer;

    @com.google.inject.Inject
    protected ActionManager actionManager;

    private DataChannelMetaData metaData;

    public static Application getInstance() {
        return instance;
    }

    public static void setInstance(Application instance) {
        Application.instance = instance;
    }

    // TODO: must be injectable directly in components
    public static WidgetFactory getWidgetFactory() {
        return instance.getInjector().getInstance(WidgetFactory.class);
    }

    // static methods that should probably go away eventually...
    public static CayenneModelerFrame getFrame() {
        return (CayenneModelerFrame) getInstance().getFrameController().getView();
    }

    public Application() {
        String configuredName = System.getProperty(APPLICATION_NAME_PROPERTY);
        this.name = (configuredName != null) ? configuredName : DEFAULT_APPLICATION_NAME;
    }

    public Injector getInjector() {
        return cayenneInjector;
    }

    public com.google.inject.Injector getBootiqueInjector() {
        return bootiqueInjector;
    }

    public Project getProject() {
        return getFrameController().getProjectController().getProject();
    }

    public Preferences getPreferencesNode(Class<?> className, String path) {
        return preferenceService.getCayennePreference().getNode(className, path);
    }

    public String getName() {
        return name;
    }

    public ClassLoadingService getClassLoadingService() {
        return modelerClassLoader;
    }

    /**
     * Returns action controller.
     */
    public ActionManager getActionManager() {
        return actionManager;
    }

    /**
     * Returns undo-edits controller.
     */
    public CayenneUndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * Returns controller for the main frame.
     */
    public CayenneModelerController getFrameController() {
        return frameController;
    }

    /**
     * Starts the application.
     */
    public void startup() {
        // init subsystems

        initClassLoader();

        this.metaData = cayenneInjector.getInstance(DataChannelMetaData.class);
        projectController.setMetaData(metaData);

        actionManager.initAllActions();

        this.bindingFactory = new BindingFactory();

        this.undoManager = new CayenneUndoManager(this);

        this.frameController = new CayenneModelerController(this);

        // open up
        frameController.startupAction();

        // After prefs have been loaded, we can now show the console if needed
        LogConsole.getInstance().showConsoleIfNeeded();
        LogConsole.getInstance().initListeners();

        getFrame().setVisible(true);
    }

    public BindingFactory getBindingFactory() {
        return bindingFactory;
    }

    public CayenneProjectPreferences getCayenneProjectPreferences() {
        return cayenneProjectPreferences;
    }

    public Preferences getMainPreferenceForProject() {
        return preferenceService.getMainPreferenceForProject();
    }

    /**
     * Returns a new instance of CodeTemplateManager.
     */
    public CodeTemplateManager getCodeTemplateManager() {
        return new CodeTemplateManager(this);
    }

    /**
     * Reinitializes ModelerClassLoader from preferences.
     */
    @SuppressWarnings("unchecked")
    public void initClassLoader() {

        // init from preferences...
        Preferences classLoaderPreference = Application.getInstance().getPreferencesNode(
                ClasspathPreferences.class,
                "");

        String[] keys;
        ArrayList<String> values = new ArrayList<>();

        try {
            keys = classLoaderPreference.keys();
            for (String cpKey : keys) {
            	values.add(classLoaderPreference.get(cpKey, ""));
            }
        } catch (BackingStoreException ignored) {
        }

        Collection<String> details = new ArrayList<>(values);

        if (details.size() > 0) {
            modelerClassLoader.setPathFiles(details.stream().map(File::new).collect(Collectors.toList()));
        }



        // set as EventDispatch thread default class loader
        if (SwingUtilities.isEventDispatchThread()) {
            Thread.currentThread().setContextClassLoader(modelerClassLoader.getClassLoader());
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    Thread.currentThread().setContextClassLoader(modelerClassLoader.getClassLoader());
                }
            });
        }
    }

    public DataChannelMetaData getMetaData() {
        return metaData;
    }

    protected void initPreferences() {
        this.cayenneProjectPreferences = new CayenneProjectPreferences();
    }

    PlatformInitializer getPlatformInitializer() {
        return platformInitializer;
    }

    public ProjectController getProjectController() {
        return projectController;
    }
}
