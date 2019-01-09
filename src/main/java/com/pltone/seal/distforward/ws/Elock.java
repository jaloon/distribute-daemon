package com.pltone.seal.distforward.ws;

import com.pltone.seal.distforward.bean.ForwardXmlState;
import com.pltone.seal.distforward.cache.ForwardStateCache;
import com.pltone.seal.distforward.dao.DistXmlDao;
import com.pltone.seal.distforward.util.ThreadUtil;
import com.pltone.seal.distforward.ws.client.ElockClient;
import com.pltone.seal.distforward.ws.constant.DistXmlNodeNameConst;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * 物流配送WebService服务
 *
 * @author chenlong
 * @version 1.0 2018-01-19
 */
@WebService(name = "ElockSoap", serviceName = "Elock", portName = "ElockSoap", targetNamespace = "http://www.cnpc.com/")
@MTOM
public class Elock {
    private static final Logger logger = LoggerFactory.getLogger(Elock.class);
    private static final int FORWARD_REPEAT_MAX = 5;
    private static final String REPLY_MESSAGE_SUCCESS = "<Message>success</Message>";
    private static final String HEADER = "<TradeData>";
    private static final String FOOTER = "</TradeData>";
    /** 转发失败重发超时1分钟 */
    private static final long FORWARD_TIMEOUT = 60000L;
    /** 转发状态：0 未转发（默认） */
    public static final int FORWARD_STATE_NONE = 0;
    /** 转发状态：1 转发成功 */
    private static final int FORWARD_STATE_DONE = 1;
    /** 转发状态：2 转发失败 */
    private static final int FORWARD_STATE_FAIL = 2;
    private boolean forwardRt;
    private boolean forwardPlt;
    private String rtWsAddr;
    private String pltWsAddr;
    private DistXmlDao distXmlDao;
    private ExecutorService cachedThreadPool;

    public Elock() {
        distXmlDao = DistXmlDao.getInstance();
        cachedThreadPool = ThreadUtil.newThreadPool(4, 64, "Forward-Pool-");
    }

    public final boolean isForwardRt() {
        return forwardRt;
    }

    final void setForwardRt(boolean forwardRt) {
        this.forwardRt = forwardRt;
    }

    public final boolean isForwardPlt() {
        return forwardPlt;
    }

    final void setForwardPlt(boolean forwardPlt) {
        this.forwardPlt = forwardPlt;
    }

    public final String getRtWsAddr() {
        return rtWsAddr;
    }

    final void setRtWsAddr(String rtWsAddr) {
        this.rtWsAddr = rtWsAddr;
    }

    public final String getPltWsAddr() {
        return pltWsAddr;
    }

    final void setPltWsAddr(String pltWsAddr) {
        this.pltWsAddr = pltWsAddr;
    }

    @WebMethod(operationName = "SetPlan")
    @WebResult(name = "SetPlanResult", targetNamespace = "http://www.cnpc.com/")
    public String setPlan(@WebParam(name = "txt", targetNamespace = "http://www.cnpc.com/") String txt) {
        logger.debug("WebService收到物流配送报文：\n{}", txt);
        if (txt == null) {
            logger.warn("XML报文为null！");
            return "<Message>fail：XML报文为null！</Message>";
        }
        if (txt.trim().isEmpty()) {
            logger.warn("XML报文为空字符串！");
            return "<Message>fail：XML报文为空字符串！</Message>";
        }
        int beginIndex = txt.indexOf(HEADER);
        int footerIndex = txt.indexOf(FOOTER);
        int endIndex = footerIndex + FOOTER.length();
        if (beginIndex < 0 || beginIndex >= footerIndex) {
            logger.warn("XML报文未包含规范格式的配送信息！");
            return "<Message>fail：XML报文未包含规范格式的配送信息！</Message>";
        }
        String distributeXmlStr = txt.substring(beginIndex, endIndex);
        // 读取XML文本内容获取Document对象
        try {
            Document document = DocumentHelper.parseText(distributeXmlStr);
            Element root = document.getRootElement();
            Element messageNode = root.element(DistXmlNodeNameConst.NODE_1_2_MESSAGE);
            if (messageNode == null) {
                logger.warn("配送信息XML文本未包含<Message>标签！");
                return "<Message>fail：配送信息XML文本未包含“<Message>”标签！</Message>";
            }
            Element dlistsNode = messageNode.element(DistXmlNodeNameConst.NODE_2_DLISTS);
            if (dlistsNode == null) {
                logger.warn("配送信息XML文本未包含<dlists>标签！");
                return "<Message>fail：配送信息XML文本未包含“<dlists>”标签！</Message>";
            }
            List<Element> elements = dlistsNode.elements();
            if (elements.size() > 0) {
                for (Element element : elements) {
                    String dlistNodeName = element.getName();
                    if (!DistXmlNodeNameConst.NODE_3_DLIST.equals(dlistNodeName)) {
                        logger.warn("配送信息XML文本<dlist>节点名称不匹配！{}", dlistNodeName);
                        return "<Message>fail：配送信息XML文本<dlist>节点名称不匹配！</Message>";
                    }
                    for (String node : DistXmlNodeNameConst.DLIST_CHILD_NODES) {
                        if (element.element(node) == null) {
                            logger.warn("配送信息XML节点不全，缺少<{}>标签！", node);
                            return "<Message>fail：配送信息XML节点不全，缺少“<" + node + ">”标签！</Message>";
                        }
                    }
                    for (String node : DistXmlNodeNameConst.USEFUL_DIST_NODES) {
                        if (element.element(node).getText().trim().isEmpty()) {
                            logger.warn("配送信息XML节点数据缺失，<{}>标签无数据！", node);
                            return "<Message>fail：配送信息XML节点数据缺失，“<" + node + ">”标签无数据！</Message>";
                        }
                    }
                }
                long id;
                try {
                    id = distXmlDao.add(txt);
                } catch (Exception e) {
                    logger.error("数据库存储配送信息异常！\n{}", e.getMessage());
                    return "<Message>fail：配送信息存储异常</Message>";
                }
                ForwardXmlState forwardXmlState = new ForwardXmlState();
                forwardXmlState.setId(id);
                forwardXmlState.setXml(txt);
                if (id > 0) {
                    // 转发配送信息
                    forwardToRt(forwardXmlState);
                    forwardToPlt(forwardXmlState);
                }
                logger.info("接收配送单[{}]成功！", id);
                return "<Message>success</Message>";
            } else {
                logger.warn("配送信息XML文本未包含<dlists>标签！");
                return "<Message>fail：具体配送信息为空！</Message>";
            }
        } catch (Exception e) {
            logger.error("接收配单失败: {}", e.getMessage());
            return "<Message>fail：" + e.getMessage() + "</Message>";
        }
    }

