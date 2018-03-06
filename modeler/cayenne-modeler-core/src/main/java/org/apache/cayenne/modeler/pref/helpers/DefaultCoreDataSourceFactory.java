package org.apache.cayenne.modeler.pref.helpers;

import com.google.inject.Inject;
import org.apache.cayenne.datasource.DriverDataSource;
import org.apache.cayenne.modeler.ClassLoadingService;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;
import org.apache.cayenne.util.Util;

import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.SQLException;

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
