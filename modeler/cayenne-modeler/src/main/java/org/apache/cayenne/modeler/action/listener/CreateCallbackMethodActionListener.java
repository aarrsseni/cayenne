package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.CreateCallbackMethodEvent;
import org.apache.cayenne.modeler.event.CreateCallbackMethodListener;
import org.apache.cayenne.modeler.undo.CreateCallbackMethodUndoableEdit;

public class CreateCallbackMethodActionListener implements CreateCallbackMethodListener{
    @Override
    public void createCallbackMethod(CreateCallbackMethodEvent e) {
        Application.getInstance().getUndoManager().addEdit(new CreateCallbackMethodUndoableEdit(
                        e.getCallbackType(),
                        e.getMethodName()));
    }
}
