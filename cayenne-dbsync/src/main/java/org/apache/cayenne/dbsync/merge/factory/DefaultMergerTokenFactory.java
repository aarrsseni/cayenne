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
package org.apache.cayenne.dbsync.merge.factory;

import java.util.Collection;

import org.apache.cayenne.dbsync.merge.token.MergerToken;
import org.apache.cayenne.dbsync.merge.token.ValueForNullProvider;
import org.apache.cayenne.dbsync.merge.token.db.AddColumnToDb;
import org.apache.cayenne.dbsync.merge.token.db.AddJoinToDb;
import org.apache.cayenne.dbsync.merge.token.db.CreateTableToDb;
import org.apache.cayenne.dbsync.merge.token.db.DropColumnToDb;
import org.apache.cayenne.dbsync.merge.token.db.DropJoinToDb;
import org.apache.cayenne.dbsync.merge.token.db.DropTableToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetAllowNullToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetColumnTypeToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetGeneratedFlagToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetNotNullToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetPrimaryKeyToDb;
import org.apache.cayenne.dbsync.merge.token.db.SetValueForNullToDb;
import org.apache.cayenne.dbsync.merge.token.model.AddColumnToModel;
import org.apache.cayenne.dbsync.merge.token.model.AddJoinToModel;
import org.apache.cayenne.dbsync.merge.token.model.CreateTableToModel;
import org.apache.cayenne.dbsync.merge.token.model.DropColumnToModel;
import org.apache.cayenne.dbsync.merge.token.model.DropJoinToModel;
import org.apache.cayenne.dbsync.merge.token.model.DropTableToModel;
import org.apache.cayenne.dbsync.merge.token.model.SetAllowNullToModel;
import org.apache.cayenne.dbsync.merge.token.model.SetColumnTypeToModel;
import org.apache.cayenne.dbsync.merge.token.model.SetGeneratedFlagToModel;
import org.apache.cayenne.dbsync.merge.token.model.SetNotNullToModel;
import org.apache.cayenne.dbsync.merge.token.model.SetPrimaryKeyToModel;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.relationship.DbJoin;

/**
 * @since 4.0
 */
public class DefaultMergerTokenFactory implements MergerTokenFactory {

    @Override
    public MergerToken createCreateTableToModel(DbEntity entity) {
        return new CreateTableToModel(entity);
    }

    @Override
    public MergerToken createCreateTableToDb(DbEntity entity) {
        return new CreateTableToDb(entity);
    }

    @Override
    public MergerToken createDropTableToModel(DbEntity entity) {
        return new DropTableToModel(entity);
    }

    @Override
    public MergerToken createDropTableToDb(DbEntity entity) {
        return new DropTableToDb(entity);
    }

    @Override
    public MergerToken createAddColumnToModel(DbEntity entity, DbAttribute column) {
        return new AddColumnToModel(entity, column);
    }

    @Override
    public MergerToken createAddColumnToDb(DbEntity entity, DbAttribute column) {
        return new AddColumnToDb(entity, column);
    }

    @Override
    public MergerToken createDropColumnToModel(DbEntity entity, DbAttribute column) {
        return new DropColumnToModel(entity, column);
    }

    @Override
    public MergerToken createDropColumnToDb(DbEntity entity, DbAttribute column) {
        return new DropColumnToDb(entity, column);
    }

    @Override
    public MergerToken createSetNotNullToModel(DbEntity entity, DbAttribute column) {
        return new SetNotNullToModel(entity, column);
    }

    @Override
    public MergerToken createSetNotNullToDb(DbEntity entity, DbAttribute column) {
        return new SetNotNullToDb(entity, column);
    }

    @Override
    public MergerToken createSetAllowNullToModel(DbEntity entity, DbAttribute column) {
        return new SetAllowNullToModel(entity, column);
    }

    @Override
    public MergerToken createSetAllowNullToDb(DbEntity entity, DbAttribute column) {
        return new SetAllowNullToDb(entity, column);
    }

    @Override
    public MergerToken createSetValueForNullToDb(DbEntity entity, DbAttribute column, ValueForNullProvider valueForNullProvider) {
        return new SetValueForNullToDb(entity, column, valueForNullProvider);
    }

    @Override
    public MergerToken createSetColumnTypeToModel(
            DbEntity entity,
            DbAttribute columnOriginal,
            DbAttribute columnNew) {
        return new SetColumnTypeToModel(entity, columnOriginal, columnNew);
    }

    @Override
    public MergerToken createSetColumnTypeToDb(
            DbEntity entity,
            DbAttribute columnOriginal,
            DbAttribute columnNew) {
        return new SetColumnTypeToDb(entity, columnOriginal, columnNew);
    }

    @Override
    public MergerToken createAddJoinToDb(DbJoin join) {
        return new AddJoinToDb(join);
    }

    @Override
    public MergerToken createDropJoinToDb(DbJoin dbJoin) {
        return new DropJoinToDb(dbJoin);
    }

    @Override
    public MergerToken createAddJoinToModel(DbJoin dbJoin) {
        return new AddJoinToModel(dbJoin);
    }

    @Override
    public MergerToken createDropJoinToModel(DbJoin dbJoin) {
        return new DropJoinToModel(dbJoin);
    }

    @Override
    public MergerToken createSetPrimaryKeyToDb(
            DbEntity entity,
            Collection<DbAttribute> primaryKeyOriginal,
            Collection<DbAttribute> primaryKeyNew,
            String detectedPrimaryKeyName) {
        return new SetPrimaryKeyToDb(
                entity,
                primaryKeyOriginal,
                primaryKeyNew,
                detectedPrimaryKeyName);
    }

    @Override
    public MergerToken createSetPrimaryKeyToModel(
            DbEntity entity,
            Collection<DbAttribute> primaryKeyOriginal,
            Collection<DbAttribute> primaryKeyNew,
            String detectedPrimaryKeyName) {
        return new SetPrimaryKeyToModel(
                entity,
                primaryKeyOriginal,
                primaryKeyNew,
                detectedPrimaryKeyName);
    }

    @Override
    public MergerToken createSetGeneratedFlagToDb(DbEntity entity, DbAttribute column, boolean isGenerated) {
        return new SetGeneratedFlagToDb(entity, column, isGenerated);
    }

    @Override
    public MergerToken createSetGeneratedFlagToModel(DbEntity entity, DbAttribute column, boolean isGenerated) {
        return new SetGeneratedFlagToModel(entity, column, isGenerated);
    }
}
