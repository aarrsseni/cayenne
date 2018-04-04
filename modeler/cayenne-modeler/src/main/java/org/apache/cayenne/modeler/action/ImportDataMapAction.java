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
import org.apache.cayenne.modeler.pref.BaseFileChooser;
import org.apache.cayenne.modeler.pref.FSPath;
import org.apache.cayenne.modeler.pref.adapter.JFileChooserAdapter;
import org.apache.cayenne.modeler.services.DataMapService;
import org.apache.cayenne.modeler.util.CayenneAction;
import org.apache.cayenne.modeler.util.FileFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Modeler action that imports a DataMap into a project from an arbitrary
 * location.
 *
 * @since 1.1
 */
public class ImportDataMapAction extends CayenneAction {

    private static Logger logObj = LoggerFactory.getLogger(ImportDataMapAction.class);

    @Inject
    public Application application;

    @Inject
    public DataMapService dataMapService;

    public ImportDataMapAction() {
        super(getActionName());
    }

    public static String getActionName() {
        return "Import DataMap";
    }

    public void performAction(ActionEvent e) {
        importDataMap();
    }

    protected void importDataMap() {
        File dataMapFile = selectDataMap(Application.getFrame());
        if (dataMapFile == null) {
            return;
        }
        try {
            dataMapService.importDataMap(dataMapFile);
        } catch (Exception ex) {
            logObj.info("Error importing DataMap.", ex);
            JOptionPane.showMessageDialog(Application.getFrame(), "Error reading DataMap: " + ex.getMessage(),
                    "Can't Open DataMap", JOptionPane.OK_OPTION);
        }
    }

    protected File selectDataMap(Frame f) {

        // find start directory in preferences
        FSPath lastDir = application.getFrameController().getLastDirectory();

        // configure dialog
        JFileChooser chooser = new JFileChooser();
        BaseFileChooser baseFileChooser = new JFileChooserAdapter(chooser);

        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        lastDir.updateChooser(baseFileChooser);

        chooser.addChoosableFileFilter(FileFilters.getDataMapFilter());

        int status = chooser.showDialog(f, "Select DataMap");
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();


            // save to preferences...
            lastDir.updateFromChooser(baseFileChooser);

            return file;
        }

        return null;
    }
}
