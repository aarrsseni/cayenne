package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.modeler.event.listener.DbAttributeDisplayListener;

import java.util.EventListener;

public class DbAttributeDisplayEvent extends AttributeDisplayEvent{

    public DbAttributeDisplayEvent(Object src, Attribute attribute, Entity entity, DataMap dataMap, DataChannelDescriptor domain) {
        super(src, attribute, entity, dataMap, domain);
    }

    public DbAttributeDisplayEvent(Object src, Attribute[] attributes, Entity entity, DataMap dataMap, DataChannelDescriptor domain) {
        super(src, attributes, entity, dataMap, domain);
    }

    public Class<? extends EventListener> getEventListener() {
        return DbAttributeDisplayListener.class;
    }
}
