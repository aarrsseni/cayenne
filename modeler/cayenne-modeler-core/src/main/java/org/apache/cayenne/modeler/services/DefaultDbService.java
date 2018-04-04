package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.modeler.pref.CoreDataSourceFactory;
import org.apache.cayenne.modeler.pref.CoreDbAdapterFactory;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;

import javax.sql.DataSource;

public class DefaultDbService implements DbService{

    @Inject
    protected CoreDbAdapterFactory dbAdapterFactory;

    @Inject
    protected CoreDataSourceFactory dataSourceFactory;

    @Override
    public DbAdapter createDbAdapter(DBConnectionInfo info) throws Exception {
        return dbAdapterFactory.createAdapter(info);
    }

    @Override
    public DataSource createDataSource(DBConnectionInfo info) throws Exception {
        return dataSourceFactory.createDataSource(info);
    }
}
