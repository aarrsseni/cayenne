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
import org.apache.cayenne.modeler.dialog.db.DataSourceWizard;
import org.apache.cayenne.modeler.dialog.db.DbActionOptionsDialog;
import org.apache.cayenne.modeler.pref.DataMapDefaults;
import org.apache.cayenne.modeler.services.DbService;

import java.awt.event.ActionEvent;
import java.util.Collection;

import static org.apache.cayenne.modeler.pref.DBConnectionInfo.*;

/**
 * @since 4.1
 */
public class GetDbConnectionAction extends DBWizardAction<DbActionOptionsDialog> {

    public static final String DIALOG_TITLE = "Configure Connection to Database";
    private static final String ACTION_NAME = "Configure Connection";
    private static final String ICON_NAME = "icon-dbi-config.png";

    @Inject
    private DbService dbService;

    public GetDbConnectionAction() {
        super(ACTION_NAME);
        setAlwaysOn(true);
    }

    public String getIconName() {
        return ICON_NAME;
    }

    @Override
    protected DbActionOptionsDialog createDialog(final Collection<String> catalogs, final Collection<String> schemas,
                                                 final String currentCatalog, final String currentSchema, final int command) {
        // NOOP
        return null;
    }

    @Override
    public void performAction(final ActionEvent e) {
        final DataSourceWizard connectWizard = dataSourceWizardDialog(DIALOG_TITLE);
        if (connectWizard == null) {
            return;
        }

        final DataMapDefaults dataMapDefaults = getProjectController().
                getDataMapPreferences(getProjectController().getCurrentState().getDataMap());

        if (dbService.getDbConnectionInfo().getDbAdapter() != null) {
            dataMapDefaults.getCurrentPreference().put(DB_ADAPTER_PROPERTY, dbService.getDbConnectionInfo().getDbAdapter());
        }
        dataMapDefaults.getCurrentPreference().put(URL_PROPERTY, dbService.getDbConnectionInfo().getUrl());
        dataMapDefaults.getCurrentPreference().put(USER_NAME_PROPERTY, dbService.getDbConnectionInfo().getUserName());
        dataMapDefaults.getCurrentPreference().put(PASSWORD_PROPERTY, dbService.getDbConnectionInfo().getPassword());
        dataMapDefaults.getCurrentPreference().put(JDBC_DRIVER_PROPERTY, dbService.getDbConnectionInfo().getJdbcDriver());
    }
}
