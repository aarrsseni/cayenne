package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.ConfigurationTree;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.ProjectOpenEvent;
import org.apache.cayenne.project.Project;

public class DefaultProjectService implements ProjectService {

    @Inject
    protected ProjectController projectController;

    public DefaultProjectService(){}

    @Override
    public void newProject() {
        DataChannelDescriptor dataChannelDescriptor = new DataChannelDescriptor();

        dataChannelDescriptor.setName(NameBuilder
                .builder(dataChannelDescriptor)
                .name());

        Project project = new Project(
                new ConfigurationTree<DataChannelDescriptor>(dataChannelDescriptor));

        projectController.fireEvent(new ProjectOpenEvent(this, project));

        // select default domain
        projectController.fireEvent(new DomainDisplayEvent(this, dataChannelDescriptor));
    }
}
