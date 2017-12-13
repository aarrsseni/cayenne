package org.apache.cayenne.query;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.reflect.ClassDescriptor;
import org.apache.cayenne.util.CayenneMapEntry;

import java.util.Iterator;

public class ObjectSelectQueryPrefetchRouterAction implements PrefetchProcessor {

    FluentSelect<?> query;
    QueryRouter router;
    EntityResolver resolver;
    ClassDescriptor classDescriptor;

    /**
     * Routes query prefetches, but not the query itself.
     */
    void route(FluentSelect query, QueryRouter router, EntityResolver resolver) {
        if (!query.isFetchingDataRows() && query.getPrefetches() != null) {

            this.query = query;
            this.router = router;
            this.resolver = resolver;
            this.classDescriptor = query.getMetaData(resolver).getClassDescriptor();

            query.getPrefetches().traverse(this);
        }
    }

    public boolean startPhantomPrefetch(PrefetchTreeNode node) {
        return true;
    }

    public boolean startDisjointPrefetch(PrefetchTreeNode node) {
        // don't do anything to root
        if (node == query.getPrefetches()) {
            return true;
        }

        String prefetchPath = node.getPath();

        // find last relationship
        Iterator<CayenneMapEntry> it = classDescriptor.getEntity().resolvePathComponents(
                prefetchPath);

        ObjRelationship relationship = null;
        while (it.hasNext()) {
            relationship = (ObjRelationship) it.next();
        }

        if (relationship == null) {
            throw new CayenneRuntimeException("Invalid prefetch '%s' for entity '%s'"
                    , prefetchPath, classDescriptor.getEntity().getName());
        }

        // chain query and entity qualifiers
        Expression queryQualifier = query.getWhere();

        Expression entityQualifier = classDescriptor
                .getEntityInheritanceTree()
                .qualifierForEntityAndSubclasses();

        if (entityQualifier != null) {
            queryQualifier = (queryQualifier != null) ? queryQualifier
                    .andExp(entityQualifier) : entityQualifier;
        }

        // create and configure PrefetchSelectQuery
        PrefetchSelectQuery prefetchQuery = new PrefetchSelectQuery(
                prefetchPath,
                relationship);
        prefetchQuery.setStatementFetchSize(query.getStatementFetchSize());

        prefetchQuery.setQualifier(classDescriptor.getEntity().translateToRelatedEntity(
                queryQualifier,
                prefetchPath));

        if (relationship.isSourceIndependentFromTargetChange()) {
            // setup extra result columns to be able to relate result rows to the parent
            // result objects.
            prefetchQuery.addResultPath("db:"
                    + relationship.getReverseDbRelationshipPath());
        }

        // pass prefetch subtree to enable joint prefetches...
        prefetchQuery.setPrefetchTree(node);

        // route...
        prefetchQuery.route(router, resolver, null);
        return true;
    }

    public boolean startDisjointByIdPrefetch(PrefetchTreeNode prefetchTreeNode) {
        // simply pass through
        return true;
    }

    public boolean startJointPrefetch(PrefetchTreeNode node) {
        // simply pass through
        return true;
    }

    public boolean startUnknownPrefetch(PrefetchTreeNode node) {
        // don't do anything to root
        if (node == query.getPrefetches()) {
            return true;
        }

        // route unknown as disjoint...
        return startDisjointPrefetch(node);
    }

    public void finishPrefetch(PrefetchTreeNode node) {
    }
}
