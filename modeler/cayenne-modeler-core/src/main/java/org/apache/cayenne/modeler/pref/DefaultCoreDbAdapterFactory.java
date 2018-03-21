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
import org.apache.cayenne.configuration.server.DbAdapterFactory;
import org.apache.cayenne.dba.AutoAdapter;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.di.AdhocObjectFactory;
import org.apache.cayenne.di.Injector;
import org.apache.cayenne.util.Util;

/**
 * @since 4.1
 */
public class DefaultCoreDbAdapterFactory implements CoreDbAdapterFactory {

    @Inject
    protected CoreDataSourceFactory dataSourceFactory;

    @Inject
    protected Injector cayenneInjector;

    /**
	 * Creates a DbAdapter based on configured values.
	 */
    @Override
    public DbAdapter createAdapter(DBConnectionInfo dbConnectionInfo) throws Exception {

        String adapterClassName = dbConnectionInfo.getDbAdapter();

        if (adapterClassName == null || AutoAdapter.class.getName().equals(adapterClassName)) {
            return cayenneInjector.getInstance(DbAdapterFactory.class)
                    .createAdapter(null, dataSourceFactory.createDataSource(dbConnectionInfo));
        }

        try {
            return cayenneInjector.getInstance(AdhocObjectFactory.class)
                    .newInstance(DbAdapter.class, adapterClassName);
        } catch (Throwable th) {
            th = Util.unwindException(th);
            throw new Exception("DbAdapter load error: " + th.getLocalizedMessage());
        }
    }
}
