package com.pltone.seal.distforward.ws.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * ElockClient
 *
 * @author chenlong
 * @version 1.0 2018-01-19
 */
public class ElockClient {
    private static final Logger logger = LoggerFactory.getLogger(ElockClient.class);

    /**
     * 推送物流信息
     *
     * @param wsAddr  {@link String} WebService服务器地址
     * @param distXml {@link String}物流信息XML文本
     * @return
     */
    public static String setPlan(String wsAddr, String distXml) {
        try {
            String wsdl = new StringBuffer(wsAddr).append("?wsdl").toString();
            // 创建WSDL的URL，注意不是服务地址
            URL url = new URL(wsdl);
            // 创建服务名称
            // 1.namespaceURI - 命名空间地址 (wsdl文档中的targetNamespace)
            // 2.localPart - 服务视图名 (wsdl文档中服务名称，例如<wsdl:service name="Elock">)
            QName qname = new QName("http://www.cnpc.com/", "Elock");
            // 创建服务视图
            // 参数解释：
            // 1.wsdlDocumentLocation - wsdl地址
            // 2.serviceName - 服务名称
            Service service = Service.create(url, qname);
            // 获取服务实现类
            // 参数解释:serviceEndpointInterface - 服务端口(wsdl文档中服务端口的name属性，
            // 例如<wsdl:port name="ElockSoap" binding="tns:ElockSoap">)
            ElockSoap elockSoap = service.getPort(ElockSoap.class);
            // 调用查询方法
            return elockSoap.setPlan(distXml);
        } catch (MalformedURLException e) {
            logger.error("创建WSDL的URL异常！\n{}", e.getMessage());
            return "<Message>fail：创建WSDL的URL异常！</Message>";
        } catch (WebServiceException e) {
            logger.error("连接WebService服务器异常！\n{}", e.getMessage());
            return "<Message>fail：连接WebService服务器异常！</Message>";
        }
    }

    private ElockClient() {}
}