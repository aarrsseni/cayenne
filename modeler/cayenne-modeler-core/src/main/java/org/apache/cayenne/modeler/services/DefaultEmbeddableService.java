package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.event.EmbeddableEvent;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.CreateEmbeddableEvent;
import org.apache.cayenne.modeler.event.EmbeddableDisplayEvent;

public class DefaultEmbeddableService implements EmbeddableService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createEmbeddable() {
        DataMap dataMap = projectController.getCurrentState().getDataMap();

        Embeddable embeddable = new Embeddable();
        String baseName = NameBuilder.builder(embeddable, dataMap).name();
        String nameWithPackage = dataMap.getNameWithDefaultPackage(baseName);
        embeddable.setClassName(nameWithPackage);

        dataMap.addEmbeddable(embeddable);

        projectController.fireEvent(
                new EmbeddableEvent(this, embeddable, MapEvent.ADD, dataMap));
        EmbeddableDisplayEvent displayEvent = new EmbeddableDisplayEvent(
                this,
                embeddable,
                dataMap,
                (DataChannelDescriptor)projectController.getProject().getRootNode());
        displayEvent.setMainTabFocus(true);
        projectController.fireEvent(displayEvent);

        projectController.fireEvent(new CreateEmbeddableEvent(this, dataMap, embeddable));
    }

    public void createEmbeddable(DataMap dataMap, Embeddable embeddable) {
        dataMap.addEmbeddable(embeddable);
        fireEmbeddableEvent(this, projectController, dataMap, embeddable);
    }

    public void removeEmbeddable(DataMap map, Embeddable embeddable) {

        EmbeddableEvent e = new EmbeddableEvent(this, embeddable, MapEvent.REMOVE, map);
        e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());

        map.removeEmbeddable(embeddable.getClassName());
        projectController.fireEvent(e);
    }

    @Override
    public void fireEmbeddableEvent(
            Object src,
            ProjectController mediator,
            DataMap dataMap,
            Embeddable embeddable) {

        mediator.fireEvent(
                new EmbeddableEvent(src, embeddable, MapEvent.ADD, dataMap));
        EmbeddableDisplayEvent displayEvent = new EmbeddableDisplayEvent(
                src,
                embeddable,
                dataMap,
                (DataChannelDescriptor)mediator.getProject().getRootNode());
        displayEvent.setMainTabFocus(true);
        mediator.fireEvent(displayEvent);

    }
}
