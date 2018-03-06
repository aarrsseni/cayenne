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

package org.apache.cayenne.modeler.pref;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import javax.sql.DataSource;

import org.apache.cayenne.conn.DataSourceInfo;
import org.apache.cayenne.datasource.DriverDataSource;

import org.apache.cayenne.modeler.ClassLoadingService;
import org.apache.cayenne.pref.CayennePreference;
import org.apache.cayenne.util.Util;

public class DBConnectionInfo extends CayennePreference {

    public static final String DB_ADAPTER_PROPERTY = "dbAdapter";
    public static final String JDBC_DRIVER_PROPERTY = "jdbcDriver";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String URL_PROPERTY = "url";
    public static final String USER_NAME_PROPERTY = "userName";
    private static final String DB_CONNECTION_INFO = "dbConnectionInfo";

    public static final String ID_PK_COLUMN = "id";

    private String nodeName;

    private String dbAdapter;
    private String jdbcDriver;
    private String password;
    private String url;
    private String userName;

    private Preferences dbConnectionInfoPreferences;

    public DBConnectionInfo() {
        dbConnectionInfoPreferences = getCayennePreference().node(DB_CONNECTION_INFO);
        setCurrentPreference(dbConnectionInfoPreferences);
    };

    public DBConnectionInfo(String nameNode, boolean initFromPreferences) {
        this();
        setNodeName(nameNode);
        if (initFromPreferences) {
            initObjectPreference();
        }
    };

    @Override
    public Preferences getCurrentPreference() {
        if (getNodeName() == null) {
            return super.getCurrentPreference();
        }
        return dbConnectionInfoPreferences.node(getNodeName());
    }

    @Override
    public void setObject(CayennePreference object) {
        if (object instanceof DBConnectionInfo) {
            setUrl(((DBConnectionInfo) object).getUrl());
            setUserName(((DBConnectionInfo) object).getUserName());
            setPassword(((DBConnectionInfo) object).getPassword());
            setJdbcDriver(((DBConnectionInfo) object).getJdbcDriver());
            setDbAdapter(((DBConnectionInfo) object).getDbAdapter());
        }
    }

    @Override
    public void saveObjectPreference() {
        if (getCurrentPreference() != null) {
            if (getDbAdapter() != null) {
                getCurrentPreference().put(DB_ADAPTER_PROPERTY, getDbAdapter());
            }
            if (getUrl() != null) {
                getCurrentPreference().put(URL_PROPERTY, getUrl());
            }
            if (getUserName() != null) {
                getCurrentPreference().put(USER_NAME_PROPERTY, getUserName());
            }
            if (getPassword() != null) {
                getCurrentPreference().put(PASSWORD_PROPERTY, getPassword());
            }
            if (getJdbcDriver() != null) {
                getCurrentPreference().put(JDBC_DRIVER_PROPERTY, getJdbcDriver());
            }
        }
    }

    public void initObjectPreference() {
        if (getCurrentPreference() != null) {
            setDbAdapter(getCurrentPreference().get(DB_ADAPTER_PROPERTY, null));
            setUrl(getCurrentPreference().get(URL_PROPERTY, null));
            setUserName(getCurrentPreference().get(USER_NAME_PROPERTY, null));
            setPassword(getCurrentPreference().get(PASSWORD_PROPERTY, null));
            setJdbcDriver(getCurrentPreference().get(JDBC_DRIVER_PROPERTY, null));
            setNodeName(getCurrentPreference().name());
        }
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getDbAdapter() {
        return dbAdapter;
    }

    public void setDbAdapter(String dbAdapter) {
        this.dbAdapter = dbAdapter;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Preferences getDbConnectionInfoPreferences() {
        return dbConnectionInfoPreferences;
    }

    public void setDbConnectionInfoPreferences(Preferences dbConnectionInfoPreferences) {
        this.dbConnectionInfoPreferences = dbConnectionInfoPreferences;
    }

    /**
     * Updates another DBConnectionInfo with this object's values.
     */
    public boolean copyTo(DBConnectionInfo dataSourceInfo) {
        boolean updated = false;

        if (!Util.nullSafeEquals(dataSourceInfo.getUrl(), getUrl())) {
            dataSourceInfo.setUrl(getUrl());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getUserName(), getUserName())) {
            dataSourceInfo.setUserName(getUserName());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getPassword(), getPassword())) {
            dataSourceInfo.setPassword(getPassword());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getJdbcDriver(), getJdbcDriver())) {
            dataSourceInfo.setJdbcDriver(getJdbcDriver());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getDbAdapter(), getDbAdapter())) {
            dataSourceInfo.setDbAdapter(getDbAdapter());
            updated = true;
        }

        return updated;
    }

    /**
     * Updates DataSourceInfo with this object's values.
     * <p>
     * <i>Currently doesn't set the adapter property. Need to change the UI to
     * handle adapter via DataSourceInfo first, and then it should be safe to do
     * an adapter update here. </i>
     * </p>
     */
    public boolean copyTo(DataSourceInfo dataSourceInfo) {
        boolean updated = false;

        if (!Util.nullSafeEquals(dataSourceInfo.getDataSourceUrl(), getUrl())) {
            dataSourceInfo.setDataSourceUrl(getUrl());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getUserName(), getUserName())) {
            dataSourceInfo.setUserName(getUserName());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getPassword(), getPassword())) {
            dataSourceInfo.setPassword(getPassword());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getJdbcDriver(), getJdbcDriver())) {
            dataSourceInfo.setJdbcDriver(getJdbcDriver());
            updated = true;
        }

        return updated;
    }

    public boolean copyFrom(DataSourceInfo dataSourceInfo) {
        boolean updated = false;

        if (!Util.nullSafeEquals(dataSourceInfo.getDataSourceUrl(), getUrl())) {
            setUrl(dataSourceInfo.getDataSourceUrl());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getUserName(), getUserName())) {
            setUserName(dataSourceInfo.getUserName());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getPassword(), getPassword())) {
            setPassword(dataSourceInfo.getPassword());
            updated = true;
        }

        if (!Util.nullSafeEquals(dataSourceInfo.getJdbcDriver(), getJdbcDriver())) {
            setJdbcDriver(dataSourceInfo.getJdbcDriver());
            updated = true;
        }

        return updated;
    }
}