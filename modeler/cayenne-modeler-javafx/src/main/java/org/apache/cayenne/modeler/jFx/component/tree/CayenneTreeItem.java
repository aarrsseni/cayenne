package org.apache.cayenne.modeler.jFx.component.tree;

import javafx.scene.control.TreeItem;
import org.apache.cayenne.configuration.BaseConfigurationNodeVisitor;
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.modeler.controller.Unbindable;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.modeler.util.IconUtil;

public class CayenneTreeItem extends TreeItem<String> implements Unbindable{

    private static final String UNDEFINED_MESSAGE = "undefined";

    private Object currentObject;

    public CayenneTreeItem(Object currentObject) {
        this.currentObject = currentObject;

        this.setValue(getNameByItemType(currentObject));
        this.setGraphic(IconUtil.imageForObject(currentObject));

        this.setExpanded(true);

        bind();
    }

    public Object getCurrentObject() {
        return currentObject;
    }

    //TODO move to class with static method?
    private String getNameByItemType(Object item) {
        ConfigurationNode node = (ConfigurationNode)item;
        node.acceptVisitor(new BaseConfigurationNodeVisitor<String>() {
            @Override
            public String visitDataChannelDescriptor(DataChannelDescriptor channelDescriptor) {
                return channelDescriptor.getName();
            }

            @Override
            public String visitDataNodeDescriptor(DataNodeDescriptor nodeDescriptor) {
                return null;
            }

            @Override
            public String visitDataMap(DataMap dataMap) {
                return dataMap.getName();
            }

            @Override
            public String visitObjEntity(ObjEntity entity) {
                return entity.getName();
            }

            @Override
            public String visitDbEntity(DbEntity entity) {
                return entity.getName();
            }
        });
        return UNDEFINED_MESSAGE;
    }

    @Override
    public void bind() {
        ObserverDictionary.getObserver(currentObject).bind("name", valueProperty());
    }

    @Override
    public void unbind() {}
}
