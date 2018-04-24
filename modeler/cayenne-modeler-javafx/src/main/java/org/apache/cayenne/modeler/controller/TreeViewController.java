package org.apache.cayenne.modeler.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.modeler.BQApplication;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DomainDisplayListener;
import org.apache.cayenne.modeler.observer.ObserverDictionary;
import org.apache.cayenne.modeler.util.IconUtil;

public class TreeViewController implements Unbindable, DomainDisplayListener{

    private static final String UNDEFINED_MESSAGE = "undefined";

    @FXML
    TreeView treeView;

    public ProjectController projectController;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        treeView.setEditable(true);

        //TODO init when root actually created
        TreeItem<DataChannelDescriptor> item = new TreeItem<>();
        treeView.setRoot(item);

        projectController = BQApplication.getInjector().getInstance(ProjectController.class);

        initListeners();

        treeView.setCellFactory(new Callback<TreeView, TreeCell>() {
            @Override
            public TreeCell<Object> call(TreeView param) {
                return new TreeFieldTreeCellImpl();
            }
        });
    }

    @Override
    public void unbind() {

    }

    public void initListeners() {
        projectController.getEventController().addListener(DomainDisplayListener.class, this);
    }

    @Override
    public void currentDomainChanged(DomainDisplayEvent e) {
        treeView.getRoot().setValue(e.getDomain());
    }

    //TODO move to class with static method?
    private String getTextByItemType(Object item) {
        if (item.getClass() == DataChannelDescriptor.class) {
            return ((DataChannelDescriptor) item).getName();
        }
        return UNDEFINED_MESSAGE;
    }

    class TreeFieldTreeCellImpl extends TreeCell {

        @Override
        public void updateItem(Object item, boolean empty) {

            setGraphic(IconUtil.imageForObject(item));

            super.updateItem(item, empty);
            if (!empty) {
                ObserverDictionary.getObserver(item).bind("name", textProperty());
                setText(getTextByItemType(item));
            }
        }
    }
}
