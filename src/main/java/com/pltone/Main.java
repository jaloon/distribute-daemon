package com.pltone;

import com.pltone.cnf.ServiceProperties;
import com.pltone.init.FilePathInit;
import com.pltone.init.SqliteDbInit;
import com.pltone.log.LogConfig;
import com.pltone.ws.ElockServer;

/**
 * 启动器
 * 
 * @author chenlong
 * @version 1.0 2018-02-12
 */
public class Main {

	public static void main(String[] args) {
	    init();
	    start();
	}

    /**
     * 初始化程序
     */
	public static void init() {
        LogConfig.initLog4j();
        FilePathInit.init();
        // LogConfig.addDailyFileAppender();
        ServiceProperties.init();
        SqliteDbInit.init();
    }

    /**
     * 启动程序
     */
	public static void start() {
        ElockServer.INSTANCE.startService();
    }

    /**
     * 关闭程序
     */
    public static void stop() {
	    ElockServer.INSTANCE.stopService();
    }

}
