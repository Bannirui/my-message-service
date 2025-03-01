package com.github.bannirui.mms.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorServiceUtils {

    public static final Logger logger = LoggerFactory.getLogger(ExecutorServiceUtils.class);

    public static void gracefullyShutdown(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                    logger.info("consumer pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}


