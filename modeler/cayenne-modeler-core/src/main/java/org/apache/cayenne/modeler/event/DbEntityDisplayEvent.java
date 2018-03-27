package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Entity;

public class DbEntityDisplayEvent extends EntityDisplayEvent{

    public DbEntityDisplayEvent(Object src, Entity entity) {
        super(src, entity);
    }

    public DbEntityDisplayEvent(Object src, Entity entity, DataMap map, DataChannelDescriptor dataChannelDescriptor) {
        super(src, entity, map, dataChannelDescriptor);
    }

    public DbEntityDisplayEvent(Object src, Entity entity, DataMap map, DataNodeDescriptor node,
                              DataChannelDescriptor dataChannelDescriptor) {
        super(src, entity, map, node, dataChannelDescriptor);
    }
}
