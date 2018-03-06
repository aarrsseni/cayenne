package org.apache.cayenne.modeler.pref.helpers;

import com.google.inject.Inject;
import org.apache.cayenne.configuration.server.DbAdapterFactory;
import org.apache.cayenne.dba.AutoAdapter;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.di.AdhocObjectFactory;
import org.apache.cayenne.di.Injector;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;
import org.apache.cayenne.util.Util;

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
