package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.GenerateCodeEvent;
import org.apache.cayenne.project.Project;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultGenerateCodeService implements GenerateCodeService {

    @Inject
    public ProjectController projectController;

    @Override
    public void generateCode() {
        Collection<DataMap> dataMaps;
        DataMap dataMap = projectController.getCurrentState().getDataMap();

        if (dataMap != null) {
            dataMaps = new ArrayList<>();
            dataMaps.add(dataMap);

            projectController.fireEvent(new GenerateCodeEvent(this, dataMaps));
        } else if (projectController.getCurrentState().getNode() != null) {
            Collection<String> nodeMaps = projectController.getCurrentState().getNode().getDataMapNames();
            Project project = projectController.getProject();
            dataMaps = ((DataChannelDescriptor) project.getRootNode()).getDataMaps();

            Collection<DataMap> resultMaps = new ArrayList<>();
            for (DataMap map : dataMaps) {
                if (nodeMaps.contains(map.getName())) {
                    resultMaps.add(map);
                }
            }

            projectController.fireEvent(new GenerateCodeEvent(this, resultMaps));
        } else {
            Project project = projectController.getProject();
            dataMaps = ((DataChannelDescriptor) project.getRootNode()).getDataMaps();

            projectController.fireEvent(new GenerateCodeEvent(this, dataMaps));
        }
    }
}
