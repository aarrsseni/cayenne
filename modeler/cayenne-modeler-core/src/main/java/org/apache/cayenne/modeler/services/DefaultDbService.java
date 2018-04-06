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
            return (List<String>) Collections.EMPTY_LIST;
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
