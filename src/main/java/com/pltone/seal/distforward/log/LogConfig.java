package com.pltone.seal.distforward.log;

import com.pltone.seal.distforward.init.FilePathInit;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;

import java.io.File;
import java.io.IOException;

/**
 * 日志配置
 *
 * @author chenlong
 * @version 1.0 2018-02-13
 */
public class LogConfig {
    private static final String CONVERSION_PATTERN = "[%-5p][%d{yyyy-MM-dd HH:mm:ss}][%l]:%m%n";
    private static final String DATE_PATTERN = "'_'yyyyMMdd'.log'";
    private static final Logger LOG4J_ROOT_LOGGER = Logger.getRootLogger();
    private static final Layout PATTERN_LAYOUT = new PatternLayout(CONVERSION_PATTERN);

    private LogConfig() {}

    /**
     * 设置日志最低输出级别
     */
    public static void setLevel() {
        LOG4J_ROOT_LOGGER.setLevel(Level.DEBUG);
    }

    /**
     * 添加控制台日志输出器
     */
    public static void addConsoleAppender() {
        ConsoleAppender consoleAppender = new ConsoleAppender(PATTERN_LAYOUT, ConsoleAppender.SYSTEM_OUT);
        consoleAppender.setThreshold(Level.WARN);
        LOG4J_ROOT_LOGGER.addAppender(consoleAppender);
    }

    /**
     * 添加每日文件日志输出器
     */
    public static void addDailyFileAppender() {
        String filename = FilePathInit.getLogFilePath();
        try {
            DailyRollingFileAppender dailyFileAppender =
                    new DailyRollingFileAppender(PATTERN_LAYOUT, filename, DATE_PATTERN);
            dailyFileAppender.setAppend(true);
            LOG4J_ROOT_LOGGER.addAppender(dailyFileAppender);
        } catch (IOException e) {
            LogLog.error("配置每日日志输出器异常", e);
        }
    }

    /**
     * 初始化log4j配置
     */
    public static void initLog4j() {
        String log4jPropFileName = new StringBuilder()
                .append(FilePathInit.HOME_DIR)
                .append(File.separatorChar)
                .append("log4j.properties")
                .toString();
        File logCnfFile = new File(log4jPropFileName);
        if (logCnfFile.exists()) {
            PropertyConfigurator.configureAndWatch(log4jPropFileName);
            FilePathInit.init();
            return;
        }
        setLevel();
        addConsoleAppender();
        FilePathInit.init();
        addDailyFileAppender();
    }
}