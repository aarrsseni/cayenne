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

import org.apache.cayenne.dbsync.reverse.dbimport.Catalog;
import org.apache.cayenne.dbsync.reverse.dbimport.IncludeTable;
import org.apache.cayenne.dbsync.reverse.dbimport.PatternParam;
import org.apache.cayenne.dbsync.reverse.dbimport.ReverseEngineering;
import org.apache.cayenne.dbsync.reverse.dbimport.Schema;
import org.apache.cayenne.modeler.pref.DBConnectionInfo;

/**
 * @since 4.1
 */
public interface ReverseEngineeringService {
    void saveConnectionInfo();

    DBConnectionInfo getConnectionInfoFromPreferences();

    void checkBuildConfig(DBConnectionInfo dbConnectionInfo);

    boolean datamapPreferencesExist();

    boolean reverseEngineeringIsEmpty(ReverseEngineering reverseEngineering);

    PatternParam getPatternParamToContainer(Class paramClass, Object selectedObject, String name);

    PatternParam getPatternParamToIncludeTable(Class paramClass, Object selectedObject, String name);

    void deleteChilds(Catalog catalog, Object selectedObject);

    void deleteChilds(Schema schema, Object selectedObject);

    void deleteChilds(IncludeTable includeTable, Object selectedObject);

    void deleteChilds(ReverseEngineering reverseEngineering, Object selectedObject);
}
