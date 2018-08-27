package com.pltone.util;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

/**
 * @author chenlong
 * @version 1.0 2018-01-19
 */
public class XmlUtil {

    private XmlUtil() {
    }

    /**
     * 创建Document对象
     *
     * @return
     */
    public static Document createDocument() {
        return DocumentHelper.createDocument();
    }

    /**
     * 读取XML文本内容获取Document对象
     *
     * @param xmlStr XML文本
     * @return Document对象
     * @throws DocumentException
     */
    public static Document getDocumentByXmlStr(String xmlStr) throws DocumentException {
        return DocumentHelper.parseText(xmlStr);
    }

    /**
     * 读取XML文件获取Document对象
     *
     * @param xmlFlie XML文件
     * @return Document对象
     * @throws DocumentException
     */
    public static Document getDocumentByXmlFile(File xmlFlie) throws DocumentException {
        // 创建SAXReader对象
        SAXReader reader = new SAXReader();
        // 读取文件，转换成Document
        return reader.read(xmlFlie);
    }

    /**
     * 获取根节点
     *
     * @param document Document对象
     * @return
     */
    public static Element getRootNode(Document document) {
        return document.getRootElement();
    }

    // 遍历当前节点下的所有节点
    @SuppressWarnings("unchecked")
    public void listNodes(Element node) {
        // 首先获取当前节点的所有属性节点
        List<Attribute> list = node.attributes();
        // 遍历属性节点
        for (Attribute attribute : list) {
            System.out.println("属性" + attribute.getName() + ":" + attribute.getValue());
        }
        // 如果当前节点内容不为空，则输出
        if (!(node.getTextTrim().equals(""))) {
            System.out.println(node.getName() + "：" + node.getText());
        }
        // 同时迭代当前节点下面的所有子节点
        // 使用递归
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e);
        }
    }

    /**
     * document写入新的文件
     *
     * @param document       Document对象
     * @param filePath       要写入的文件路径全名
     * @param isFormatOutput 是否对输出文档进行排版格式化，<code>true</code>格式化输出，<code>false</code>紧凑型输出
     * @throws IOException
     */
    public static void writerDocumentToNewFile(Document document, String filePath, boolean isFormatOutput) throws IOException {
        // 输出格式
        OutputFormat format = null;
        if (isFormatOutput) {
            // 输出文档时进行排版格式化
            format = OutputFormat.createPrettyPrint();
        } else {
            // 输出内容是一行，不进行格式化，是紧凑型的输出
            format = OutputFormat.createCompactFormat();
        }
        // 设置编码
        format.setEncoding("UTF-8");
        // XMLWriter 指定输出文件以及格式
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath)), "UTF-8"), format);
            // 写入新文件
            writer.write(document);
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}