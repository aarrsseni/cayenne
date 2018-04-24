package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.services.SaveService;
import org.apache.cayenne.project.Project;

public class SaveAction extends SaveAsAction {

    @Inject
    public SaveService saveService;

    @Inject
    public ProjectController projectController;

    protected boolean saveAll() throws Exception {
        Project p = projectController.getProject();
        if (p == null || p.getConfigurationResource() == null) {
            return super.saveAll();
        }

        return saveService.saveAll();
    }
}
