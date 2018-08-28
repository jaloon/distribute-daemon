package com.pltone.ws;

import com.pltone.cnf.ServiceProperties;
import com.pltone.job.ForwordSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Endpoint;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 服务启动类
 *
 * @author chenlong
 * @version 1.0 2018-08-20
 */
public enum ElockServer {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(ElockServer.class);
    private Elock elock;
    private Endpoint endpoint;
    private ScheduledExecutorService service;
    private boolean serviceStart;

    /**
     * 启动服务
     */
    public void startService() {
        logger.info("WebService start...");
        String publishAddr = new StringBuffer().append("http://0.0.0.0:").append(ServiceProperties.getServicePort())
                .append('/').append(ServiceProperties.getServicePath()).toString();
        String realAddr = new StringBuffer("http://").append(ServiceProperties.DEFAUT_IP).append(':')
                .append(ServiceProperties.getServicePort()).append('/')
                .append(ServiceProperties.getServicePath()).toString();
        String rtWsAddr = new StringBuffer().append(ServiceProperties.getRtHttp()).append("://")
                .append(ServiceProperties.getRtIp()).append(':').append(ServiceProperties.getRtPort())
                .append('/').append(ServiceProperties.getRtPath()).toString();
        String pltWsAddr = new StringBuffer().append(ServiceProperties.getPltHttp()).append("://")
                .append(ServiceProperties.getPltIp()).append(':').append(ServiceProperties.getPltPort())
                .append('/').append(ServiceProperties.getPltPath()).toString();

        elock = new Elock();
        elock.setForwordRt(ServiceProperties.isRtForward());
        elock.setForwordPlt(ServiceProperties.isPltForward());
        elock.setRtWsAddr(rtWsAddr);
        elock.setPltWsAddr(pltWsAddr);

        // 启动服务
        serviceStart = startService(publishAddr);
        if (!serviceStart) {
           return;
        }
        logger.info("转发服务器地址: {}", realAddr);
        if (ServiceProperties.isRtForward()) {
            logger.info("瑞通接口地址: {}", rtWsAddr);
        }
        if (ServiceProperties.isPltForward()) {
            logger.info("普利通接口地址: {}", pltWsAddr);
        }
        logger.info("WebService started.");
    }

    /**
     * 启动服务
     *
     * @param address {@link String} WebSevice发布地址
     * @return {@link Boolean} 启动成功与否
     */
    private boolean startService(String address) {
        try {
            endpoint = Endpoint.publish(address, elock);
            service = ForwordSchedule.executeForwordSchedule(elock);
            return true;
        } catch (Exception e) {
            logger.error("启动服务异常：\n{}", e.toString());
            return false;
        }
    }

    /**
     * 关闭服务
     */
    public void stopService() {
        if (serviceStart) {
            try {
                endpoint.stop();
                service.shutdown();
                elock.closeThreadPool();
                logger.info("WebService stopped.");
            } catch (Exception e) {
                logger.error("关闭服务异常：\n{}", e.toString());
            }
        }
    }
}