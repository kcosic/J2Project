package main.hr.kcosic.project.utils;

import java.util.logging.Logger;

public class LogUtils {

    private static final Logger logger = Logger.getAnonymousLogger();


    public static void logSevere(String message) {
        logger.severe(message);
    }
    public static void logWarning(String message) {
        logger.warning(message);
    }
    public static void logConfig(String message) {
        logger.config(message);
    }

    public static void logInfo(String message) {
        logger.info(message);
    }
}
