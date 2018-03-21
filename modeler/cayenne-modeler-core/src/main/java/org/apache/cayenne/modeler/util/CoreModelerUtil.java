package org.apache.cayenne.modeler.util;

import org.apache.cayenne.reflect.PropertyUtils;
import org.apache.cayenne.util.CayenneMapEntry;

public final class CoreModelerUtil {
    public static String getObjectName(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof CayenneMapEntry) {
            return ((CayenneMapEntry) object).getName();
        } else if (object instanceof String) {
            return (String) object;
        } else {
            try {
                // use reflection
                return (String) PropertyUtils.getProperty(object, "name");
            }
            catch (Exception ex) {
                return null;
            }
        }
    }
}
