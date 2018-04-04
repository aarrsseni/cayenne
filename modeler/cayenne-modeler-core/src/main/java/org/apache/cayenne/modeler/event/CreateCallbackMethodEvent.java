package org.apache.cayenne.modeler.event;

import org.apache.cayenne.modeler.editor.CallbackType;

import java.util.EventListener;
import java.util.EventObject;

public class CreateCallbackMethodEvent extends EventObject{

    private CallbackType callbackType;
    private String methodName;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CreateCallbackMethodEvent(Object source) {
        super(source);
    }

    public CreateCallbackMethodEvent(Object source, CallbackType callbackType, String methodName) {
        this(source);
        this.callbackType = callbackType;
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public CallbackType getCallbackType() {
        return callbackType;
    }

    public Class<? extends EventListener> getEventListener() {
        return CreateCallbackMethodListener.class;
    }
}
