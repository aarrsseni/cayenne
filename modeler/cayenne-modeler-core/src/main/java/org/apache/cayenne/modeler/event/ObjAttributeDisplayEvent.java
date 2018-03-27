package org.apache.cayenne.modeler.event;

import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Entity;

public class ObjAttributeDisplayEvent extends AttributeDisplayEvent{

    public ObjAttributeDisplayEvent(Object src, Attribute attribute, Entity entity, DataMap dataMap, DataChannelDescriptor domain) {
        super(src, attribute, entity, dataMap, domain);
    }

    public ObjAttributeDisplayEvent(Object src, Attribute[] attributes, Entity entity, DataMap dataMap, DataChannelDescriptor domain) {
        super(src, attributes, entity, dataMap, domain);
    }

}
