package com.pltone.cnf;

import com.pltone.init.FilePathInit;
import com.pltone.util.FileUtil;
import com.pltone.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 服务配置
 *
 * @author chenlong
 * @version 1.0 2018-08-17
 */
public class ServiceProperties {
    public static final String DEFAULT_HTTP = "http";
    public static final String DEFAULT_IP = NetUtil.getLocalIp();
    public static final int DEFAULT_PORT = 80;
    public static final String DEFAULT_PATH = "Elock_Service.asmx";
    public static final boolean DEFAULT_FORWARD = false;
    public static final long DEFAULT_LOG_OVERDUE = 60L;

    private static final Logger logger = LoggerFactory.getLogger(ServiceProperties.class);
    private static final String PROP_FILE_PATH = FilePathInit.getPropFilePath();
    // private static final String NAME_SERVICE_IP = "service.ip";
    private static final String NAME_SERVICE_PORT = "service.port";
    private static final String NAME_SERVICE_PATH = "service.path";
    private static final String NAME_RT_FORWARD = "ruitong.forward";
    private static final String NAME_RT_HTTP = "ruitong.http";
    private static final String NAME_RT_IP = "ruitong.ip";
    private static final String NAME_RT_PORT = "ruitong.port";
    private static final String NAME_RT_PATH = "ruitong.path";
    private static final String NAME_PLT_FORWARD = "pltone.forward";
    private static final String NAME_PLT_HTTP = "pltone.http";
    private static final String NAME_PLT_IP = "pltone.ip";
    private static final String NAME_PLT_PORT = "pltone.port";
    private static final String NAME_PLT_PATH = "pltone.path";
    private static final String NAME_LOG_OVERDUE = "log.overdue";

    private static Properties properties;

    // private static String serviceIp;
    private static int servicePort;
    private static String servicePath;

    private static boolean rtForward;
    private static String rtHttp;
    private static String rtIp;
    private static int rtPort;
    private static String rtPath;

    private static boolean pltForward;
    private static String pltHttp;
    private static String pltIp;
    private static int pltPort;
    private static String pltPath;

    private static long logOverdue;

    // public static String getServiceIp() {
    //     return serviceIp;
    // }
    //
    // public static void setServiceIp(String serviceIp) {
    //     ServiceProperties.serviceIp = serviceIp;
    //     properties.setProperty(NAME_SERVICE_IP, serviceIp);
    // }

    public static int getServicePort() {
        return servicePort;
    }

    public static void setServicePort(int servicePort) {
        ServiceProperties.servicePort = servicePort;
        properties.setProperty(NAME_SERVICE_PORT, Integer.toString(servicePort));
    }

    public static String getServicePath() {
        return servicePath;
    }

    public static void setServicePath(String servicePath) {
        ServiceProperties.servicePath = servicePath;
        properties.setProperty(NAME_SERVICE_PATH, servicePath);
    }

    public static boolean isRtForward() {
        return rtForward;
    }

    public static void setRtForward(boolean rtForward) {
        ServiceProperties.rtForward = rtForward;
        properties.setProperty(NAME_RT_FORWARD, rtForward ? "1" : "0");
    }

    public static String getRtHttp() {
        return rtHttp;
    }

    public static void setRtHttp(String rtHttp) {
        ServiceProperties.rtHttp = rtHttp;
        properties.setProperty(NAME_RT_HTTP, rtHttp);
    }

    public static String getRtIp() {
        return rtIp;
    }

    public static void setRtIp(String rtIp) {
        ServiceProperties.rtIp = rtIp;
        properties.setProperty(NAME_RT_IP, rtIp);
    }

    public static int getRtPort() {
        return rtPort;
    }

    public static void setRtPort(int rtPort) {
        ServiceProperties.rtPort = rtPort;
        properties.setProperty(NAME_RT_PORT, Integer.toString(rtPort));
    }

    public static String getRtPath() {
        return rtPath;
    }

    public static void setRtPath(String rtPath) {
        ServiceProperties.rtPath = rtPath;
        properties.setProperty(NAME_RT_PATH, rtPath);
    }

    public static boolean isPltForward() {
        return pltForward;
    }

    public static void setPltForward(boolean pltForward) {
        ServiceProperties.pltForward = pltForward;
        properties.setProperty(NAME_PLT_FORWARD, pltForward ? "1" : "0");
    }

    public static String getPltHttp() {
        return pltHttp;
    }

    public static void setPltHttp(String pltHttp) {
        ServiceProperties.pltHttp = pltHttp;
        properties.setProperty(NAME_PLT_HTTP, pltHttp);
    }

    public static String getPltIp() {
        return pltIp;
    }

    public static void setPltIp(String pltIp) {
        ServiceProperties.pltIp = pltIp;
        properties.setProperty(NAME_PLT_IP, pltIp);
    }

    public static int getPltPort() {
        return pltPort;
    }

    public static void setPltPort(int pltPort) {
        ServiceProperties.pltPort = pltPort;
        properties.setProperty(NAME_PLT_PORT, Integer.toString(pltPort));
    }

    public static String getPltPath() {
        return pltPath;
    }

    public static void setPltPath(String pltPath) {
        ServiceProperties.pltPath = pltPath;
        properties.setProperty(NAME_PLT_PATH, pltPath);
    }

    public static long getLogOverdue() {
        return logOverdue;
    }

    public static void setLogOverdue(long logOverdue) {
        ServiceProperties.logOverdue = logOverdue;
        properties.setProperty(NAME_LOG_OVERDUE, Long.toString(logOverdue));
    }

    public static void init() {
        try {
            properties = new Properties();
            properties.load(new FileInputStream(PROP_FILE_PATH));

            // serviceIp = properties.getProperty(NAME_SERVICE_IP, DEFAULT_IP);
            servicePort = FileUtil.getIntProp(properties, NAME_SERVICE_PORT, DEFAULT_PORT);
            servicePath = properties.getProperty(NAME_SERVICE_PATH, DEFAULT_PATH);

            rtForward = FileUtil.getBoolProp(properties, NAME_RT_FORWARD, DEFAULT_FORWARD);
            rtHttp = properties.getProperty(NAME_RT_HTTP, DEFAULT_HTTP);
            rtIp = properties.getProperty(NAME_RT_IP, DEFAULT_IP);
            rtPort = FileUtil.getIntProp(properties, NAME_RT_PORT, DEFAULT_PORT);
            rtPath = properties.getProperty(NAME_RT_PATH, DEFAULT_PATH);

            pltForward = FileUtil.getBoolProp(properties, NAME_PLT_FORWARD, DEFAULT_FORWARD);
            pltHttp = properties.getProperty(NAME_PLT_HTTP, DEFAULT_HTTP);
            pltIp = properties.getProperty(NAME_PLT_IP, DEFAULT_IP);
            pltPort = FileUtil.getIntProp(properties, NAME_PLT_PORT, DEFAULT_PORT);
            pltPath = properties.getProperty(NAME_PLT_PATH, DEFAULT_PATH);

            logOverdue = FileUtil.getLongProp(properties, NAME_LOG_OVERDUE, DEFAULT_LOG_OVERDUE);
        } catch (IOException e) {
            logger.error("初始化配置异常！", e);
        }
    }

    public static void save() {
        try (OutputStream out = new FileOutputStream(PROP_FILE_PATH)) {
            properties.store(out, null);
        } catch (Exception e) {
            logger.error("更新配置文件异常！", e);
        }
    }
}