package org.apache.cayenne.modeler.services;

import org.apache.cayenne.pref.RenamedPreferences;

public class DefaultExitService implements ExitService{

    @Override
    public void exit() {
        RenamedPreferences.removeNewPreferences();

        // goodbye
        System.exit(0);
    }

}
