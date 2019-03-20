package org.apache.cayenne.modeler.configuration.xml;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.configuration.xml.XMLDataMapLoader;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.relationship.DbJoin;
import org.apache.cayenne.modeler.map.relationship.DbJoinMutable;
import org.apache.cayenne.modeler.map.relationship.DbJoinMutableBuilder;
import org.apache.cayenne.resource.Resource;

public class XMLDataMapModelerLoader extends XMLDataMapLoader {

    @Override
    public synchronized DataMap load(Resource configurationResource) throws CayenneRuntimeException {
        DataMap dataMap = super.load(configurationResource);
        DbJoinMutableBuilder builder = new DbJoinMutableBuilder();
        List<DbJoin> dbJoins = dataMap.getDbJoinList();
        List<DbJoinMutable> dbJoinMutableList = dbJoins.stream()
                .map(builder::buildFromJoin)
                .collect(Collectors.toList());
        dbJoins.clear();
        dbJoins.addAll(dbJoinMutableList);
        return dataMap;
    }
}
