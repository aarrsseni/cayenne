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
package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dbsync.reverse.dbload.DbLoader;
import org.apache.cayenne.modeler.pref.CoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.CoreDbAdapterFactory;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @since 4.1
 */
public class DefaultDbService implements DbService{

    @Inject
    protected CoreDbAdapterFactory dbAdapterFactory;

    @Inject
    protected CoreDataSourceFactory dataSourceFactory;

    private DataSource dataSource;
    private DbAdapter dbAdapter;
    private Connection connection;

    private DBConnectionInfo dbConnectionInfo;

    @Override
    public DbAdapter createDbAdapter(DBConnectionInfo info) throws Exception {
        this.dbAdapter = dbAdapterFactory.createAdapter(info);
        return dbAdapter;
    }

    @Override
    public DataSource createDataSource(DBConnectionInfo info) throws SQLException {
        this.dataSource = dataSourceFactory.createDataSource(info);
        return dataSource;
    }

    @Override
    public DbAdapter getDbAdapter() {
        return dbAdapter;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public List<String> getSchemas(Connection connection) throws Exception {
        return DbLoader.loadSchemas(connection);
    }

    @Override
    public List<String> getCatalogs(Connection connection) throws Exception {
        if(!getDbAdapter().supportsCatalogsOnReverseEngineering()) {
            return  Collections.emptyList();
        }

        return DbLoader.loadCatalogs(connection);
    }

    @Override
    public Connection createConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createDbConnectionInfo() {
        dbConnectionInfo = new DBConnectionInfo();
    }

    @Override
    public DBConnectionInfo getDbConnectionInfo() {
        return dbConnectionInfo;
    }

    @Override
    public void setDbConnectionInfo(DBConnectionInfo dbConnectionInfo) {
        this.dbConnectionInfo = dbConnectionInfo;
    }
}
