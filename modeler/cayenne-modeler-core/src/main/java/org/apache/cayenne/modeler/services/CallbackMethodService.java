package org.apache.cayenne.modeler.services;

import org.apache.cayenne.modeler.editor.CallbackType;

public interface CallbackMethodService {
    void createCallbackMethod();

    void createCallbackMethod(CallbackType callbackType, String methodName);

    void removeCallbackMethod(CallbackType callbackType, String method);
}
