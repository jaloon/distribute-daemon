package com.pltone.ws;

import com.pltone.init.SqliteDbInit;
import com.pltone.util.JdbcUtil;
import com.pltone.ws.client.ElockClient;
import com.pltone.ws.constant.DistXmlNodeNameConst;
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
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	private static final JdbcUtil JDBC_UTIL = new JdbcUtil();
	private static final int FORWORD_REPEAT_MAX = 5;
	private static final String REPLY_MESSAGE_SUCCESS = "<Message>success</Message>";
	private static final String HEADER = "<TradeData>";
	private static final String FOOTER = "</TradeData>";
	/** 转发失败重发超时1分钟 */
	private static final long FORWORD_TIMEOUT = 60000L;
	/** 转发状态：0 未转发（默认） */
	public static final int FORWORD_STATE_NONE = 0;
	/** 转发状态：1 转发成功 */
	public static final int FORWORD_STATE_DONE = 1;
	/** 转发状态：2 转发失败 */
	public static final int FORWORD_STATE_FAIL = 2;
	private boolean forwordRt;
	private boolean forwordPlt;
	private String rtWsAddr;
	private String pltWsAddr;
	private ExecutorService cachedThreadPool;

	public boolean isForwordRt() {
		return forwordRt;
	}

	public void setForwordRt(boolean forwordRt) {
		this.forwordRt = forwordRt;
	}

	public boolean isForwordPlt() {
		return forwordPlt;
	}

	public void setForwordPlt(boolean forwordPlt) {
		this.forwordPlt = forwordPlt;
	}

	public String getRtWsAddr() {
		return rtWsAddr;
	}

	public void setRtWsAddr(String rtWsAddr) {
		this.rtWsAddr = rtWsAddr;
	}

	public String getPltWsAddr() {
		return pltWsAddr;
	}

	public void setPltWsAddr(String pltWsAddr) {
		this.pltWsAddr = pltWsAddr;
	}

	@SuppressWarnings("unchecked")
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
			if (cachedThreadPool == null) {
				cachedThreadPool = Executors.newCachedThreadPool();
			}
			List<Element> elements = dlistsNode.elements();
			if (elements.size() > 0) {
				for (Element element : elements) {
					String dlistNodeName =  element.getName();
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
				JDBC_UTIL.createSqliteConnection(SqliteDbInit.SQLITE_DB_NAME);
				String sql = "INSERT INTO tbl_distribute(xml) values(?)";
				JDBC_UTIL.createPrepareStatement(sql);
				int id = 0;
				try {
					JDBC_UTIL.setString(1, txt);
					JDBC_UTIL.executeUpdate();
					id = JDBC_UTIL.getGeneratedKey(1);
					JDBC_UTIL.commit();
				} catch (SQLException e) {
					logger.error("数据库存储配送信息异常！\n{}", e.getMessage());
					JDBC_UTIL.rollback();
					JDBC_UTIL.close();
					return "<Message>fail：配送信息存储异常</Message>";
				}
				JDBC_UTIL.close();
				if (id > 0) {
					// 转发配送信息
					forwordToRt(txt, id);
					forwordToPlt(txt, id);
				}
				logger.info("接收配单成功！");
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
	 * @param distributeXmlStr
	 *            {@link String} 配送信息XML文本
	 * @param id
	 *            {@link int} 数据库记录ID
	 */
	public void forwordToRt(String distributeXmlStr, int id) {
		if (forwordRt) {
			cachedThreadPool.execute(() -> {
				JdbcUtil jdbcUtil = new JdbcUtil();
				jdbcUtil.createSqliteConnection(SqliteDbInit.SQLITE_DB_NAME);
				int repeatCount = 0;
				while (repeatCount < FORWORD_REPEAT_MAX) {
					String rtReply = ElockClient.setPlan(rtWsAddr, distributeXmlStr);
					if (rtReply.equals(REPLY_MESSAGE_SUCCESS)) {
						// 插入数据库 转发成功
						String sql = "UPDATE tbl_distribute SET rt = ? WHERE id = ?";
						jdbcUtil.createPrepareStatement(sql);
						try {
							jdbcUtil.setInt(1, FORWORD_STATE_DONE);
							jdbcUtil.setInt(2, id);
							jdbcUtil.executeUpdate();
							jdbcUtil.commit();
						} catch (SQLException e) {
							logger.error("更新数据库：配送信息[{}]转发瑞通成功。异常！\n{}", id, e.getMessage());
							jdbcUtil.rollback();
						}
						logger.info("配送信息[{}]转发瑞通服务器成功！", id);
						return;
					}
					logger.error("第{}次转发配送信息[{}]到瑞通服务器回复失败：{}", ++repeatCount, id, rtReply);
					try {
						Thread.sleep(FORWORD_TIMEOUT);
					} catch (InterruptedException e) {
						logger.error(e.toString());
					}
				}
				// 插入数据库 转发失败
				String sql = "UPDATE tbl_distribute SET rt = ? WHERE id = ?";
				jdbcUtil.createPrepareStatement(sql);
				try {
					jdbcUtil.setInt(1, FORWORD_STATE_FAIL);
					jdbcUtil.setInt(2, id);
					jdbcUtil.executeUpdate();
					jdbcUtil.commit();
				} catch (SQLException e) {
					logger.error("更新数据库：配送信息[{}]转发瑞通失败。异常！\n{}", id, e.getMessage());
					jdbcUtil.rollback();
				}
				jdbcUtil.close();
				logger.error("配送信息[{}]转发瑞通服务器失败！", id);
			});
		}
	}

	/**
	 * 转发配送信息到普利通系统WebService服务器
	 * 
	 * @param distributeXmlStr
	 *            {@link String} 配送信息XML文本
	 * @param id
	 *            {@link int} 数据库记录ID
	 */
	public void forwordToPlt(String distributeXmlStr, int id) {
		if (forwordPlt) {
			cachedThreadPool.execute(() -> {
				JdbcUtil jdbcUtil = new JdbcUtil();
				jdbcUtil.createSqliteConnection(SqliteDbInit.SQLITE_DB_NAME);
				int repeatCount = 0;
				while (repeatCount < FORWORD_REPEAT_MAX) {
					String rtReply = ElockClient.setPlan(pltWsAddr, distributeXmlStr);
					if (rtReply.equals(REPLY_MESSAGE_SUCCESS)) {
						// 插入数据库 转发成功
						String sql = "UPDATE tbl_distribute SET plt = ? WHERE id = ?";
						jdbcUtil.createPrepareStatement(sql);
						try {
							jdbcUtil.setInt(1, FORWORD_STATE_DONE);
							jdbcUtil.setInt(2, id);
							jdbcUtil.executeUpdate();
							jdbcUtil.commit();
						} catch (SQLException e) {
							logger.error("更新数据库：配送信息[{}]转发普利通成功。异常！\n{}", id, e.getMessage());
							jdbcUtil.rollback();
						}
						logger.info("配送信息[{}]转发普利通服务器成功！", id);
						return;
					}
					logger.error("第{}次转发配送信息[{}]到普利通服务器回复失败：{}", ++repeatCount, id, rtReply);
					try {
						Thread.sleep(FORWORD_TIMEOUT);
					} catch (InterruptedException e) {
						logger.error(e.toString());
					}
				}
				// 插入数据库 转发失败
				String sql = "UPDATE tbl_distribute SET plt = ? WHERE id = ?";
				jdbcUtil.createPrepareStatement(sql);
				try {
					jdbcUtil.setInt(1, FORWORD_STATE_FAIL);
					jdbcUtil.setInt(2, id);
					jdbcUtil.executeUpdate();
					jdbcUtil.commit();
				} catch (SQLException e) {
					logger.error("更新数据库：配送信息[{}]转发普利通失败。异常！\n{}", id, e.getMessage());
					jdbcUtil.rollback();
				}
				jdbcUtil.close();
				logger.error("配送信息[{}]转发普利通服务器失败！", id);
			});
		}
	}
	
	/**
	 * 关闭线程池
	 */
	public void closeThreadPool(){
		if (cachedThreadPool != null) {
			cachedThreadPool.shutdown();
		}
	}
}
