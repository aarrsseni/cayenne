package org.apache.cayenne.modeler.services;

import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DbService {
    DbAdapter createDbAdapter(DBConnectionInfo info) throws Exception;

    DataSource createDataSource(DBConnectionInfo info) throws SQLException;

    Connection createConnection() throws SQLException;

    DbAdapter getDbAdapter();

    DataSource getDataSource();

    List<String> getSchemas(Connection connection) throws Exception;

    List<String> getCatalogs(Connection connection) throws Exception;

    void setConnection(Connection connection);

    Connection getConnection();

    void createDbConnectionInfo();

    DBConnectionInfo getDbConnectionInfo();

    void setDbConnectionInfo(DBConnectionInfo dbConnectionInfo);
}
