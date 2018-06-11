package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import org.apache.cayenne.configuration.BaseConfigurationNodeVisitor;
import org.apache.cayenne.configuration.ConfigurationNode;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.DataMapDisplayEvent;
import org.apache.cayenne.modeler.event.DbEntityDisplayEvent;
import org.apache.cayenne.modeler.event.DomainDisplayEvent;
import org.apache.cayenne.modeler.event.ObjEntityDisplayEvent;
import org.apache.cayenne.modeler.event.listener.DataMapDisplayListener;
import org.apache.cayenne.modeler.event.listener.DbEntityDisplayListener;
import org.apache.cayenne.modeler.event.listener.DomainDisplayListener;
import org.apache.cayenne.modeler.event.listener.ObjEntityDisplayListener;
import org.apache.cayenne.modeler.jFx.component.factory.CayenneTreeFactory;
import org.apache.cayenne.modeler.jFx.component.tree.CayenneTreeItem;
import org.apache.cayenne.project.Project;

import java.util.ArrayList;
import java.util.List;

public class TreeViewController implements Unbindable, DomainDisplayListener, DataMapDisplayListener, DbEntityDisplayListener, ObjEntityDisplayListener {

    @FXML
    private TreeView<String> treeView;

    @Inject
    private CayenneTreeFactory cayenneTreeFactory;

    @Inject
    public ProjectController projectController;

