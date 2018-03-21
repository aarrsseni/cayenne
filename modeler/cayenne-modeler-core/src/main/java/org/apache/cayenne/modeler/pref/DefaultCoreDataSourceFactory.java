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

import com.google.inject.Inject;
import org.apache.cayenne.datasource.DriverDataSource;
import org.apache.cayenne.modeler.ClassLoadingService;
import org.apache.cayenne.util.Util;

import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.SQLException;

/**
 * @since 4.1
 */
public class DefaultCoreDataSourceFactory implements CoreDataSourceFactory{

    @Inject
    protected ClassLoadingService classLoadingService;

    /**
     * Returns a DataSource that uses connection information from this object.
     * Returned DataSource is not pooling its connections. It can be wrapped in
     * PoolManager if pooling is needed.
     */
    @Override
    public DataSource createDataSource(DBConnectionInfo dbConnectionInfo) throws SQLException {

        // validate...
        if (dbConnectionInfo.getJdbcDriver() == null) {
            throw new SQLException("No JDBC driver set.");
        }

        if (dbConnectionInfo.getUrl() == null) {
            throw new SQLException("No DB URL set.");
        }

        // load driver...
        Driver driver;

        try {
            driver = classLoadingService.loadClass(Driver.class, dbConnectionInfo.getJdbcDriver()).newInstance();
        } catch (Throwable th) {
            th = Util.unwindException(th);
            throw new SQLException("Driver load error: " + th.getLocalizedMessage());
        }

        return new DriverDataSource(driver, dbConnectionInfo.getUrl(), dbConnectionInfo.getUserName(), dbConnectionInfo.getPassword());
    }
}
