package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import javafx.event.Event;
import javafx.stage.DirectoryChooser;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.modeler.JavaFxModelerController;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.controller.ScreenController;
import org.apache.cayenne.modeler.event.ProjectOnSaveEvent;
import org.apache.cayenne.modeler.services.SaveService;
import org.apache.cayenne.modeler.util.AbstractCayenneAction;
import org.apache.cayenne.modeler.util.ModelerUtils;
import org.apache.cayenne.project.validation.ProjectValidator;
import org.apache.cayenne.validation.ValidationResult;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SaveAsAction extends AbstractCayenneAction {

    @Inject
    public SaveService saveService;

    @Inject
    public ScreenController screenController;

    @Inject
    public ProjectController projectController;

    @Inject
    public JavaFxModelerController javaFxModelerController;

    protected boolean saveAll() throws Exception {

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Open Resource File");
        File projectDir = chooser.showDialog(screenController.getPrimaryStage());

        if (projectDir == null) {
            return false;
        }

        return saveService.saveAll(projectDir);
    }

    @Override
    public void handle(Event event) {
        ProjectValidator projectValidator = projectController.getInjector().getInstance(ProjectValidator.class);
        ValidationResult validationResult = projectValidator.validate(projectController.getProject().getRootNode());

        projectController.fireEvent(new ProjectOnSaveEvent(SaveAsAction.class));
        try {
            if (!saveAll()) {
                return;
            }
        } catch (Exception ex) {
            LoggerFactory.getLogger(getClass()).error("Error on save", ex);
            throw new CayenneRuntimeException("Error on save", ex);
        }

        javaFxModelerController.projectSavedAction();

        // If there were errors or warnings at validation, display them
        if (validationResult.getFailures().size() > 0) {
            ModelerUtils.showAlert(validationResult);
        }
    }
}
