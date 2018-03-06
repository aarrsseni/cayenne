package org.apache.cayenne.modeler.pref.helpers;

import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;

public interface CoreDbAdapterFactory {
    DbAdapter createAdapter(DBConnectionInfo dbConnectionInfo) throws Exception;
}
