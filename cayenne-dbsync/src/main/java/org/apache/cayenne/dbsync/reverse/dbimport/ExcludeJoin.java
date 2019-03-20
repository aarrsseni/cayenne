package org.apache.cayenne.dbsync.reverse.dbimport;

public class ExcludeJoin extends PatternParam {
    public ExcludeJoin(){

    }

    public ExcludeJoin(String pattern){
        super(pattern);
    }

    public ExcludeJoin(ExcludeJoin original) {
        super(original);
    }
}
