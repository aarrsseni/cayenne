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
import org.apache.cayenne.configuration.DataChannelDescriptor;
import org.apache.cayenne.dbsync.reverse.dbload.DbRelationshipDetected;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.DetectedDbEntity;
import org.apache.cayenne.map.EJBQLQueryDescriptor;
import org.apache.cayenne.map.Embeddable;
import org.apache.cayenne.map.EmbeddableAttribute;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.map.ProcedureQueryDescriptor;
import org.apache.cayenne.map.QueryDescriptor;
import org.apache.cayenne.map.SQLTemplateDescriptor;
import org.apache.cayenne.map.SelectQueryDescriptor;
import org.apache.cayenne.modeler.ProjectController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @since 4.1
 */
public class DefaultFindService implements FindService{

    @Inject
    public ProjectController projectController;

    /**
     * Result sort priority based on result type
     */
    private static final Map<Class<?>, Integer> PRIORITY_BY_TYPE = new HashMap<>();
    static {
        PRIORITY_BY_TYPE.put(ObjEntity.class,                1);
        PRIORITY_BY_TYPE.put(DbEntity.class,                 2);
        PRIORITY_BY_TYPE.put(DetectedDbEntity.class,         2); // this one comes from db reverse engineering
        PRIORITY_BY_TYPE.put(ObjAttribute.class,             5);
        PRIORITY_BY_TYPE.put(DbAttribute.class,              6);
        PRIORITY_BY_TYPE.put(ObjRelationship.class,          7);
        PRIORITY_BY_TYPE.put(DbRelationship.class,           8);
        PRIORITY_BY_TYPE.put(DbRelationshipDetected.class,   8); // this one comes from db reverse engineering
        PRIORITY_BY_TYPE.put(QueryDescriptor.class,          9);
        PRIORITY_BY_TYPE.put(SelectQueryDescriptor.class,   10);
        PRIORITY_BY_TYPE.put(EJBQLQueryDescriptor.class,    11);
        PRIORITY_BY_TYPE.put(SQLTemplateDescriptor.class,   12);
        PRIORITY_BY_TYPE.put(ProcedureQueryDescriptor.class,13);
        PRIORITY_BY_TYPE.put(Embeddable.class,              14);
        PRIORITY_BY_TYPE.put(EmbeddableAttribute.class,     15);
    }


    public List<SearchResultEntry> search(String searchStr) {
        Pattern pattern = Pattern.compile(searchStr, Pattern.CASE_INSENSITIVE);
        List<SearchResultEntry> result = new ArrayList<>();
        for (DataMap dataMap : ((DataChannelDescriptor) projectController.getProject().getRootNode()).getDataMaps()) {
            searchInQueryDescriptors(pattern, result, dataMap);
            searchInEmbeddables(pattern, result, dataMap);
            searchInDbEntities(pattern, result, dataMap);
            searchInObjEntities(pattern, result, dataMap);
        }
        Collections.sort(result);
        return result;
    }

    private void searchInQueryDescriptors(Pattern pattern, List<SearchResultEntry> result, DataMap dataMap) {
        for (QueryDescriptor q : dataMap.getQueryDescriptors()) {
            if (match(q.getName(), pattern)) {
                result.add(new SearchResultEntry(q, q.getName()));
            }
        }
    }

    private void searchInEmbeddables(Pattern pattern, List<SearchResultEntry> result, DataMap dataMap) {
        for (Embeddable emb : dataMap.getEmbeddables()) {
            if (match(emb.getClassName(), pattern)) {
                result.add(new SearchResultEntry(emb, emb.getClassName()));
            }
            for (EmbeddableAttribute attr : emb.getAttributes()) {
                if (match(attr.getName(), pattern)) {
                    result.add(new SearchResultEntry(attr, emb.getClassName() + "." + attr.getName()));
                }
            }
        }
    }

    private void searchInObjEntities(Pattern pattern, List<SearchResultEntry> result, DataMap dataMap) {
        for (ObjEntity ent : dataMap.getObjEntities()) {
            if (match(ent.getName(), pattern)) {
                result.add(new SearchResultEntry(ent, ent.getName()));
            }
            for (ObjAttribute attr : ent.getAttributes()) {
                if (match(attr.getName(), pattern)) {
                    result.add(new SearchResultEntry(attr, ent.getName() + "." + attr.getName()));
                }
            }
            for (ObjRelationship rel : ent.getRelationships()) {
                if (match(rel.getName(), pattern)) {
                    result.add(new SearchResultEntry(rel, ent.getName() + "." + rel.getName()));
                }
            }
        }
    }

    private void searchInDbEntities(Pattern pattern, List<SearchResultEntry> result, DataMap dataMap) {
        for (DbEntity ent : dataMap.getDbEntities()) {
            if (match(ent.getName(), pattern)) {
                result.add(new SearchResultEntry(ent, ent.getName()));
            }
            for (DbAttribute attr : ent.getAttributes()) {
                if (match(attr.getName(), pattern)) {
                    result.add(new SearchResultEntry(attr, ent.getName() + "." + attr.getName()));
                }
            }
            for (DbRelationship rel : ent.getRelationships()) {
                if (match(rel.getName(), pattern)) {
                    result.add(new SearchResultEntry(rel, ent.getName() + "." + rel.getName()));
                }
            }

            checkCatalogOrSchema(pattern, result, ent, ent.getCatalog());
            checkCatalogOrSchema(pattern, result, ent, ent.getSchema());
        }
    }

    private boolean match(String entityName, Pattern pattern) {
        return pattern.matcher(entityName).find();
    }

    private void checkCatalogOrSchema(Pattern pattern, List<SearchResultEntry> paths, DbEntity ent, String catalogOrSchema) {
        if (catalogOrSchema != null && !catalogOrSchema.isEmpty()) {
            if (match(catalogOrSchema, pattern)) {
                SearchResultEntry entry = new SearchResultEntry(ent, ent.getName());
                if(!paths.contains(entry)) {
                    paths.add(entry);
                }
            }
        }
    }

    /**
     * Search result holder
     */
    public static class SearchResultEntry implements Comparable<SearchResultEntry>{
        private final Object object;
        private final String name;

        public SearchResultEntry(Object object, String name) {
            this.object = Objects.requireNonNull(object);
            this.name = Objects.requireNonNull(name);
        }

        public String getName() {
            return name;
        }

        public Object getObject() {
            return object;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SearchResultEntry entry = (SearchResultEntry) o;
            return name.equals(entry.name) && object.getClass().equals(entry.object.getClass());
        }

        @Override
        public int hashCode() {
            int result = object.getClass().hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }

        private int getPriority() {
            Integer priority = PRIORITY_BY_TYPE.get(object.getClass());
            if(priority == null) {
                throw new NullPointerException("Unknown type: " + object.getClass().getCanonicalName());
            }
            return priority;
        }

        @Override
        public int compareTo(SearchResultEntry o) {
            int res = getPriority() - o.getPriority();
            if(res != 0) {
                return res;
            }
            return getName().compareTo(o.getName());
        }
    }
}
