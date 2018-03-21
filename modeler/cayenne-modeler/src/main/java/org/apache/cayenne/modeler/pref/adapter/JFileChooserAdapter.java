package org.apache.cayenne.modeler.pref.adapter;

import org.apache.cayenne.modeler.pref.helpers.BaseFileChooser;

import javax.swing.*;
import java.io.File;

public class JFileChooserAdapter implements BaseFileChooser{

    protected JFileChooser jFileChooser;

    public JFileChooserAdapter(JFileChooser jFileChooser){
        this.jFileChooser = jFileChooser;
    }

    public JFileChooser getjFileChooser() {
        return jFileChooser;
    }

    @Override
    public File getSelectedFile() {
        return jFileChooser.getSelectedFile();
    }

    @Override
    public void setCurrentDirectory(File dir) {
        jFileChooser.setCurrentDirectory(dir);
    }
}
