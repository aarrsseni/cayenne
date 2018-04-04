package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.GenerateDbEvent;
import org.apache.cayenne.project.Project;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultGenerateDbService implements GenerateDbService {

    @Inject
    public ProjectController projectController;

    @Override
    public void generateDb() {
        Collection<DataMap> dataMaps;
        DataMap dataMap = projectController.getCurrentState().getDataMap();

        if (dataMap != null) {
            dataMaps = new ArrayList<>();
            dataMaps.add(dataMap);
        } else {
            Project project = projectController.getProject();
            dataMaps = ((DataChannelDescriptor) project.getRootNode()).getDataMaps();
        }
        projectController.fireEvent(new GenerateDbEvent(this, dataMaps, projectController));
    }
}
