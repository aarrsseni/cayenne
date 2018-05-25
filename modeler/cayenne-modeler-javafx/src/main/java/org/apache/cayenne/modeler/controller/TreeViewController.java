package org.apache.cayenne.modeler.controller;

import com.google.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.event.DataMapEvent;
import org.apache.cayenne.configuration.event.DataMapListener;
import org.apache.cayenne.map.*;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.event.*;
import org.apache.cayenne.modeler.event.listener.DataMapDisplayListener;
import org.apache.cayenne.modeler.event.listener.DbEntityDisplayListener;
import org.apache.cayenne.modeler.event.listener.DomainDisplayListener;
import org.apache.cayenne.modeler.components.CayenneTreeHelper;
import org.apache.cayenne.modeler.components.CayenneTreeItem;
import org.apache.cayenne.modeler.event.listener.ObjEntityDisplayListener;

import java.util.ArrayList;
import java.util.List;

public class TreeViewController implements Unbindable, DomainDisplayListener, DataMapDisplayListener, DataMapListener, DbEntityDisplayListener, ObjEntityDisplayListener {

    @FXML
    TreeView treeView;

    @Inject
    private CayenneTreeHelper cayenneTreeHelper;

    @Inject
    public ProjectController projectController;

    public TreeViewController() {

    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        cayenneTreeHelper.setTreeView(treeView);

        initListeners();

        treeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue,
                                Object newValue) {
                CayenneTreeItem selectedItem = (CayenneTreeItem) newValue;

                selectedItem.bind();

                processSelection(selectedItem);
            }
        });
    }

    @Override
    public void bind() {
        System.out.println("Bind treeViewController");
    }

    @Override
    public void unbind() {
        System.out.println("Unbind treeViewController");
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

        cayenneTreeHelper.createTreeItem(e.getDomain());
    }

    @Override
    public void currentDataMapChanged(DataMapDisplayEvent e) {
        if ((e.getSource() == this || !e.isDataMapChanged()) && !e.isRefired()) {
            return;
        }

        cayenneTreeHelper.createTreeItem(e.getDomain(), e.getDataMap());
    }

    @Override
    public void dataMapChanged(DataMapEvent e) {

    }

    @Override
    public void dataMapAdded(DataMapEvent e) {

    }

    @Override
    public void dataMapRemoved(DataMapEvent e) {

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
    public void processSelection(CayenneTreeItem treeItem) {
        if (treeItem == null) {
            return;
        }

        CayenneTreeItem currentNode = (CayenneTreeItem) treeItem;

        Object[] data = getUserObjects(currentNode);
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
        if (obj instanceof DataChannelDescriptor) {
            projectController.fireEvent(new DomainDisplayEvent(
                    this,
                    (DataChannelDescriptor) obj));
        } else if (obj instanceof DataMap) {
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
        } else if (obj instanceof DataNodeDescriptor) {
            if (data.length == 1) {
                projectController.fireEvent(new DataNodeDisplayEvent(
                        this,
                        (DataChannelDescriptor) projectController.getProject().getRootNode(),
                        (DataNodeDescriptor) obj));
            }
        } else if (obj instanceof DbEntity) {
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
        } else if (obj instanceof ObjEntity) {
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
        } else if (obj instanceof Embeddable) {
            EmbeddableDisplayEvent e = new EmbeddableDisplayEvent(
                    this,
                    (Embeddable) obj,
                    (DataMap) data[data.length - 2],
                    (DataChannelDescriptor) projectController.getProject().getRootNode());
            projectController.fireEvent(e);
        } else if (obj instanceof Procedure) {
            ProcedureDisplayEvent e = new ProcedureDisplayEvent(
                    this,
                    (Procedure) obj,
                    (DataMap) data[data.length - 2],
                    (DataChannelDescriptor) projectController.getProject().getRootNode());
            projectController.fireEvent(e);
        } else if (obj instanceof QueryDescriptor) {
            QueryDisplayEvent e = new QueryDisplayEvent(
                    this,
                    (QueryDescriptor) obj,
                    (DataMap) data[data.length - 2],
                    (DataChannelDescriptor) projectController.getProject().getRootNode());
            projectController.fireEvent(e);
        }
    }

    @Override
    public void currentDbEntityChanged(DbEntityDisplayEvent e) {
        e.setEntityChanged(true);

        if ((e.getSource() == this || !e.isEntityChanged()) && !e.isRefired()) {
            return;
        }

        cayenneTreeHelper.createTreeItem(e.getDomain(), e.getDataMap(), e.getEntity());
    }

    @Override
    public void currentObjEntityChanged(ObjEntityDisplayEvent e) {
        e.setEntityChanged(true);

        if ((e.getSource() == this || !e.isEntityChanged()) && !e.isRefired()) {
            return;
        }

        cayenneTreeHelper.createTreeItem(e.getDomain(), e.getDataMap(), e.getEntity());
    }
}
