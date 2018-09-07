package com.pltone.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * jar工具类
 *
 * @author chenlong
 * @version 1.0 2018-08-17
 */
public class JarUtil {
    /**
     * 获取可执行jar包目录
     *
     * @return {@link String} jar包目录
     */
    public static String getExecutableJarDir() {
        return System.getProperty("user.dir");
    }

    /**
     * 获取类文件所属jar包目录
     *
     * @param clazz {@link Class} 类
     * @return {@link String} jar包目录
     * @throws IOException
     */
    public static String getJarDirOfClass(Class<?> clazz) throws IOException {
        String jarPath = getJarPathOfClass(clazz);
        if (jarPath == null || jarPath.trim().isEmpty()) {
            return null;
        }
        // 转换处理中文及空格
        jarPath = URLEncoder.encode(jarPath, "utf-8");
        File jarFile = new File(jarPath);
        return jarFile.getParent();
    }

    /**
     * 获取类文件所属jar包路径
     *
     * @param clazz {@link Class} 类
     * @return {@link String} jar包路径
     */
    public static String getJarPathOfClass(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + File.separator + "distribute-2.1.0.jar";
        File file = new File(path);
        System.out.println(file.exists());
        String name = "test";
        String content = "这是测试数据qqdwqdew";
        try {
            writeFileToJar(path, name, content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改jar包里的文件或者添加文件
     *
     * @param jarPath  {@link String} jar包路径
     * @param fileName {@link String} 文件名称
     * @param fileData {@link byte[]} 文件数据
     * @throws IOException
     */
    public static void writeFileToJar(String jarPath, String fileName, byte[] fileData) throws IOException {
        JarFile jarFile = null;
        // 保持排列的顺序，所以用TreeMap，而不用HashMap
        TreeMap<String, byte[]> tm = new TreeMap<>();
        try {
            // 将原Jar包里的所有内容读取到内存里
            jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement();
                tm.put(jarEntry.getName(), inputStreamToByteArray(jarFile.getInputStream(jarEntry)));
            }
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    // no exception expect
                }
            }
        }
        JarOutputStream jarOut = null;
        try {
            jarOut = new JarOutputStream(new FileOutputStream(jarPath));
            Iterator<Map.Entry<String, byte[]>> iterator = tm.entrySet().iterator();
            boolean add = true;
            // 将TreeMap重新写到原jar里，如果TreeMap里已经有entryName文件那么覆盖，否则在最后添加
            while (iterator.hasNext()) {
                Map.Entry<String, byte[]> entry = iterator.next();
                String name = entry.getKey();
                JarEntry jarEntry = new JarEntry(name);
                jarOut.putNextEntry(jarEntry);
                if (jarEntry.getName().equals(fileName)) {
                    // 覆盖
                    add = false;
                    jarOut.write(fileData, 0, fileData.length);
                    continue;
                }
                byte[] bytes = entry.getValue();
                jarOut.write(bytes, 0, bytes.length);
            }
            if (add) {
                // 添加
                JarEntry newJarEntry = new JarEntry(fileName);
                jarOut.putNextEntry(newJarEntry);
                jarOut.write(fileData, 0, fileData.length);
            }
            jarOut.finish();
        } finally {
            if (jarOut != null) {
                try {
                    jarOut.close();
                } catch (IOException e) {
                    // no exception expect
                }
            }
        }
    }

    /**
     * 将输入流转为字节数组
     *
     * @param input {@link InputStream} 输入流
     * @return {@link byte[]} 字节数组
     * @throws IOException
     */
    public static byte[] inputStreamToByteArray(InputStream input) throws IOException {
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            return out.toByteArray();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // no exception expect
                }
            }
            try {
                input.close();
            } catch (IOException e) {
                // no exception expect
            }
        }
    }
}