package com.javabaas.javasdk.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by zangyilin on 2017/11/1.
 */
public class JBLogger {

    public static final int LOG_LEVEL_VERBOSE = 1 << 1;
    public static final int LOG_LEVEL_DEBUG = 1 << 2;
    public static final int LOG_LEVEL_INFO = 1 << 3;
    public static final int LOG_LEVEL_WARNING = 1 << 4;
    public static final int LOG_LEVEL_ERROR = 1 << 5;
    public static final int LOG_LEVEL_NONE = ~0;
    boolean enabled = false;
    int logLevel = LOG_LEVEL_NONE;

    public boolean isDebugEnabled() {
        return enabled;
    }

    public void setDebugEnabled(boolean enable) {
        enabled = enable;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }


    private static final Logger logger = LogManager.getLogger(JBLogger.class);


    public static JBLogger instance() {
        synchronized (JBLogger.class) {
            if (instance == null) {
                instance = new JBLogger();
            }
        }
        return instance;
    }

    private JBLogger() {}

    private static JBLogger instance;

    public int v(String tag, String msg) {
        if (isDebugEnabled()) {
            logger.info(msg);
        }
        return 0;
    }

    public int v(String tag, String msg, Throwable tr) {
        if (isDebugEnabled()) {
            logger.info(msg, tr);
        }
        return 0;
    }

    public int d(String tag, String msg) {
        if (isDebugEnabled()) {
            logger.debug(msg);
        }
        return 0;
    }

    public int d(String tag, String msg, Throwable tr) {
        if (isDebugEnabled()) {
            logger.debug(msg, tr);
        }
        return 0;
    }

    public int i(String tag, String msg) {
        if (isDebugEnabled()) {
            logger.info(msg);
        }
        return 0;
    }

    public int i(String tag, String msg, Throwable tr) {
        if (isDebugEnabled()) {
            logger.info(msg, tr);
        }
        return 0;
    }

    public int w(String tag, String msg) {
        if (isDebugEnabled()) {
            logger.warn(msg);
        }
        return 0;
    }

    public int w(String tag, String msg, Throwable tr) {
        if (isDebugEnabled()) {
            logger.warn(msg, tr);
        }
        return 0;
    }

    public int w(String tag, Throwable tr) {
        if (isDebugEnabled()) {
            logger.warn(tr);
        }
        return 0;
    }

    public int e(String tag, String msg) {
        if (isDebugEnabled()) {
            logger.error(msg);
        }
        return 0;
    }

    public int e(String tag, String msg, Throwable tr) {
        if (isDebugEnabled()) {
            logger.error(msg, tr);
        }
        return 0;
    }
}
