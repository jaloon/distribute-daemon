package com.pltone.ws.constant;

/**
 * 配送单报文节点名称常量
 * 
 * @author chenlong
 * @version 1.0 2018-07-05
 */
public final class DistXmlNodeNameConst {
	/** 根节点：TradeData */
	public static final String ROOT_NODE_TRADE_DATA = "TradeData";
	/** 一级节点1：BizKey */
	public static final String NODE_1_1_BIZ_KEY = "BizKey";
	/** 一级节点2：Message */
	public static final String NODE_1_2_MESSAGE = "Message";
	/** 二级节点：dlists */
	public static final String NODE_2_DLISTS = "dlists";
	/** 三级节点：dlist */
	public static final String NODE_3_DLIST = "dlist";
	/** 四级节点01：distributNO */
	public static final String NODE_4_01_DISTRIBUT_NO = "distributNO";
	/** 四级节点02：effectDate */
	public static final String NODE_4_02_EFFECT_DATE = "effectDate";
	/** 四级节点03：vehicNo */
	public static final String NODE_4_03_VEHIC_NO = "vehicNo";
	/** 四级节点04：cardNo */
	public static final String NODE_4_04_CARD_NO = "cardNo";
	/** 四级节点05：binNum */
	public static final String NODE_4_05_BIN_NUM = "binNum";
	/** 四级节点06：deptId */
	public static final String NODE_4_06_DEPT_ID = "deptId";
	/** 四级节点07：deptName */
	public static final String NODE_4_07_DEPT_NAME = "deptName";
	/** 四级节点08：depotNo */
	public static final String NODE_4_08_DEPOT_NO = "depotNo";
	/** 四级节点09：depotName */
	public static final String NODE_4_09_DEPOT_NAME = "depotName";
	/** 四级节点10：distributFlag */
	public static final String NODE_4_10_DISTRIBUT_FLAG = "distributFlag";
	/** dlist的子节点名称 */
	public static final String[] DLIST_CHILD_NODES = { NODE_4_01_DISTRIBUT_NO, NODE_4_02_EFFECT_DATE,
			NODE_4_03_VEHIC_NO, NODE_4_04_CARD_NO, NODE_4_05_BIN_NUM, NODE_4_06_DEPT_ID, NODE_4_07_DEPT_NAME,
			NODE_4_08_DEPOT_NO, NODE_4_09_DEPOT_NAME, NODE_4_10_DISTRIBUT_FLAG };
	/** 有用的配送节点 */
	public static final String[] USEFUL_DIST_NODES = { NODE_4_01_DISTRIBUT_NO, NODE_4_03_VEHIC_NO,
			NODE_4_05_BIN_NUM, NODE_4_06_DEPT_ID, NODE_4_08_DEPOT_NO };
}