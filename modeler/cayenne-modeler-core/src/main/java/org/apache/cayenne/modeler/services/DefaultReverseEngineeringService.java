package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.dbsync.reverse.dbimport.*;
import org.apache.cayenne.modeler.ProjectController;
import org.apache.cayenne.modeler.dialog.db.load.DbLoaderContext;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;
import org.apache.cayenne.modeler.pref.DataMapDefaults;

import java.sql.SQLException;

import static org.apache.cayenne.modeler.pref.DBConnectionInfo.*;
import static org.apache.cayenne.modeler.pref.DBConnectionInfo.JDBC_DRIVER_PROPERTY;

public class DefaultReverseEngineeringService implements ReverseEngineeringService {

    @Inject
    private ProjectController projectController;

    @Inject
    private DbService dbService;

    @Inject
    DbLoaderContext context;

    @Override
    public void saveConnectionInfo() {
        DataMapDefaults dataMapDefaults = projectController.
                getDataMapPreferences(projectController.getCurrentState().getDataMap());
        dataMapDefaults.getCurrentPreference().put(DB_ADAPTER_PROPERTY, dbService.getDbConnectionInfo().getDbAdapter());
        dataMapDefaults.getCurrentPreference().put(URL_PROPERTY, dbService.getDbConnectionInfo().getUrl());
        dataMapDefaults.getCurrentPreference().put(USER_NAME_PROPERTY, dbService.getDbConnectionInfo().getUserName());
        dataMapDefaults.getCurrentPreference().put(PASSWORD_PROPERTY, dbService.getDbConnectionInfo().getPassword());
        dataMapDefaults.getCurrentPreference().put(JDBC_DRIVER_PROPERTY, dbService.getDbConnectionInfo().getJdbcDriver());

    }

