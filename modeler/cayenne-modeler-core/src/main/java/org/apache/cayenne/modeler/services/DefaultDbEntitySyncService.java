package org.apache.cayenne.modeler.services;

import org.apache.cayenne.dbsync.naming.DefaultObjectNameGenerator;
import org.apache.cayenne.map.DbRelationship;

public class DefaultDbEntitySyncService {
    static class PreserveRelationshipNameGenerator extends DefaultObjectNameGenerator {

        @Override
        public String relationshipName(DbRelationship... relationshipChain) {
            if(relationshipChain.length == 0) {
                return super.relationshipName(relationshipChain);
            }
            DbRelationship last = relationshipChain[relationshipChain.length - 1];
            // must be in sync with DefaultBaseNameVisitor.visitDbRelationship
            if(last.getName().startsWith("untitledRel")) {
                return super.relationshipName(relationshipChain);
            }

            // keep manually set relationship name
            return last.getName();
        }
    }
}
