package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.pref.RenamedPreferences;
import org.apache.cayenne.project.Project;
import org.apache.cayenne.project.ProjectSaver;

public class DefaultSaveService implements SaveService {

    @Inject
    public ProjectController projectController;

    @Override
    public void saveAll(Project p) {
        String oldPath = p.getConfigurationResource().getURL().getPath();

        projectController.getFileChangeTracker().pauseWatching();

        ProjectSaver saver = projectController.getInjector().getInstance(ProjectSaver.class);
        saver.save(p);

        RenamedPreferences.removeOldPreferences();

        // if change DataChanelDescriptor name - as result change name of xml file
        // we will need change preferences path
        String[] path = oldPath.split("/");
        String[] newPath = p.getConfigurationResource().getURL().getPath().split("/");

        if (!path[path.length - 1].equals(newPath[newPath.length - 1])) {
            String newName = newPath[newPath.length - 1].replace(".xml", "");
            RenamedPreferences.copyPreferences(newName, projectController.getPreferenceForProject());
            RenamedPreferences.removeOldPreferences();
        }
    }
}
