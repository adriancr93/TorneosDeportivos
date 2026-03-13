package org.example.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {
    private static final Logger logger = LogManager.getLogger(LoggerUtil.class);

    public static void logError(Exception e) {
        logger.error(e.getMessage(), e);
    }

    public static void logError(String message, Exception e) {
        logger.error(message, e);
    }

    public static void logInfo(String message) {
        logger.info(message);
    }
}
