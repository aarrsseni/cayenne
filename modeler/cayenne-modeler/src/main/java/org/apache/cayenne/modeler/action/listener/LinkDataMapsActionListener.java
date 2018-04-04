package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.LinkDataMapsEvent;
import org.apache.cayenne.modeler.event.LinkDataMapsListener;
import org.apache.cayenne.modeler.undo.LinkDataMapsUndoableEdit;

public class LinkDataMapsActionListener implements LinkDataMapsListener{
    @Override
    public void linkDataMaps(LinkDataMapsEvent e) {
        Application.getInstance().getUndoManager().addEdit(
                new LinkDataMapsUndoableEdit(e.getDataNodeDescriptor(), e.getLinkedDataMaps(), Application.getInstance().getProjectController()));
    }
}
