package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.LinkDataMapEvent;
import org.apache.cayenne.modeler.event.LinkDataMapListener;
import org.apache.cayenne.modeler.undo.LinkDataMapUndoableEdit;

public class LinkDataMapActionListener implements LinkDataMapListener{
    @Override
    public void linkDataMap(LinkDataMapEvent e) {
        Application.getInstance().getUndoManager().addEdit(
                new LinkDataMapUndoableEdit(e.getDataMap(), e.getNode(), e.getUnlinkedNodes(), e.getProjectController()));
    }
}
