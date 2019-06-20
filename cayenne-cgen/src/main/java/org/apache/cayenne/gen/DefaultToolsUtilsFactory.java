/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.gen;

import java.util.List;

import org.apache.cayenne.di.AdhocObjectFactory;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.gen.property.PropertyDescriptorCreator;
import org.apache.cayenne.tools.ToolsConstants;
import org.slf4j.Logger;

/**
 * @since 4.2
 */
public class DefaultToolsUtilsFactory implements ToolsUtilsFactory {

    @Inject
    private AdhocObjectFactory objectFactory;

    @Inject(ToolsConstants.CUSTOM_PROPERTIES)
    private List<PropertyDescriptorCreator> propertyList;

    @Inject(ToolsConstants.PATTERN_PROPERTIES)
    private List<String> namePatternDictionary;

    @Override
    public ImportUtils createImportUtils(StringUtils stringUtils) {
        return new ImportUtils(stringUtils);
    }

    @Override
    public PropertyUtils createPropertyUtils(Logger logger,
                                             ImportUtils importUtils,
                                             StringUtils stringUtils) {
        return new PropertyUtils(importUtils,
                stringUtils,
                objectFactory,
                propertyList,
                logger);
    }

    @Override
    public StringUtils createStringUtils() {
        return new StringUtils(namePatternDictionary);
    }

}
