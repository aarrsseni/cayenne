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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

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

    public static String[] getRegisteredTypeNames() {
        String[] nonPrimitivesNames = {
                String.class.getName(),
                BigDecimal.class.getName(),
                BigInteger.class.getName(),
                Boolean.class.getName(),
                Byte.class.getName(),
                Character.class.getName(),
                Date.class.getName(),
                java.util.Date.class.getName(),
                Double.class.getName(),
                Float.class.getName(),
                Integer.class.getName(),
                Long.class.getName(),
                Short.class.getName(),
                Time.class.getName(),
                Timestamp.class.getName(),
                Date.class.getName(),
                GregorianCalendar.class.getName(),
                Calendar.class.getName(),
                UUID.class.getName(),
                Serializable.class.getName(),
                "java.lang.Character[]",
                "java.lang.Byte[]",
                "java.time.LocalDate",
                "java.time.LocalTime",
                "java.time.LocalDateTime"
        };
        Arrays.sort(nonPrimitivesNames);

        String[] primitivesNames = {
                "boolean", "byte", "byte[]", "char", "char[]", "double", "float", "int", "long", "short"
        };

        String[] finalList = new String[primitivesNames.length + nonPrimitivesNames.length + 1];

        finalList[0] = "";
        System.arraycopy(primitivesNames, 0, finalList, 1, primitivesNames.length);
        System.arraycopy(
                nonPrimitivesNames,
                0,
                finalList,
                primitivesNames.length + 1,
                nonPrimitivesNames.length);

        return finalList;
    }
}