    /**
     * 转发配送信息到瑞通系统WebService服务器
     *
     * @param forwardXmlState 转发报文状态信息
     */
    public final void forwardToRt(ForwardXmlState forwardXmlState) {
        if (forwardRt) {
            forward(forwardXmlState, rtWsAddr, "瑞通", distXmlDao::updateRtForwardState);
        }
    }

    /**
     * 转发配送信息到普利通系统WebService服务器
     *
     * @param forwardXmlState 转发报文状态信息
     */
    public final void forwardToPlt(ForwardXmlState forwardXmlState) {
        if (forwardPlt) {
            forward(forwardXmlState, pltWsAddr, "普利通", distXmlDao::updatePltForwardState);
        }
    }

    /**
     * 转发
     *
     * @param forwardXmlState 转发报文状态信息
     * @param wsAddr          转发到的WebService服务器地址
     * @param tips            提示信息
     * @param consumer        数据库操作函数
     */
    private void forward(ForwardXmlState forwardXmlState, String wsAddr, String tips, Consumer<ForwardXmlState> consumer) {
        cachedThreadPool.execute(() -> {
            long id = forwardXmlState.getId();
            int repeatCount = 0;
            while (repeatCount < FORWARD_REPEAT_MAX) {
                String rtReply = ElockClient.setPlan(wsAddr, forwardXmlState.getXml());
                if (rtReply.equals(REPLY_MESSAGE_SUCCESS)) { // 转发成功
                    forwardXmlState.setState(FORWARD_STATE_DONE);
                    logger.info(
                            new StringBuilder()
                                    .append("配送信息[")
                                    .append(id)
                                    .append("]转发")
                                    .append(tips)
                                    .append("服务器成功！").toString()
                    );
                    try {
                        consumer.accept(forwardXmlState);
                    } catch (Exception e) {
                        ForwardStateCache.getInstance().put(id, forwardXmlState);
                        logger.error(
                                new StringBuilder()
                                        .append("更新数据库（配送信息[")
                                        .append(id)
                                        .append("]转发")
                                        .append(tips)
                                        .append("成功）异常：")
                                        .append(e.getMessage())
                                        .toString()
                        );
                    }
                    return;
                }
                logger.error(
                        new StringBuilder()
                                .append('第')
                                .append(++repeatCount)
                                .append("次转发配送信息[")
                                .append(id)
                                .append("]到")
                                .append(tips)
                                .append("服务器回复失败：")
                                .append(rtReply)
                                .toString()
                );
                try {
                    Thread.sleep(FORWARD_TIMEOUT);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            // 转发失败
            forwardXmlState.setState(FORWARD_STATE_FAIL);
            logger.warn(
                    new StringBuilder()
                            .append("配送信息[")
                            .append(id)
                            .append("]转发")
                            .append(tips)
                            .append("服务器失败！").toString()
            );
            try {
                consumer.accept(forwardXmlState);
            } catch (Exception e) {
                ForwardStateCache.getInstance().put(id, forwardXmlState);
                logger.error(
                        new StringBuilder()
                                .append("更新数据库（配送信息[")
                                .append(id)
                                .append("]转发")
                                .append(tips)
                                .append("失败）异常：")
                                .append(e.getMessage())
                                .toString()
                );
            }
        });
    }

    /**
     * 关闭线程池
     */
    final void closeThreadPool() {
        if (cachedThreadPool != null) {
            cachedThreadPool.shutdown();
        }
    }
}