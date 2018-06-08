package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.JavaFxModelerController;
import org.apache.cayenne.modeler.controller.ScreenController;
import org.apache.cayenne.modeler.services.OpenProjectService;
import org.apache.cayenne.modeler.services.util.OpenProjectStatus;
import org.apache.cayenne.resource.Resource;

import java.io.File;

public class OpenProjectAction extends ProjectAction {

    @Inject
    private OpenProjectService openProjectService;

    @Inject
    public ScreenController screenController;

    @Inject
    public ProjectController projectController;

    @Inject
    public JavaFxModelerController javaFxModelerController;

    @Override
    public void handle(Event event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File res = fileChooser.showOpenDialog(screenController.getPrimaryStage());

        if (res == null) {
            try {
                // Get the project file name (always cayenne.xml)

            } catch (Exception ex) {

            }
        }

        if (res != null) {
            // by now if the project is unsaved, this has been a user choice...
//            if (projectController != null) {
//                return;
//            }
            if (projectController != null && !closeProject(false)) {
                return;
            }

            openProject(res);
        }
    }

        public void projectOpener(File file){
//        if(projectController != null){
//            return;
//        }

            File f = file;

            if (f == null) {
                try {
                    // Get the project file name (always cayenne.xml)

                } catch (Exception ex) {

                }
            }

            if (f != null) {
                // by now if the project is unsaved, this has been a user choice...
//            if (projectController != null) {
//                return;
//            }

                openProject(f);
            }
        }

        /** Opens specified project file. File must already exist. */
        public void openProject(File file) {
            try {
                OpenProjectStatus status = openProjectService.canOpen(file);

                Resource rootSource = openProjectService.getRootSource(file);
                switch (status.getProjectStatus()) {
                    case ERROR:
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText("Error");
                        alert.setContentText("Error");
                        alert.showAndWait();
                        closeProject(false);
                        return;
                    case UPGRADE_NEEDED:
                        if (processUpgrades()) {
                            rootSource = openProjectService.upgradeResource(rootSource);
                        } else {
                            closeProject(false);
                            return;
                        }
                        break;
                }
                openProjectService.openProjectResourse(rootSource);
            } catch (Exception ex) {

            }
        }

    private boolean processUpgrades() {
        // need an upgrade
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Error");
        alert.setContentText("Error");
        alert.showAndWait();
        return alert.getResult() != ButtonType.NO;
    }
}
