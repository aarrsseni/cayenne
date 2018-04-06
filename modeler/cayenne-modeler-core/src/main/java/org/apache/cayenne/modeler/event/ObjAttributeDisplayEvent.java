package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.modeler.event.listener.ObjAttributeDisplayListener;

import java.util.EventListener;

public class ObjAttributeDisplayEvent extends AttributeDisplayEvent{

    public ObjAttributeDisplayEvent(Object src, Attribute attribute, Entity entity, DataMap dataMap, DataChannelDescriptor domain) {
        super(src, attribute, entity, dataMap, domain);
    }

    public ObjAttributeDisplayEvent(Object src, Attribute[] attributes, Entity entity, DataMap dataMap, DataChannelDescriptor domain) {
        super(src, attributes, entity, dataMap, domain);
    }

    public Class<? extends EventListener> getEventListener() {
        return ObjAttributeDisplayListener.class;
    }
}
