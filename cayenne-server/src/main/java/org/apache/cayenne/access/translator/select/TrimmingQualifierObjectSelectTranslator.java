package org.apache.cayenne.access.translator.select;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.map.DbAttribute;

import java.sql.Types;

public class TrimmingQualifierObjectSelectTranslator extends QualifierObjectSelectTranslator {
    protected String trimFunction;

    /**
     * Constructor for TrimmingQualifierTranslator.
     */
    public TrimmingQualifierObjectSelectTranslator(QueryAssembler queryAssembler, String trimFunction) {
        super(queryAssembler);
        this.trimFunction = trimFunction;
    }

    /**
     * Adds special handling of CHAR columns.
     */
    @Override
    protected void processColumn(DbAttribute dbAttr) {
        if (dbAttr.getType() == Types.CHAR) {
            out.append(trimFunction).append("(");
            super.processColumn(dbAttr);
            out.append(')');
        } else {
            super.processColumn(dbAttr);
        }
    }

    /**
     * Adds special handling of CHAR columns.
     */
    @Override
    protected void processColumnWithQuoteSqlIdentifiers(DbAttribute dbAttr, Expression pathExp) {

        if (dbAttr.getType() == Types.CHAR) {
            out.append(trimFunction).append("(");
            super.processColumnWithQuoteSqlIdentifiers(dbAttr, pathExp);
            out.append(')');
        } else {
            super.processColumnWithQuoteSqlIdentifiers(dbAttr, pathExp);
        }
    }

    /**
     * Returns the trimFunction.
     *
     * @return String
     */
    public String getTrimFunction() {
        return trimFunction;
    }

    /**
     * Sets the trimFunction.
     *
     * @param trimFunction
     *            The trimFunction to set
     */
    public void setTrimFunction(String trimFunction) {
        this.trimFunction = trimFunction;
    }
}
