package com.pltone.daemon;

import com.pltone.Main;

/**
 * Windows服务安装入口类
 * <p>
 * 跟linux下的入口类不同，入口类只需要实现start、stop方法，安装时指定此类，procrun会根据这些方法进启动、关闭操作。<br>
 * <strong>注意：</strong>与linux的不同，此两个方法需要带参数，否则会报找不到start方法错误。
 * </p>
 *
 * @author chenlong
 * @version 1.0 2018-08-20
 */
public class WindowsDaemon {
    public static void start(String[] args) {
        Main.init();
        Main.start();
    }

    public static void stop(String[] args) {
        Main.stop();
        System.exit(0);
    }
}
