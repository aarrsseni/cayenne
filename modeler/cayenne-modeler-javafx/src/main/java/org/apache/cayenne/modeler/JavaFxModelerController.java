package org.apache.cayenne.modeler;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import org.apache.cayenne.modeler.controller.ScreenController;
import org.apache.cayenne.modeler.controller.TreeViewController;
import org.apache.cayenne.modeler.event.ProjectDirtyEvent;
import org.apache.cayenne.modeler.util.state.ProjectStateUtil;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.project.validation.ProjectValidator;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;

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

    public TreeViewController treeViewController;

    public void initActionListeners() {
        actionListener.initListeners();
    }

    public void projectOpenedAction(Project project) {
        projectController.setProject(project);

        try {
        FXMLLoader loader = fxmlLoaderFactory.getLoader(getClass().getResource("Main.fxml"));
        Pane loadedPane = loader.load();

        screenController.setUnbindableController(loader.getController());
        screenController.updateScene(loadedPane);

        screenController.addController("Main.fxml", loadedPane, loader.getController());

        } catch (IOException e) {
            e.printStackTrace();
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
//            ValidatorDialog.showDialog(frame, validationResult.getFailures());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Look, an Information Dialog");
            alert.setContentText("I have a great message for you!");

            alert.showAndWait();
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

        // start message cleanup thread that would remove the message after X seconds
        if (message != null && message.trim().length() > 0) {
            Thread cleanup = new ExpireThread(message, 6);
            cleanup.start();
        }
    }

    public void projectSavedAction() {
        projectController.fireEvent(new ProjectDirtyEvent(this, false));
        projectController.updateProjectControllerPreferences();
        updateStatus("Project saved...");
//        frame.setTitle(projectController.getProject().getConfigurationResource().getURL().getPath());
    }

    class ExpireThread extends Thread {

        int seconds;
        protected String message;

        ExpireThread(String message, int seconds) {
            this.seconds = seconds;
            this.message = message;
        }

        @Override
        public void run() {
            try {
                sleep(seconds * 1000);
            } catch (InterruptedException e) {
                // ignore exception
            }

            if (message.equals(screenController.getPrimaryStage().getTitle())) {
                updateStatus(null);
            }
        }
    }
}
