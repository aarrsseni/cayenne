package org.apache.cayenne.modeler.services;

import org.apache.cayenne.dbsync.reverse.dbimport.*;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;

public interface ReverseEngineeringService {
    void saveConnectionInfo();

    DBConnectionInfo getConnectionInfoFromPreferences();

    void checkBuildConfig(DBConnectionInfo dbConnectionInfo);

    boolean datamapPreferencesExist();

    boolean reverseEngineeringIsEmpty(ReverseEngineering reverseEngineering);

    PatternParam getPatternParamToContainer(Class paramClass, Object selectedObject, String name);

    PatternParam getPatternParamToIncludeTable(Class paramClass, Object selectedObject, String name);

    void deleteChilds(Catalog catalog, Object selectedObject);

    void deleteChilds(Schema schema, Object selectedObject);

    void deleteChilds(IncludeTable includeTable, Object selectedObject);

    void deleteChilds(ReverseEngineering reverseEngineering, Object selectedObject);
}
