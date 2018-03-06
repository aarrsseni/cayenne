package org.apache.cayenne.modeler.pref.helpers;

import java.io.File;

public interface BaseFileChooser {

    File getSelectedFile();

    void setCurrentDirectory(File dir);
}
