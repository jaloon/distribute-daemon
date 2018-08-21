package com.pltone.daemon;

import com.pltone.Main;

/**
 * Linux服务安装入口类
 * <p>
 * 入口类只需要实现init,destroy,start,stop方法即可，安装时指定此类，jsvc会根据这些方法进行初始化、启动、关闭等操作。<br>
 * init方法有参数，destroy、start及stop方法无参。<br>
 * start方法作启动入口，stop方法为关闭
 * </p>
 *
 * @author chenlong
 * @version 1.0 2018-08-20
 */
public class LinuxDaemon {

    public static void init(String[] args) {
        Main.init();
    }

    public static void destroy() {
        stop();
    }

    public static void start() {
        Main.start();
    }

    public static void stop() {
        Main.stop();
        System.exit(0);
    }
}
