package org.apache.cayenne.modeler.event.listener;

import org.apache.cayenne.modeler.event.GenerateCodeEvent;

import java.util.EventListener;

public interface GenerateCodeListener extends EventListener{
    void generateCode(GenerateCodeEvent e);
}
