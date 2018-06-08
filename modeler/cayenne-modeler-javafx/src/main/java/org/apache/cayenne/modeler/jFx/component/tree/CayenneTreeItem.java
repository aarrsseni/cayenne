package org.apache.cayenne.modeler.jFx.component.tree;

import javafx.scene.control.TreeItem;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.modeler.controller.Unbindable;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.modeler.util.IconUtil;

public class CayenneTreeItem extends TreeItem<String> implements Unbindable{

    private static final String UNDEFINED_MESSAGE = "undefined";

    private Object currentObject;

    public CayenneTreeItem(Object currentObject) {
        this.currentObject = currentObject;

        this.setValue(getTextByItemType(currentObject));
        this.setGraphic(IconUtil.imageForObject(currentObject));

        this.setExpanded(true);

        bind();
    }

    public Object getCurrentObject() {
        return currentObject;
    }

    //TODO move to class with static method?
    private String getTextByItemType(Object item) {
        if (item.getClass() == DataChannelDescriptor.class) {
            return ((DataChannelDescriptor) item).getName();
        } else if(item.getClass() == DataMap.class){
            return ((DataMap) item).getName();
        } else if(item.getClass() == DbEntity.class){
            return ((DbEntity) item).getName();
        }
        return UNDEFINED_MESSAGE;
    }

    @Override
    public void bind() {
        ObserverDictionary.getObserver(currentObject).bind("name", valueProperty());
    }

    @Override
    public void unbind() {}
}
