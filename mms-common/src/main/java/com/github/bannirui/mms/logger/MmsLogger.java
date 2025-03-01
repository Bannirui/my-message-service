package com.github.bannirui.mms.logger;

import org.slf4j.Logger;

public class MmsLogger {

    private static final String MMS_LOGGER = "mms_logger";
    private static final String STATISTIC_LOGGER = "mms_statistic_logger";

    public static Logger log;
    public static Logger statisticLog;

    static {
        log = MmsClientLogger.createLogger(MMS_LOGGER);
        statisticLog = MmsClientLogger.createLogger(STATISTIC_LOGGER);
    }
}