    @Override
    public DBConnectionInfo getConnectionInfoFromPreferences() {
        DBConnectionInfo connectionInfo = new DBConnectionInfo();
        DataMapDefaults dataMapDefaults = projectController.
                getDataMapPreferences(projectController.getCurrentState().getDataMap());
        connectionInfo.setDbAdapter(dataMapDefaults.getCurrentPreference().get(DB_ADAPTER_PROPERTY, null));
        connectionInfo.setUrl(dataMapDefaults.getCurrentPreference().get(URL_PROPERTY, null));
        connectionInfo.setUserName(dataMapDefaults.getCurrentPreference().get(USER_NAME_PROPERTY, null));
        connectionInfo.setPassword(dataMapDefaults.getCurrentPreference().get(PASSWORD_PROPERTY, null));
        connectionInfo.setJdbcDriver(dataMapDefaults.getCurrentPreference().get(JDBC_DRIVER_PROPERTY, null));

        if(dbService.getConnection() == null) {
            try {
                dbService.createDataSource(connectionInfo);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connectionInfo;
    }

    @Override
    public void checkBuildConfig(DBConnectionInfo dbConnectionInfo) {
        if(!context.buildConfig(dbConnectionInfo)) {
            try {
                dbService.getConnection().close();
            } catch (SQLException ignored) {}
            return;
        }
    }

    @Override
    public boolean datamapPreferencesExist() {
        DataMapDefaults dataMapDefaults = projectController.
                getDataMapPreferences(projectController.getCurrentState().getDataMap());
        return dataMapDefaults.getCurrentPreference().get(DB_ADAPTER_PROPERTY, null) != null;
    }

    @Override
    public boolean reverseEngineeringIsEmpty(ReverseEngineering reverseEngineering) {
        return ((reverseEngineering.getCatalogs().size() == 0) && (reverseEngineering.getSchemas().size() == 0)
                && (reverseEngineering.getIncludeTables().size() == 0) && (reverseEngineering.getExcludeTables().size() == 0)
                && (reverseEngineering.getIncludeColumns().size() == 0) && (reverseEngineering.getExcludeColumns().size() == 0)
                && (reverseEngineering.getIncludeProcedures().size() == 0) && (reverseEngineering.getExcludeProcedures().size() == 0));
    }

    @Override
    public PatternParam getPatternParamToContainer(Class paramClass, Object selectedObject, String name) {
        FilterContainer container = (FilterContainer) selectedObject;
        PatternParam element = null;
        if (paramClass == ExcludeTable.class) {
            element = new ExcludeTable(name);
            container.addExcludeTable((ExcludeTable) element);
        } else if (paramClass == IncludeColumn.class) {
            element = new IncludeColumn(name);
            container.addIncludeColumn((IncludeColumn) element);
        } else if (paramClass == ExcludeColumn.class) {
            element = new ExcludeColumn(name);
            container.addExcludeColumn((ExcludeColumn) element);
        } else if (paramClass == IncludeProcedure.class) {
            element = new IncludeProcedure(name);
            container.addIncludeProcedure((IncludeProcedure) element);
        } else if (paramClass == ExcludeProcedure.class) {
            element = new ExcludeProcedure(name);
            container.addExcludeProcedure((ExcludeProcedure) element);
        }
        return element;
    }

    @Override
    public PatternParam getPatternParamToIncludeTable(Class paramClass, Object selectedObject, String name) {
        IncludeTable includeTable = (IncludeTable) selectedObject;
        PatternParam element = null;
        if (paramClass == IncludeColumn.class) {
            element = new IncludeColumn(name);
            includeTable.addIncludeColumn((IncludeColumn) element);

        } else if (paramClass == ExcludeColumn.class) {
            element = new ExcludeColumn(name);
            includeTable.addExcludeColumn((ExcludeColumn) element);
        }
        return element;
    }

    @Override
    public void deleteChilds(Catalog catalog, Object selectedObject) {
        if (selectedObject instanceof Schema) {
            catalog.getSchemas().remove(selectedObject);
        } else if (selectedObject instanceof IncludeTable) {
            catalog.getIncludeTables().remove(selectedObject);
        } else if (selectedObject instanceof PatternParam) {
            removePatternParams(catalog, selectedObject);
        }
    }

    @Override
    public void deleteChilds(Schema schema, Object selectedObject) {
        if (selectedObject instanceof IncludeTable) {
            schema.getIncludeTables().remove(selectedObject);
        } else if (selectedObject instanceof PatternParam) {
            removePatternParams(schema, selectedObject);
        }
    }

    @Override
    public void deleteChilds(IncludeTable includeTable, Object selectedObject) {
        includeTable.getIncludeColumns().remove(selectedObject);
        includeTable.getExcludeColumns().remove(selectedObject);
    }

    @Override
    public void deleteChilds(ReverseEngineering reverseEngineering, Object selectedObject) {
        if (selectedObject instanceof Catalog) {
            reverseEngineering.getCatalogs().remove(selectedObject);
        } else if (selectedObject instanceof Schema) {
            reverseEngineering.getSchemas().remove(selectedObject);
        } else if (selectedObject instanceof IncludeTable) {
            reverseEngineering.getIncludeTables().remove(selectedObject);
        } else if (selectedObject instanceof ExcludeTable) {
            reverseEngineering.getExcludeTables().remove(selectedObject);
        } else if (selectedObject instanceof IncludeColumn) {
            reverseEngineering.getIncludeColumns().remove(selectedObject);
        } else if (selectedObject instanceof ExcludeColumn) {
            reverseEngineering.getExcludeColumns().remove(selectedObject);
        } else if (selectedObject instanceof IncludeProcedure) {
            reverseEngineering.getIncludeProcedures().remove(selectedObject);
        } else if (selectedObject instanceof ExcludeProcedure) {
            reverseEngineering.getExcludeProcedures().remove(selectedObject);
        }
    }

    private void removePatternParams(FilterContainer container, Object selectedObject) {
        container.getExcludeTables().remove(selectedObject);
        container.getIncludeColumns().remove(selectedObject);
        container.getExcludeColumns().remove(selectedObject);
        container.getIncludeProcedures().remove(selectedObject);
        container.getExcludeProcedures().remove(selectedObject);
    }
}
