package com.pltone.seal.distforward.job;

import com.pltone.seal.distforward.bean.ForwardXmlState;
import com.pltone.seal.distforward.cache.ForwardStateCache;
import com.pltone.seal.distforward.dao.DistXmlDao;
import com.pltone.seal.distforward.util.ThreadUtil;
import com.pltone.seal.distforward.ws.Elock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 定时转发任务
 *
 * @author chenlong
 * @version 1.0 2018-02-27
 */
public class ForwardSchedule {
    private static final Logger logger = LoggerFactory.getLogger(ForwardSchedule.class);

    /**
     * 执行转发定时任务
     *
     * @param elock {@link Elock}
     */
    public static ScheduledExecutorService executeForwardSchedule(Elock elock) {
        Runnable task = () -> {
            // task to run goes here
            List<ForwardXmlState> rtList = buildForwardXmlList(DistXmlDao.getInstance()::findRtForwardXmlList);
            List<ForwardXmlState> pltList = buildForwardXmlList(DistXmlDao.getInstance()::findPltForwardXmlList);
            if (elock.isForwardRt() && rtList.size() > 0) {
                logger.info("定时任务：转发失败的配送信息{}重发瑞通服务器",
                        rtList.stream().map(ForwardXmlState::getId).collect(Collectors.toList()));
                rtList.forEach(elock::forwardToRt);
            }
            if (elock.isForwardPlt() && pltList.size() > 0) {
                logger.info("定时任务：转发失败的配送信息{}重发普利通服务器",
                        pltList.stream().map(ForwardXmlState::getId).collect(Collectors.toList()));
                pltList.forEach(elock::forwardToPlt);
            }
        };
        // ScheduledExecutorService是从Java SE5开始在java.util.concurrent包里做为并发工具类被引进的，这是最理想的定时任务实现方式。
        // 相比于使用java.util.Timer和线程等待来实现定时任务，它有以下好处：
        // 1>相比于Timer的单线程，它是通过线程池的方式来执行任务的
        // 2>可以很灵活的去设定第一次执行任务delay时间
        // 3>提供了良好的约定，以便设定执行的时间间隔
        ScheduledExecutorService service = Executors.newScheduledThreadPool(4,
                ThreadUtil.newThreadFactory("Resend-Schedule-"));
        // 参数：1、任务体 2、首次执行的延时时间 3、任务执行间隔 4、间隔时间单位
        service.scheduleAtFixedRate(task, 10, 10, TimeUnit.MINUTES);
        return service;
    }

    /**
     * 构建重发报文列表
     *
     * @param supplier 重发报文查询方法
     * @return 报文列表
     */
    private static List<ForwardXmlState> buildForwardXmlList(Supplier<List<ForwardXmlState>> supplier) {
        List<ForwardXmlState> list;
        try {
            list = supplier.get();
        } catch (Exception e) {
            list = new ArrayList<>();
            logger.error("查询需要重发的配送报文异常！", e);
        }
        ForwardStateCache forwardStateCache = ForwardStateCache.getInstance();
        forwardStateCache.prune();
        List<ForwardXmlState> cacheList = forwardStateCache.values();
        if (cacheList.size() > 0) {
            List<ForwardXmlState> rmList = cacheList.stream()
                    .filter(ForwardXmlState::isDone).collect(Collectors.toList());
            if (rmList.size() > 0) {
                list.removeIf(forwardXmlState -> {
                    for (ForwardXmlState rm : rmList) {
                        if (forwardXmlState.getId() == rm.getId()) {
                            return true;
                        }
                    }
                    return false;
                });
            }
            List<ForwardXmlState> addList = cacheList.stream()
                    .filter(ForwardXmlState::isFail).collect(Collectors.toList());
            if (addList.size() > 0) {
                list.addAll(addList);
            }
        }
        return list;
    }
}