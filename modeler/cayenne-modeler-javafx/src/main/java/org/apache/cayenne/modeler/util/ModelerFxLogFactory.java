package org.apache.cayenne.modeler.util;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ModelerFxLogFactory implements ILoggerFactory {

    /**
     * Local cache of modeler loggers
     */
    protected Map<String, ModelerFxLogger> localCache;

    public ModelerFxLogFactory() {
        localCache = new HashMap<>();
    }

    @Override
    public Logger getLogger(String name) {
        ModelerFxLogger local = localCache.get(name);
        if (local == null) {
            //Logger def = LoggerFactory.getLogger(name);
            local = new ModelerFxLogger(name);
            localCache.put(name, local);
        }
        return local;
    }
}
