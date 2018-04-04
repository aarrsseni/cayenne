package org.apache.cayenne.modeler.event;

import java.util.EventListener;

public interface GenerateCodeListener extends EventListener{
    void generateCode(GenerateCodeEvent e);
}
