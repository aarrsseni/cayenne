package org.apache.cayenne.dba.hsqldb;

import org.apache.cayenne.access.translator.select.DefaultObjectSelectTranslator;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.query.Query;

public class HSQLObjectSelectTranslator extends DefaultObjectSelectTranslator {

    public HSQLObjectSelectTranslator(Query query, DbAdapter adapter, EntityResolver entityResolver) {
        super(query, adapter, entityResolver);
    }

    @Override
    protected void appendLimitAndOffsetClauses(StringBuilder buffer) {
        int offset = queryMetadata.getFetchOffset();
        int limit = queryMetadata.getFetchLimit();

        if (offset > 0 || limit > 0) {
            buffer.append(" LIMIT ");

            // both OFFSET and LIMIT must be present, so come up with defaults
            // if one of
            // them is not set by the user
            if (limit == 0) {
                limit = Integer.MAX_VALUE;
            }

            buffer.append(limit).append(" OFFSET ").append(offset);
        }
    }
}
