package com.pltone.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 网络工具类
 *
 * @author chenlong
 * @version 1.0 2018-01-19
 */
public class NetUtil {
    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    private NetUtil() {
    }

    /**
     * 获取本机IP地址
     *
     * @return {@link InetAddress} 本机IP地址
     */
    public static InetAddress getLocalHostLANAddress() {
        InetAddress candidateAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            // 遍历所有的网络接口
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddrs = networkInterface.getInetAddresses();
                // 在所有的接口下再遍历IP
                while (inetAddrs.hasMoreElements()) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    // 排除loopback类型地址
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress == null) {
                // 如果没有发现 non-loopback地址.只能用最次选的方案 jdkSuppliedAddress
                candidateAddress = InetAddress.getLocalHost();
            }
        } catch (SocketException e) {
            logger.error("an I/O error occurs when get NetworkInterfaces! \n{}", e.getMessage());
        } catch (UnknownHostException e) {
            logger.error("the local host name could not be resolved into an address! \n{}", e.getMessage());
        }
        return candidateAddress;
    }

    /**
     * 获取MAC地址
     *
     * @param ia {@link InetAddress}
     * @return {@link String} MAC地址
     */
    public static String getMacAddress(InetAddress ia) {
        try {
            // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            // 下面代码是把mac地址拼装成String
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                // mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            // 把字符串所有小写字母改为大写成为正规的mac地址并返回
            return sb.toString().toUpperCase();
        } catch (SocketException e) {
            logger.error("an I/O error occurs when get HardwareAddress! \n{}", e.getMessage());
            return null;
        }
    }
}