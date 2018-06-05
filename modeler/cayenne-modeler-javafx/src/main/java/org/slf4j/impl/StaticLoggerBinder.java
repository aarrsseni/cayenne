package org.slf4j.impl;

import org.apache.cayenne.modeler.util.ModelerFxLogFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {

    private final static StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    private final static String LOGGER_FACTORY_CLASS_STR = ModelerFxLogFactory.class.getName();

    private final ILoggerFactory loggerFactory = new ModelerFxLogFactory();

    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    public String getLoggerFactoryClassStr() {
        return LOGGER_FACTORY_CLASS_STR;
    }
}
