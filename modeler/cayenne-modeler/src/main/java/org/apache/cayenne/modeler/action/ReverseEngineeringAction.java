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
import org.apache.cayenne.modeler.dialog.db.DataSourceWizard;
import org.apache.cayenne.modeler.dialog.db.DbActionOptionsDialog;
import org.apache.cayenne.modeler.dialog.db.load.DbLoadResultDialog;
import org.apache.cayenne.modeler.dialog.db.load.DbLoaderContext;
import org.apache.cayenne.modeler.dialog.db.load.LoadDataMapTask;
import org.apache.cayenne.modeler.services.DbService;
import org.apache.cayenne.modeler.editor.DbImportController;
import org.apache.cayenne.modeler.editor.dbimport.DbImportView;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;
import org.apache.cayenne.modeler.services.DbService;
import org.apache.cayenne.modeler.services.ReverseEngineeringService;
import org.apache.cayenne.modeler.util.CayenneAction;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Action that imports database structure into a DataMap.
 */
public class ReverseEngineeringAction extends CayenneAction {

    private static final String ACTION_NAME = "Reengineer Database Schema";
    private static final String ICON_NAME = "icon-dbi-runImport.png";
    private static final String DIALOG_TITLE = "Reengineer DB Schema: Connect to Database";

    public String getIconName() {
        return ICON_NAME;
    }

    @Inject
    private Application application;

    @Inject
    private DbService dbService;

    @Inject
    private DbLoaderContext context;

    @Inject
    private ReverseEngineeringService reverseEngineeringService;

    public ReverseEngineeringAction() {
        super(getActionName());
    }

    public static String getActionName() {
        return ACTION_NAME;
    }

    public void performAction() {
//        final DbLoaderContext context = new DbLoaderContext(application.getMetaData());
    /**
     * Connects to DB and delegates processing to DbLoaderController, starting it asynchronously.
     */
    @Override
    public void performAction(ActionEvent event) {
        DBConnectionInfo connectionInfo;
        if (!reverseEngineeringService.datamapPreferencesExist()) {
            DataSourceWizard connectWizard = new DataSourceWizard(getProjectController(), DIALOG_TITLE);
            if (!connectWizard.startupAction()) {
                return ;
            }
            connectionInfo = dbService.getDbConnectionInfo();
            reverseEngineeringService.saveConnectionInfo();
        } else {
            connectionInfo = reverseEngineeringService.getConnectionInfoFromPreferences();
        }

        try {
            dbService.setConnection(dbService.createConnection());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    Application.getFrame(),
                    ex.getMessage(),
                    "Error loading schemas dialog",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DbImportController dbImportController = Application.getInstance().getFrameController().getDbImportController();
        DbLoadResultDialog dbLoadResultDialog = dbImportController.createDialog();
        if(!dbLoadResultDialog.isVisible()) {
            dbImportController.showDialog();
        }

        if(!context.buildConfig(connectionInfo, view)) {
        if(!context.buildConfig(connectionInfo)) {
            try {
                dbService.getConnection().close();
            } catch (SQLException ignored) {}
            return;
        }

        runLoaderInThread(context, () -> {
            application.getUndoManager().discardAllEdits();
            try {
                context.getConnection().close();
            } catch (SQLException ignored) {}
        runLoaderInThread(context, () -> {
            application.getUndoManager().discardAllEdits();
            try {
                dbService.getConnection().close();
            } catch (SQLException ignored) {}
        });
    }

    /**
     * Connects to DB and delegates processing to DbLoaderController, starting it asynchronously.
     */
    @Override
    public void performAction(ActionEvent event) {
        performAction();
    }

    private DBConnectionInfo getConnectionInfoFromPreferences() {
        DBConnectionInfo connectionInfo = new DBConnectionInfo();
        DataMapDefaults dataMapDefaults = getProjectController().
                getDataMapPreferences(getProjectController().getCurrentState().getDataMap());
        connectionInfo.setDbAdapter(dataMapDefaults.getCurrentPreference().get(DB_ADAPTER_PROPERTY, null));
        connectionInfo.setUrl(dataMapDefaults.getCurrentPreference().get(URL_PROPERTY, null));
        connectionInfo.setUserName(dataMapDefaults.getCurrentPreference().get(USER_NAME_PROPERTY, null));
        connectionInfo.setPassword(dataMapDefaults.getCurrentPreference().get(PASSWORD_PROPERTY, null));
        connectionInfo.setJdbcDriver(dataMapDefaults.getCurrentPreference().get(JDBC_DRIVER_PROPERTY, null));
        return connectionInfo;
    }

    private void saveConnectionInfo() {
        DataMapDefaults dataMapDefaults = getProjectController().
                getDataMapPreferences(getProjectController().getCurrentState().getDataMap());
        dataMapDefaults.getCurrentPreference().put(DB_ADAPTER_PROPERTY, dbService.getDbConnectionInfo().getDbAdapter());
        dataMapDefaults.getCurrentPreference().put(URL_PROPERTY, dbService.getDbConnectionInfo().getUrl());
        dataMapDefaults.getCurrentPreference().put(USER_NAME_PROPERTY, dbService.getDbConnectionInfo().getUserName());
        dataMapDefaults.getCurrentPreference().put(PASSWORD_PROPERTY, dbService.getDbConnectionInfo().getPassword());
        dataMapDefaults.getCurrentPreference().put(JDBC_DRIVER_PROPERTY, dbService.getDbConnectionInfo().getJdbcDriver());
    }

    private boolean datamapPreferencesExist() {
        DataMapDefaults dataMapDefaults = projectController.
                getDataMapPreferences(projectController.getCurrentState().getDataMap());
        return dataMapDefaults.getCurrentPreference().get(DB_ADAPTER_PROPERTY, null) != null;
    }

    private void runLoaderInThread(final DbLoaderContext context, final Runnable callback) {
        Thread th = new Thread(() -> {
            LoadDataMapTask task = new LoadDataMapTask(Application.getFrame(), "Reengineering DB", context);
            task.startAndWait();
            SwingUtilities.invokeLater(callback);
        });
        th.start();
    }
}