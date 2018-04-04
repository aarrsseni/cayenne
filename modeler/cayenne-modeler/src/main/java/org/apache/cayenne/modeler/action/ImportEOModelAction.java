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
import org.apache.cayenne.modeler.dialog.ErrorDebugDialog;
import org.apache.cayenne.modeler.pref.BaseFileChooser;
import org.apache.cayenne.modeler.pref.FSPath;
import org.apache.cayenne.modeler.pref.adapter.JFileChooserAdapter;
import org.apache.cayenne.modeler.services.EOModelService;
import org.apache.cayenne.modeler.util.CayenneAction;
import org.apache.cayenne.modeler.util.Comparators;
import org.apache.cayenne.modeler.util.FileFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Action handler for WebObjects EOModel import function.
 * 
 */
public class ImportEOModelAction extends CayenneAction {

    @Inject
    public Application application;

    @Inject
    public EOModelService eoModelService;

    private static Logger logObj = LoggerFactory.getLogger(ImportEOModelAction.class);

    public static String getActionName() {
        return "Import EOModel";
    }

    protected JFileChooser eoModelChooser;

    public ImportEOModelAction() {
        super(getActionName());
    }

    public String getIconName() {
        return "icon-eomodel.png";
    }

    public void performAction(ActionEvent event) {
        importEOModel();
    }

    /**
     * Allows user to select an EOModel, then imports it as a DataMap.
     */
    protected void importEOModel() {
        JFileChooser fileChooser = getEOModelChooser();
        BaseFileChooser baseFileChooser = new JFileChooserAdapter(fileChooser);

        int status = fileChooser.showOpenDialog(Application.getFrame());

        if (status == JFileChooser.APPROVE_OPTION) {

            // save preferences
            FSPath lastDir = application
                    .getFrameController()
                    .getLastEOModelDirectory();
            lastDir.updateFromChooser(baseFileChooser);

            File file = fileChooser.getSelectedFile();
            if (file.isFile()) {
                file = file.getParentFile();
            }

            try {
                eoModelService.importEOModel(file);
            }
            catch (Exception ex) {
                logObj.info("EOModel Loading Exception", ex);
                ErrorDebugDialog.guiException(ex);
            }

        }
    }

    /**
     * Returns EOModel chooser.
     */
    public JFileChooser getEOModelChooser() {

        if (eoModelChooser == null) {
            eoModelChooser = new EOModelChooser("Select EOModel");
        }

        FSPath lastDir = application.getFrameController().getLastEOModelDirectory();
        BaseFileChooser baseFileChooser = new JFileChooserAdapter(eoModelChooser);

        lastDir.updateChooser(baseFileChooser);

        return eoModelChooser;
    }

    /**
     * Custom file chooser that will pop up again if a bad directory is selected.
     */
    class EOModelChooser extends JFileChooser {

        protected FileFilter selectFilter;
        protected JDialog cachedDialog;

        public EOModelChooser(String title) {
            super.setFileFilter(FileFilters.getEOModelFilter());
            super.setDialogTitle(title);
            super.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            this.selectFilter = FileFilters.getEOModelSelectFilter();
        }

        public int showOpenDialog(Component parent) {
            int status = super.showOpenDialog(parent);
            if (status != JFileChooser.APPROVE_OPTION) {
                cachedDialog = null;
                return status;
            }

            // make sure invalid directory is not selected
            File file = this.getSelectedFile();
            if (selectFilter.accept(file)) {
                cachedDialog = null;
                return JFileChooser.APPROVE_OPTION;
            }
            else {
                if (file.isDirectory()) {
                    this.setCurrentDirectory(file);
                }

                return this.showOpenDialog(parent);
            }
        }

        protected JDialog createDialog(Component parent) throws HeadlessException {

            if (cachedDialog == null) {
                cachedDialog = super.createDialog(parent);
            }
            return cachedDialog;
        }
    }
}
