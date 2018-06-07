package org.apache.cayenne.modeler.jFx.component.factory;

import javafx.scene.control.TreeView;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.modeler.jFx.component.tree.CayenneTreeItem;

import java.util.ArrayList;
import java.util.List;

public class CayenneTreeFactory {

    private TreeView treeView;

    public List<CayenneTreeItem> treeItemList = new ArrayList<>();

    public List<Object> treeObjects = new ArrayList<>();

    public void setTreeView(TreeView treeView) {
        this.treeView = treeView;
    }

    public void createTreeItem(Object... objects) {
        if(objects.length == 0 || objects == null) {
            return;
        }

        CayenneTreeItem parentNode = (CayenneTreeItem) treeView.getRoot();

        CayenneTreeItem item = new CayenneTreeItem(objects[objects.length - 1]);

        if (treeView.getRoot() == null && objects[0] instanceof DataChannelDescriptor) {
            treeView.setRoot(item);
            return;
        }

        if(!treeObjects.contains(item.getCurrentObject())) {

            int start = 0;
            if (parentNode.getCurrentObject() == objects[0]) {
                start = 1;
            }

            for (int i = start; i < objects.length - 1; i++) {
                CayenneTreeItem foundNode = null;
                for (Object node : parentNode.getChildren()) {
                    if (((CayenneTreeItem) node).getCurrentObject() == objects[i]) {
                        foundNode = (CayenneTreeItem) node;
                        break;
                    }
                }
                if (foundNode == null) {
                    return;
                } else {
                    parentNode = foundNode;
                }
            }
            treeItemList.add(item);
            treeObjects.add(item.getCurrentObject());
            parentNode.getChildren().add(item);
            treeView.getSelectionModel().select(item);
        }



    }

    public List<CayenneTreeItem> getTreeItemList() {
        return treeItemList;
    }
}
