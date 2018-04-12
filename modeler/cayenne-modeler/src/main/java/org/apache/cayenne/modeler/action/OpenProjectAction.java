/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler.action;

import com.google.inject.Inject;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.ErrorDebugDialog;
import org.apache.cayenne.modeler.services.OpenProjectService;
import org.apache.cayenne.modeler.services.util.OpenProjectStatus;
import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.swing.control.FileMenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OpenProjectAction extends ProjectAction {

    @Inject
    public Application application;

    @Inject
    public ProjectController projectController;

    @Inject
    public OpenProjectService openProjectService;

    private static Logger logObj = LoggerFactory.getLogger(OpenProjectAction.class);

    @Inject
    private ProjectOpener fileChooser;

    public static String getActionName() {
        return "Open Project";
    }

    public OpenProjectAction() {
        super(getActionName());
        setAlwaysOn(true);
        resetClipboard();
    }

    @Override
    public String getIconName() {
        return "icon-open.png";
    }

    @Override
    public KeyStroke getAcceleratorKey() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }

    @Override
    public void performAction(ActionEvent e) {

        // Save and close (if needed) currently open project.
        if (projectController != null && !checkSaveOnClose()) {
            return;
        }

        File f = null;
        if (e.getSource() instanceof FileMenuItem) {
            FileMenuItem menu = (FileMenuItem) e.getSource();
            f = menu.getFile();
        } else if (e.getSource() instanceof File) {
            f = (File) e.getSource();
        }

        if (f == null) {
            try {
                // Get the project file name (always cayenne.xml)
                f = fileChooser.openProjectFile(Application.getFrame());
            } catch (Exception ex) {
                logObj.warn("Error loading project file.", ex);
            }
        }

        if (f != null) {
            // by now if the project is unsaved, this has been a user choice...
            if (projectController != null && !closeProject(false)) {
                return;
            }

            openProject(f);
        }

        application.getUndoManager().discardAllEdits();
    }

    /** Opens specified project file. File must already exist. */
    public void openProject(File file) {
        try {
            OpenProjectStatus status = openProjectService.canOpen(file);

            Resource rootSource = openProjectService.getRootSource(file);
            switch (status.getProjectStatus()) {
                case ERROR:
                    JOptionPane.showMessageDialog(
                            Application.getFrame(),
                            status.getMessage(),
                            status.getTitle(),
                            JOptionPane.ERROR_MESSAGE);
                    closeProject(false);
                    return;
                case UPGRADE_NEEDED:
                    if (processUpgrades()) {
                        rootSource = openProjectService.upgradeResource(rootSource);
                    } else {
                        closeProject(false);
                        return;
                    }
                    break;
            }
            openProjectService.openProjectResourse(rootSource);
        } catch (Exception ex) {
            logObj.warn("Error loading project file.", ex);
            ErrorDebugDialog.guiWarning(ex, "Error loading project");
        }
    }

    private boolean processUpgrades() {
        // need an upgrade
        int returnCode = JOptionPane.showConfirmDialog(
                Application.getFrame(),
                "Project needs an upgrade to a newer version. Upgrade?",
                "Upgrade Needed",
                JOptionPane.YES_NO_OPTION);
        return returnCode != JOptionPane.NO_OPTION;
    }

    private void resetClipboard() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[0];
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return false;
            }

            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                throw new UnsupportedFlavorException(flavor);
            }
        }, null);
    }
}
