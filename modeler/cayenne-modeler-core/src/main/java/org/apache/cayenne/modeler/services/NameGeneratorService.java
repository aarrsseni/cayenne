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
package org.apache.cayenne.modeler.services;

import com.google.inject.Inject;
import org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator;
import org.apache.cayenne.dbsync.naming.NoStemStemmer;
import org.apache.cayenne.dbsync.naming.ObjectNameGenerator;
import org.apache.cayenne.modeler.ClassLoadingService;

import java.util.Arrays;
import java.util.Vector;

/**
 * @since 4.1
 */
public class NameGeneratorService {
    @Inject
    private PreferenceService preferenceService;
    @Inject
    private ClassLoadingService modelerClassLoader;

    private static final String STRATEGIES_PREFERENCE = "name.generators.recent";

    /**
     * Naming strategies to appear in combobox by default
     */
    private static final Vector<String> PREDEFINED_STRATEGIES = new Vector<String>();
    static {
        PREDEFINED_STRATEGIES.add(DefaultObjectNameGenerator.class.getCanonicalName());
    }


    /**
     * @return last used strategies, PREDEFINED_STRATEGIES by default
     */
    public Vector<String> getLastUsedStrategies() {

        String prop = null;

        if (preferenceService.getMainPreferenceForProject() != null) {
            prop = preferenceService.getMainPreferenceForProject().get(STRATEGIES_PREFERENCE, null);
        }

        if (prop == null) {
            return PREDEFINED_STRATEGIES;
        }

        return new Vector<String>(Arrays.asList(prop.split(",")));
    }

    public ObjectNameGenerator createNamingStrategy()
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        return modelerClassLoader
                .loadClass(ObjectNameGenerator.class, getLastUsedStrategies().get(0)).newInstance();
    }

    public ObjectNameGenerator defaultNameGenerator() {
        return new DefaultObjectNameGenerator(NoStemStemmer.getInstance());
    }
}
