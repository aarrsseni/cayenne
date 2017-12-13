package org.apache.cayenne.access.translator.select;

import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.query.FluentSelect;
import org.apache.cayenne.query.ObjectSelect;

public interface ObjectSelectTranslatorFactory {

    SelectTranslator translator(FluentSelect<?> query, DbAdapter adapter, EntityResolver entityResolver);
}
