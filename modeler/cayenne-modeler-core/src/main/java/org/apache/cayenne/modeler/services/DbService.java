package org.apache.cayenne.modeler.services;

import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;

import javax.sql.DataSource;

public interface DbService {
    DbAdapter createDbAdapter(DBConnectionInfo info) throws Exception;

    DataSource createDataSource(DBConnectionInfo info) throws Exception;
}
