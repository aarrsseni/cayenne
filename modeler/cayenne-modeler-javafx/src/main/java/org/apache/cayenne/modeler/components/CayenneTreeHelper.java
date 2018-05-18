package org.apache.cayenne.modeler.components;

import javafx.scene.control.TreeView;
import org.apache.cayenne.configuration.DataChannelDescriptor;

import java.util.ArrayList;
import java.util.List;

public class CayenneTreeHelper {

    private TreeView treeView;

    public List<CayenneTreeItem> list = new ArrayList<>();

    public void setTreeView(TreeView treeView) {
        this.treeView = treeView;
    }

    public void createTreeItem(Object... objects) {
        if(objects.length == 0 || objects == null) {
            return;
        }

        CayenneTreeItem parentNode = (CayenneTreeItem) treeView.getRoot();

        CayenneTreeItem item = new CayenneTreeItem(objects[objects.length - 1]);

        if(treeView.getRoot() == null && objects[0] instanceof DataChannelDescriptor) {
            treeView.setRoot(item);
            return;
        }

        int start = 0;
        if(parentNode.getCurrentObject() == objects[0]) {
            start = 1;
        }

        for(int i = start; i < objects.length - 1; i++) {
            CayenneTreeItem foundNode = null;
            for(Object node : parentNode.getChildren()) {
                if(((CayenneTreeItem)node).getCurrentObject() == objects[i]) {
                    foundNode = (CayenneTreeItem) node;
                    break;
                }
            }
            if(foundNode == null) {
                return;
            } else {
                parentNode = foundNode;
            }
        }
        list.add(item);
        parentNode.getChildren().add(item);

        treeView.getSelectionModel().select(item);

    }

}
