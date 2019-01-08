package com.pltone.seal.distforward.util;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 文件操作工具类
 *
 * @author chenlong
 * @version 1.0 2018-10-10
 */
public class FileUtil {
    /**
     * 获取整形属性值
     *
     * @param properties   {@link Properties}
     * @param key          属性名称
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static int getIntProp(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value, 10);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取长整形属性值
     *
     * @param properties   {@link Properties}
     * @param key          属性名称
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static long getLongProp(Properties properties, String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value, 10);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取布尔型属性值
     *
     * @param properties   {@link Properties}
     * @param key          属性名称
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static boolean getBoolProp(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.equals("1")
                || value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("yes");
    }

    /**
     * 删除指定文件夹下指定日期前的文件
     *
     * @param folderPath 文件夹路径
     * @param duration   日期间隔
     * @param timeUnit   日期单位
     * @return 删除的文件
     * @throws IllegalArgumentException if path is file not folder.
     */
    public static File[] deleteFilesBeforeDateInFolder(String folderPath, long duration, TimeUnit timeUnit) {
        File folder = new File(folderPath);
        if (folder.isFile()) throw new IllegalArgumentException("给定的路径[" + folderPath + "]是文件而非文件夹！");
        long durationMillis = timeUnit.toMillis(duration);
        long beforeMillis = System.currentTimeMillis() - durationMillis;
        return folder.listFiles(file -> {
            long lastModified = file.lastModified();
            boolean flag = lastModified < beforeMillis;
            if (flag) file.delete();
            return flag;
        });
    }
}
