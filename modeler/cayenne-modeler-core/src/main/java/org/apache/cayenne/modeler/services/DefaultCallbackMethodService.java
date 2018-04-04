package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.dbsync.naming.NameBuilder;
import org.apache.cayenne.map.CallbackMap;
import org.apache.cayenne.map.LifecycleEvent;
import org.apache.cayenne.map.event.MapEvent;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.editor.CallbackType;
import org.apache.cayenne.modeler.event.CallbackMethodEvent;
import org.apache.cayenne.modeler.event.CreateCallbackMethodEvent;
import org.apache.cayenne.util.Util;

public class DefaultCallbackMethodService implements CallbackMethodService{

    @Inject
    public ProjectController projectController;

    @Override
    public void createCallbackMethod() {
        CallbackType callbackType = projectController.getCurrentState().getCallbackType();

        String methodName = NameBuilder
                .builderForCallbackMethod(projectController.getCurrentState().getObjEntity())
                .baseName(toMethodName(callbackType.getType()))
                .name();

        createCallbackMethod(callbackType, methodName);

        projectController.fireEvent(new CreateCallbackMethodEvent(this, callbackType, methodName));
    }

    public void createCallbackMethod(
            CallbackType callbackType,
            String methodName) {
        getCallbackMap().getCallbackDescriptor(callbackType.getType()).addCallbackMethod(methodName);

        CallbackMethodEvent ce = new CallbackMethodEvent(
                this,
                null,
                methodName,
                MapEvent.ADD);

        projectController.fireEvent(ce);
    }

    public void removeCallbackMethod(CallbackType callbackType, String method) {
        getCallbackMap().getCallbackDescriptor(callbackType.getType()).removeCallbackMethod(method);

        CallbackMethodEvent e = new CallbackMethodEvent(
                this,
                null,
                method,
                MapEvent.REMOVE);

        projectController.fireEvent(e);
    }

    private String toMethodName(LifecycleEvent event) {
        return "on" + Util.underscoredToJava(event.name(), true);
    }

    /**
     * @return CallbackMap instance where to create a method
     */
    public CallbackMap getCallbackMap() {
        return projectController.getCurrentState().getObjEntity().getCallbackMap();
    }
}
