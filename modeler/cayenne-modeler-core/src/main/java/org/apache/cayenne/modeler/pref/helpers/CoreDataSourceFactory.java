package org.apache.cayenne.modeler.pref.helpers;

import org.apache.cayenne.modeler.pref.DBConnectionInfo;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface CoreDataSourceFactory {
    DataSource createDataSource(DBConnectionInfo dbConnectionInfo) throws SQLException;
}
