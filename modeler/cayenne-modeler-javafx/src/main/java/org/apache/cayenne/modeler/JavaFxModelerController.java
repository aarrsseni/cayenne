package org.apache.cayenne.modeler;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import org.apache.cayenne.modeler.controller.ScreenController;
import org.apache.cayenne.modeler.controller.StatusBarController;
import org.apache.cayenne.modeler.controller.TreeViewController;
import org.apache.cayenne.modeler.event.ProjectDirtyEvent;
import org.apache.cayenne.modeler.services.ProjectService;
import org.apache.cayenne.modeler.util.ModelerUtils;
import org.apache.cayenne.modeler.util.state.ProjectStateUtil;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.project.validation.ProjectValidator;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JavaFxModelerController{

    private static final ProjectStateUtil PROJECT_STATE_UTIL = new ProjectStateUtil();

    @Inject
    public ScreenController screenController;

    @Inject
    public JavaFxModelerActionListener actionListener;

    @Inject
    public ProjectController projectController;

    @Inject
    public ProjectValidator projectValidator;

    @Inject
    public FXMLLoaderFactory fxmlLoaderFactory;

    @Inject
    public ProjectService projectService;

    @Inject
    public StatusBarController statusBarController;

    public TreeViewController treeViewController;

    public void initActionListeners() {
        actionListener.initListeners();
    }

    public void projectOpenedAction(Project project) {

        projectController.setProject(project);

        screenController.getControllersCache().clear();
        screenController.getPanesCache().clear();

        try {
        FXMLLoader loader = fxmlLoaderFactory.getLoader(getClass().getResource("Main.fxml"));
        Pane loadedPane = loader.load();

        screenController.setUnbindableController(loader.getController());
        screenController.updateScene(loadedPane);

        screenController.addController("Main.fxml", loadedPane, loader.getController());

        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Can't load Main panel." + e);
        }

        projectOpened();

        //action manager update actions

        // do status update AFTER the project is actually opened...
        if (projectController.getProject().getConfigurationResource() == null) {
            updateStatus("New project created...");
            screenController.getPrimaryStage().setTitle("[New Project]");
        } else {
            updateStatus("Project opened...");
            screenController.getPrimaryStage().setTitle(projectController.getProject().getConfigurationResource().getURL().getPath());
        }

        //update preferences!
//        PROJECT_STATE_UTIL.fireLastState(projectController);

        // for validation purposes combine load failures with post-load validation (not
        // sure if that'll cause duplicate messages?).
        List<ValidationFailure> allFailures = new ArrayList<>();
        Collection<ValidationFailure> loadFailures = projectController.getProject().getConfigurationTree().getLoadFailures();

        if (!loadFailures.isEmpty()) {
            // mark project as unsaved
            projectController.getProject().setModified(true);
//            projectController.fireEvent(new ProjectDirtyEvent(this,true));
            allFailures.addAll(loadFailures);
        }

        ValidationResult validationResult = projectValidator.validate(projectController.getProject().getRootNode());
        allFailures.addAll(validationResult.getFailures());
        if (!allFailures.isEmpty()) {
            ModelerUtils.showAlert(validationResult);
        }
    }

    public ProjectController getProjectController() {
        return projectController;
    }

    //init display listeners
    public void projectOpened() {

    }

    /**
     * Performs status bar update with a message. Message will dissappear in 6 seconds.
     */
    public void updateStatus(String message) {
        screenController.getPrimaryStage().setTitle(message);
//        statusBarController.updateStatusBar(message);

        // start message cleanup thread that would remove the message after X seconds
//        if (message != null && message.trim().length() > 0) {
//            Thread cleanup = new ExpireThread(message, 6);
//            cleanup.start();
//        }
    }

    public void projectSavedAction() {
        projectController.fireEvent(new ProjectDirtyEvent(this, false));
        projectController.updateProjectControllerPreferences();
        updateStatus("Project saved...");
    }

    public void projectClosedAction() {
        PROJECT_STATE_UTIL.saveLastState(projectController);


//        // --- update view
//        frame.setView(null);
//
//        // repaint is needed, since sometimes there is a
//        // trace from menu left on the screen
//        frame.repaint();
//        frame.setTitle("");

        projectService.projectClosed();

        this.initActionListeners();
//
//        application.getActionManager().projectClosed();

        updateStatus("Project Closed...");
    }
}
