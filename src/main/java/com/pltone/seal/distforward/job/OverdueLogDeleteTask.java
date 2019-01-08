package com.pltone.seal.distforward.job;

import com.pltone.seal.distforward.cnf.ServiceProperties;
import com.pltone.seal.distforward.init.FilePathInit;
import com.pltone.seal.distforward.util.FileUtil;
import com.pltone.seal.distforward.util.TimeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 删除过期日志任务
 *
 * @author chenlong
 * @version 1.0 2018-10-10
 */
public class OverdueLogDeleteTask {
    private static final Logger logger = LoggerFactory.getLogger(OverdueLogDeleteTask.class);
    private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L;
    private static final long LOG_OVERDUE_DURATION = ServiceProperties.getLogOverdue();
    private static final Runnable task = () -> {
        final StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("删除超过").append(LOG_OVERDUE_DURATION).append("天的日志：");
        File[] files = FileUtil.deleteFilesBeforeDateInFolder(FilePathInit.LOG_DIR, LOG_OVERDUE_DURATION, TimeUnit.DAYS);
        if (files == null || files.length == 0) {
            logBuilder.append("无过期日志。");
        } else {
            logBuilder.append('（').append(files.length).append("个日志文件）");
            for (File file : files) {
                logBuilder.append('\n').append(file.getName());
            }
        }
        logger.info(logBuilder.toString());
    };

    public static ScheduledExecutorService execute() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 每日零点执行
        long initDelay = TimeUtil.getZeroMillisByJdk8() - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : ONE_DAY_MILLIS + initDelay;
        service.scheduleAtFixedRate(task, initDelay, ONE_DAY_MILLIS, TimeUnit.MILLISECONDS);
        return service;
    }
}
