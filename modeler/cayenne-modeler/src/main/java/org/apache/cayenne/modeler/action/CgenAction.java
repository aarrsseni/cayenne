package org.apache.cayenne.modeler.action;

import org.apache.cayenne.gen.CgenConfiguration;
import org.apache.cayenne.gen.ClassGenerationAction;
import org.apache.cayenne.gen.ClientClassGenerationAction;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.dialog.ErrorDebugDialog;
import org.apache.cayenne.modeler.util.CayenneAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CgenAction extends CayenneAction {

    /**
     * Logger to print stack traces
     */
    private static Logger logObj = LoggerFactory.getLogger(ErrorDebugDialog.class);

    public CgenAction() {
        super(getActionName());
    }

    public static String getActionName() {
        return "Cgen action";
    }

    @Override
    public void performAction(ActionEvent e) {}

    public void performAction(CgenConfiguration cgenConfiguration) {
        ClassGenerationAction generator = cgenConfiguration.isClient() ?
                new ClientClassGenerationAction(cgenConfiguration) :
                new ClassGenerationAction(cgenConfiguration);

        try {
            generator.prepareArtifacts();
            generator.execute();
            JOptionPane.showMessageDialog(
                    Application.getFrame(),
                    "Class generation finished");
        } catch (Exception e) {
            logObj.error("Error generating classes", e);
            JOptionPane.showMessageDialog(
                    Application.getFrame(),
                    "Error generating classes - " + e.getMessage());
        }
    }
}
