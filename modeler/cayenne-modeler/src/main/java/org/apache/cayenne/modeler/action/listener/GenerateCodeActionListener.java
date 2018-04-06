package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.dialog.codegen.CodeGeneratorController;
import org.apache.cayenne.modeler.event.GenerateCodeEvent;
import org.apache.cayenne.modeler.event.listener.GenerateCodeListener;

public class GenerateCodeActionListener implements GenerateCodeListener{
    @Override
    public void generateCode(GenerateCodeEvent e) {
        new CodeGeneratorController(Application.getInstance().getFrameController(), e.getDataMaps()).startup();
    }
}
