package org.apache.cayenne.modeler.dialog.db.load;

public class DbLoaderData {
    private String catalog;
    private String schema;
    private String tableIncludePattern;
    private String tableExcludePattern;
    private String procedureNamePattern;
    private String meaningfulPk;
    private String namingStrategy;
    private boolean usePrimitives;
    private boolean useJava7Typed;

    public DbLoaderData(String catalog, String schema, String tableIncludePattern, String tableExcludePattern,
                        String procedureNamePattern, String meaningfulPk, String namingStrategy, boolean usePrimitives,
                        boolean useJava7Typed) {
        this.catalog = catalog;
        this.schema = schema;
        this.tableIncludePattern = tableIncludePattern;
        this.tableExcludePattern = tableExcludePattern;
        this.procedureNamePattern = procedureNamePattern;
        this.meaningfulPk = meaningfulPk;
        this.namingStrategy = namingStrategy;
        this.usePrimitives = usePrimitives;
        this.useJava7Typed = useJava7Typed;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableIncludePattern() {
        return tableIncludePattern;
    }

    public String getTableExcludePattern() {
        return tableExcludePattern;
    }

    public String getProcedureNamePattern() {
        return procedureNamePattern;
    }

    public String getMeaningfulPk() {
        return meaningfulPk;
    }

    public String getNamingStrategy() {
        return namingStrategy;
    }

    public boolean isUsePrimitives() {
        return usePrimitives;
    }

    public boolean isUseJava7Typed() {
        return useJava7Typed;
    }
}
