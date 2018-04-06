package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.dialog.db.gen.DBGeneratorOptions;
import org.apache.cayenne.modeler.event.GenerateDbEvent;
import org.apache.cayenne.modeler.event.listener.GenerateDbListener;

public class GenerateDbActionListener implements GenerateDbListener{
    @Override
    public void generateDb(GenerateDbEvent e) {
        new DBGeneratorOptions(e.getProjectController(), "Generate DB Schema: Options", e.getDataMaps())
                .startupAction();
    }
}
