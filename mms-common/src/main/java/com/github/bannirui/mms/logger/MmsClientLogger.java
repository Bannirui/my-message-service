package com.github.bannirui.mms.logger;

import com.github.bannirui.mms.common.MmsException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MmsClientLogger {

    private static final String MMS_LOG_ROOT = "mms.client.logging.path";
    private static final String MMS_LOG_LEVEL = "mms.client.logging.level";

    static Logger createLogger(final String loggerName) {
        boolean isLoadConfig = Boolean.parseBoolean(System.getProperty("mms.client.log.loadconfig", "true"));
        final String log4j2ResourceFile = System.getProperty("mms.client.log4j2.resource.fileName", "log4j2_mms_client.xml");
        String mmsClientLogRoot = System.getProperty(MMS_LOG_ROOT, "/data/logs/mms");
        System.setProperty(MMS_LOG_ROOT, mmsClientLogRoot);
        String mmsClientLogLevel = System.getProperty(MMS_LOG_LEVEL, "info");
        System.setProperty(MMS_LOG_LEVEL, mmsClientLogLevel);
        // if (isLoadConfig) {
        //     try {
        //         Class<?> joranConfigurator = Class.forName("org.apache.logging.log4j.core.config.Configurator");
        //         Method initialize = joranConfigurator.getDeclaredMethod("initialize", String.class, String.class);
        //         initialize.invoke(joranConfigurator, "log4j2", log4j2ResourceFile);
        //     } catch (Exception e) {
        //         throw new MmsException("创建logger失败", e);
        //     }
        // }
        return LoggerFactory.getLogger(MmsClientLogger.class);
    }
}

