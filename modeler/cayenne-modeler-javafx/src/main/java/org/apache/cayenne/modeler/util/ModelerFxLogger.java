package org.apache.cayenne.modeler.util;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModelerFxLogger implements Logger {
    private static final int BUFFER_SIZE = 32;
    private static final byte LOG_LEVEL_INFO = 2;
    private static final byte LOG_LEVEL_DEBUG = 1;
    private static final byte LOG_LEVEL_TRACE = 0;
    private static final byte LOG_LEVEL_WARNING = 3;
    private static final byte LOG_LEVEL_ERROR = 4;

    private static final String INFO_LOG_NAME = "INFO";
    private static final String DEBUG_LOG_NAME = "DEBUG";
    private static final String TRACE_LOG_NAME = "TRACE";
    private static final String WARNING_LOG_NAME = "WARNING";
    private static final String ERROR_LOG_NAME = "ERROR";
    private static final String DATE_FORMAT = "yyyy/MM/dd HH.mm.ss";

    /**
     * Logger name
     */
    private String name;
    private int currentLogLevel = LOG_LEVEL_INFO;

    public ModelerFxLogger(String name) {
        this.name = name;
    }

    private String getLogLevel(byte level) {
        switch (level) {
            case LOG_LEVEL_INFO:
                return INFO_LOG_NAME;

            case LOG_LEVEL_DEBUG:
                return DEBUG_LOG_NAME;

            case LOG_LEVEL_TRACE:
                return TRACE_LOG_NAME;

            case LOG_LEVEL_WARNING:
                return WARNING_LOG_NAME;

            case LOG_LEVEL_ERROR:
                return ERROR_LOG_NAME;

            default:
                throw new IllegalStateException("Unregistered log level - " + level);

        }
    }

    private void consoleLog(byte level, String message, Throwable throwable) {
        if(this.isLevelEnabled(level)) {
            StringBuilder buffer = new StringBuilder(BUFFER_SIZE);
            buffer.append(this.getFormattedDate());
            buffer.append(' ');

            buffer.append('[');
            buffer.append(Thread.currentThread().getName());
            buffer.append("] ");

            buffer.append('[');
            String levelStr = this.getLogLevel(level);
            buffer.append(levelStr);
            buffer.append(']');

            buffer.append(' ');
            buffer.append(message);
            this.write(buffer, throwable);
        }
    }

    private void consoleLog(byte level, String message) {
        consoleLog(level, message, (Throwable) null);
    }

    private void consoleLog(byte level, String format, Object... arguments) {
        if(this.isLevelEnabled(level)) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, arguments);
            this.consoleLog(level, tuple.getMessage(), tuple.getThrowable());
        }
    }

    private String getFormattedDate() {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String dateText = formatter.format(currentDate);
        return dateText;
    }

    private void write(StringBuilder buffer, Throwable throwable) {
        PrintStream targetStream = System.err;
        targetStream.println(buffer.toString());
        this.writeThrowable(throwable, targetStream);
        targetStream.flush();
    }

    private void writeThrowable(Throwable throwable, PrintStream targetStream) {
        if(throwable != null) {
            throwable.printStackTrace(targetStream);
        }

    }

    private boolean isLevelEnabled(int logLevel) {
        return (logLevel >= this.currentLogLevel);
    }

    @Override
    public void debug(String message) {
        consoleLog(LOG_LEVEL_DEBUG, message);
    }

    @Override
    public void debug(String message, Object object) {
        consoleLog(LOG_LEVEL_DEBUG, message, object);
    }

    @Override
    public void debug(String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_DEBUG, message, object, secondObject);
    }

    @Override
    public void debug(String message, Object... objects) {
        consoleLog(LOG_LEVEL_DEBUG, message, objects);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_DEBUG, message, throwable);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isLevelEnabled(LOG_LEVEL_DEBUG);
    }

    @Override
    public void debug(Marker marker, String message) {
        consoleLog(LOG_LEVEL_DEBUG, message);
    }

    @Override
    public void debug(Marker marker, String message, Object object) {
        consoleLog(LOG_LEVEL_DEBUG, message, object);
    }

    @Override
    public void debug(Marker marker, String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_DEBUG, message, object, secondObject);
    }

    @Override
    public void debug(Marker marker, String message, Object... objects) {
        consoleLog(LOG_LEVEL_DEBUG, message, objects);
    }

    @Override
    public void debug(Marker marker, String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_DEBUG, message, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLevelEnabled(LOG_LEVEL_INFO);
    }

    @Override
    public void error(String message) {
        consoleLog(LOG_LEVEL_ERROR, message);
    }

    @Override
    public void error(String message, Object object) {
        consoleLog(LOG_LEVEL_ERROR, message, object);
    }

    @Override
    public void error(String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_ERROR, message, object, secondObject);
    }

    @Override
    public void error(String message, Object... objects) {
        consoleLog(LOG_LEVEL_ERROR, message, objects);
    }

    @Override
    public void error(String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_ERROR, message, throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isLevelEnabled(LOG_LEVEL_ERROR);
    }

    @Override
    public void error(Marker marker, String message) {
        consoleLog(LOG_LEVEL_ERROR, message);
    }

    @Override
    public void error(Marker marker, String message, Object object) {
        consoleLog(LOG_LEVEL_ERROR, message, object);
    }

    @Override
    public void error(Marker marker, String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_ERROR, message, object, secondObject);
    }

    @Override
    public void error(Marker marker, String message, Object... objects) {
        consoleLog(LOG_LEVEL_ERROR, message, objects);
    }

    @Override
    public void error(Marker marker, String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_ERROR, message, throwable);
    }

    @Override
    public void info(String message) {
        consoleLog(LOG_LEVEL_INFO, message);
    }

    @Override
    public void info(String message, Object object) {
        consoleLog(LOG_LEVEL_INFO, message, object);
    }

    @Override
    public void info(String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_INFO, message, object, secondObject);
    }

    @Override
    public void info(String message, Object... objects) {
        consoleLog(LOG_LEVEL_INFO, message, objects);
    }

    @Override
    public void info(String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_INFO, message, throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isLevelEnabled(LOG_LEVEL_INFO);
    }

    @Override
    public void info(Marker marker, String message) {
        consoleLog(LOG_LEVEL_INFO, message);
    }

    @Override
    public void info(Marker marker, String message, Object object) {
        consoleLog(LOG_LEVEL_INFO, message, object);
    }

    @Override
    public void info(Marker marker, String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_INFO, message, object, secondObject);
    }

    @Override
    public void info(Marker marker, String message, Object... objects) {
        consoleLog(LOG_LEVEL_INFO, message, objects);
    }

    @Override
    public void info(Marker marker, String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_INFO, message, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLevelEnabled(LOG_LEVEL_WARNING);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTraceEnabled() {
        return isLevelEnabled(LOG_LEVEL_TRACE);
    }

    @Override
    public void trace(String message) {
        consoleLog(LOG_LEVEL_TRACE, message);
    }

    @Override
    public void trace(String message, Object object) {
        consoleLog(LOG_LEVEL_TRACE, message, object);
    }

    @Override
    public void trace(String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_TRACE, message, secondObject);
    }

    @Override
    public void trace(String message, Object... objects) {
        consoleLog(LOG_LEVEL_TRACE, message, objects);
    }

    @Override
    public void trace(String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_TRACE, message, throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isLevelEnabled(LOG_LEVEL_TRACE);
    }

    @Override
    public void trace(Marker marker, String message) {
        consoleLog(LOG_LEVEL_TRACE, message);
    }

    @Override
    public void trace(Marker marker, String message, Object object) {
        consoleLog(LOG_LEVEL_TRACE, message, object);
    }

    @Override
    public void trace(Marker marker, String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_TRACE, message, object, secondObject);
    }

    @Override
    public void trace(Marker marker, String message, Object... objects) {
        consoleLog(LOG_LEVEL_TRACE, message, objects);
    }

    @Override
    public void trace(Marker marker, String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_TRACE, message, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return isErrorEnabled();
    }

    @Override
    public void warn(String message) {
        consoleLog(LOG_LEVEL_WARNING, message);
    }

    @Override
    public void warn(String message, Object object) {
        consoleLog(LOG_LEVEL_WARNING, message, object);
    }

    @Override
    public void warn(String message, Object... objects) {
        consoleLog(LOG_LEVEL_WARNING, message, objects);
    }

    @Override
    public void warn(String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_WARNING, message, secondObject);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_WARNING, message, throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isLevelEnabled(LOG_LEVEL_WARNING);
    }

    @Override
    public void warn(Marker marker, String message) {
        consoleLog(LOG_LEVEL_WARNING, message);
    }

    @Override
    public void warn(Marker marker, String message, Object object) {
        consoleLog(LOG_LEVEL_WARNING, message, object);
    }

    @Override
    public void warn(Marker marker, String message, Object object, Object secondObject) {
        consoleLog(LOG_LEVEL_WARNING, message, object, secondObject);
    }

    @Override
    public void warn(Marker marker, String message, Object... objects) {
        consoleLog(LOG_LEVEL_WARNING, message, objects);
    }

    @Override
    public void warn(Marker marker, String message, Throwable throwable) {
        consoleLog(LOG_LEVEL_WARNING, message, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return isLevelEnabled(LOG_LEVEL_ERROR);
    }
}
