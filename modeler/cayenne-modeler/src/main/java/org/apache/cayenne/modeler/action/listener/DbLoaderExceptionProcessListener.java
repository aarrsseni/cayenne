package org.apache.cayenne.modeler.action.listener;

import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.event.DbLoaderExceptionEvent;
import org.apache.cayenne.modeler.event.listener.DbLoaderExceptionListener;

import javax.swing.*;

public class DbLoaderExceptionProcessListener implements DbLoaderExceptionListener{
    @Override
    public void processException(DbLoaderExceptionEvent e) {
        JOptionPane.showMessageDialog(Application.getFrame(), e.getTh().getMessage(), e.getMsg(),
                JOptionPane.ERROR_MESSAGE);
    }
}
