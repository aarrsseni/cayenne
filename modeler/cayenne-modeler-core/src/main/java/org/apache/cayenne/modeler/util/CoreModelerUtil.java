/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.modeler.util;

import org.apache.cayenne.reflect.PropertyUtils;
import org.apache.cayenne.util.CayenneMapEntry;

/**
 * @since 4.1
 */
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
