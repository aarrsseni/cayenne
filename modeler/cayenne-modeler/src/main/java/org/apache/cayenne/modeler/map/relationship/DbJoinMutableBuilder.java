package org.apache.cayenne.modeler.map.relationship;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.xml.DataChannelMetaData;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.map.relationship.DbJoinBuilder;
import org.apache.cayenne.modeler.Application;
import org.apache.cayenne.project.extension.info.ObjectInfo;

public class DbJoinMutableBuilder extends DbJoinBuilder {

    public DbJoinMutable buildFromJoin(DbJoin dbJoin) {
        DataChannelMetaData metaData = Application.getInstance().getMetaData();
        String comment = ObjectInfo
                .getFromMetaData(metaData, dbJoin, ObjectInfo.COMMENT);
        DbJoinMutable dbJoinMutable = new DbJoinMutable(
                dbJoin.getDbJoinCondition(),
                dbJoin.getDbEntities(),
                dbJoin.getNames(),
                dbJoin.getToDependentPkSemantics(),
                dbJoin.getToManySemantics(),
                dbJoin.getDataMap());
        ObjectInfo.putToMetaData(metaData, dbJoinMutable, ObjectInfo.COMMENT, comment);
        return dbJoinMutable;
    }

    public DbJoinMutable build() {
        if(dbJoinCondition == null ||
                dbEntities == null ||
                names == null ||
                toDependentPkSemantics == null ||
                toManySemantics == null ||
                dataMap == null) {
            throw new CayenneRuntimeException("Miss parameters to create dbJoin.");
        }
        return new DbJoinMutable(
                dbJoinCondition,
                dbEntities,
                names,
                toDependentPkSemantics,
                toManySemantics,
                dataMap);
    }
}
