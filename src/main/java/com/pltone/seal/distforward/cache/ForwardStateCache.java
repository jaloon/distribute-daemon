package com.pltone.seal.distforward.cache;

import com.pltone.cache.impl.TimedCache;
import com.pltone.seal.distforward.bean.ForwardXmlState;

/**
 * 转发状态缓存
 *
 * @author chenlong
 * @version 1.0 2019-01-08
 */
public class ForwardStateCache extends TimedCache<Long, ForwardXmlState> {
    /** 缓存有效时间 */
    private static long DEFAULT_TIME_OUT = 30 * 60 * 1000L;
    /** 缓存单例对象 */
    private static ForwardStateCache instance = new ForwardStateCache();

    /**
     * 获取缓存单例
     *
     * @return 缓存单例对象
     */
    public static ForwardStateCache getInstance() {
        return instance;
    }

    /**
     * 默认构造，缓存半小时，每隔一小时执行一次清理
     */
    private ForwardStateCache() {
        super(DEFAULT_TIME_OUT);
        this.schedulePrune(2 * DEFAULT_TIME_OUT);
    }
}