    public TreeViewController() {

    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        cayenneTreeFactory.setTreeView(treeView);

        initListeners();

        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            CayenneTreeItem selectedItem = (CayenneTreeItem) newValue;
            processSelection(selectedItem);
            treeView.getSelectionModel().select(selectedItem);
            selectedItem.bind();
        });
    }

    private void initFromModel(Project project) {
        DataChannelDescriptor dataChannelDescriptor = (DataChannelDescriptor)project.getConfigurationTree().getRootNode();
        cayenneTreeFactory.createTreeItem(dataChannelDescriptor);
        for(DataMap dataMap : dataChannelDescriptor.getDataMaps()){
            cayenneTreeFactory.createTreeItem(dataChannelDescriptor, dataMap);
            for(DbEntity dbEntity : dataMap.getDbEntities()){
                cayenneTreeFactory.createTreeItem(dataChannelDescriptor, dataMap, dbEntity);
            }
            for(ObjEntity objEntity : dataMap.getObjEntities()){
                cayenneTreeFactory.createTreeItem(dataChannelDescriptor, dataMap, objEntity);
            }
        }
        treeView.getSelectionModel().select(treeView.getRoot());
    }

    @Override
    public void bind() {
        initFromModel(projectController.getProject());
    }

    @Override
    public void unbind() {

    }

    public void initListeners() {
        projectController.getEventController().addListener(DomainDisplayListener.class, this);
        projectController.getEventController().addListener(DataMapDisplayListener.class, this);
        projectController.getEventController().addListener(DbEntityDisplayListener.class, this);
        projectController.getEventController().addListener(ObjEntityDisplayListener.class, this);
    }

    @Override
    public void currentDomainChanged(DomainDisplayEvent e) {
        if(treeView.getRoot() != null) {
            return;
        }

        cayenneTreeFactory.createTreeItem(e.getDomain());
    }

    @Override
    public void currentDataMapChanged(DataMapDisplayEvent e) {
        if ((e.getSource() == this || !e.isDataMapChanged()) && !e.isRefired()) {
            return;
        }
        cayenneTreeFactory.createTreeItem(e.getDomain(), e.getDataMap());
    }


    /**
     * Returns array of the user objects ending with this and starting with one under
     * root. That is the array of actual objects rather than wrappers.
     */
    private Object[] getUserObjects(CayenneTreeItem node) {
        List<Object> list = new ArrayList<>();
        while (node != treeView.getRoot()) {
            list.add(0, node.getCurrentObject());
            node = (CayenneTreeItem) node.getParent();
        }
        return list.toArray();
    }

    /**
     * Processes node selection regardless of whether a new node was selected, or an
     * already selected node was clicked again. Normally called from event listener
     * methods.
     */
    private void processSelection(CayenneTreeItem treeItem) {
        if (treeItem == null) {
            return;
        }

        Object[] data = getUserObjects(treeItem);

        if (data.length == 0) {
            // this should clear the right-side panel
            DomainDisplayEvent domEvent = new DomainDisplayEvent(this, null);
            domEvent.setDomain((DataChannelDescriptor) projectController
                    .getProject()
                    .getRootNode());
            projectController.fireEvent(domEvent);
            return;
        }

        Object obj = data[data.length - 1];
        ConfigurationNode node = (ConfigurationNode)obj;

        node.acceptVisitor(new BaseConfigurationNodeVisitor<Void>() {
            @Override
            public Void visitDataChannelDescriptor(DataChannelDescriptor channelDescriptor) {
                projectController.fireEvent(new DomainDisplayEvent(
                        this,
                        (DataChannelDescriptor) obj));
                return null;
            }

            @Override
            public Void visitDataNodeDescriptor(DataNodeDescriptor nodeDescriptor) {
                return null;
            }

            @Override
            public Void visitDataMap(DataMap dataMap) {
                if (data.length == 2) {
                    projectController.fireEvent(new DataMapDisplayEvent(
                            this,
                            (DataMap) obj,
                            (DataChannelDescriptor) projectController.getProject().getRootNode(),
                            (DataNodeDescriptor) data[data.length - 2]));
                } else if (data.length == 1) {
                    projectController.fireEvent(new DataMapDisplayEvent(
                            this,
                            (DataMap) obj,
                            (DataChannelDescriptor) projectController.getProject().getRootNode()));
                }
                return null;
            }

            @Override
            public Void visitObjEntity(ObjEntity entity) {
                ObjEntityDisplayEvent e = new ObjEntityDisplayEvent(this, (Entity) obj);
                e.setUnselectAttributes(true);
                if (data.length == 3) {
                    e.setDataMap((DataMap) data[data.length - 2]);
                    e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());
                    e.setDataNode((DataNodeDescriptor) data[data.length - 3]);
                } else if (data.length == 2) {
                    e.setDataMap((DataMap) data[data.length - 2]);
                    e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());
                }
                projectController.fireEvent(e);
                return null;
            }

            @Override
            public Void visitDbEntity(DbEntity entity) {
                DbEntityDisplayEvent e = new DbEntityDisplayEvent(this, (Entity) obj);
                e.setUnselectAttributes(true);
                if (data.length == 3) {
                    e.setDataMap((DataMap) data[data.length - 2]);
                    e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());
                    e.setDataNode((DataNodeDescriptor) data[data.length - 3]);
                } else if (data.length == 2) {
                    e.setDataMap((DataMap) data[data.length - 2]);
                    e.setDomain((DataChannelDescriptor) projectController.getProject().getRootNode());
                }
                projectController.fireEvent(e);
                return null;
            }

            @Override
            public Void visitEmbeddable(Embeddable embeddable) {
                return null;
            }

            @Override
            public Void visitProcedure(Procedure procedure) {
                return null;
            }

            @Override
            public Void visitQuery(QueryDescriptor query) {
                return null;
            }
        });
    }

    @Override
    public void currentDbEntityChanged(DbEntityDisplayEvent e) {
        e.setEntityChanged(true);

        if ((e.getSource() == this || !e.isEntityChanged()) && !e.isRefired()) {
            return;
        }

        cayenneTreeFactory.createTreeItem(e.getDomain(), e.getDataMap(), e.getEntity());
    }

    @Override
    public void currentObjEntityChanged(ObjEntityDisplayEvent e) {
        e.setEntityChanged(true);

        if ((e.getSource() == this || !e.isEntityChanged()) && !e.isRefired()) {
            return;
        }

        cayenneTreeFactory.createTreeItem(e.getDomain(), e.getDataMap(), e.getEntity());
    }
}
