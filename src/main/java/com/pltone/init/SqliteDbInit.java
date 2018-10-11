package com.pltone.init;

import com.pltone.util.JdbcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * sqlite数据库初始化
 *
 * @author chenlong
 * @version 1.0 2018-02-27
 */
public class SqliteDbInit {
    private static final Logger logger = LoggerFactory.getLogger(SqliteDbInit.class);
    private static final JdbcUtil JDBC_UTIL = new JdbcUtil();
    /**
     * 数据库名称
     */
    public static final String SQLITE_DB_NAME = "distribute";

    /**
     * 初始化
     */
    public static void init() {
        try {
            File dbFlie = new File(FilePathInit.getDbFilePath());
            if (dbFlie.exists()) {
                logger.info("数据库文件[{}.db]已存在！", SQLITE_DB_NAME);
                return;
            }
            JDBC_UTIL.createSqliteConnection(SQLITE_DB_NAME);
            String sql = new StringBuffer()
                    .append("CREATE TABLE IF NOT EXISTS \"tbl_distribute\" (")
                    .append("  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,")                 // id	自增主键
                    .append("  \"xml\" text NOT NULL,")                                             // xml	物流配送信息XML文本
                    .append("  \"time\" timestamp NOT NULL DEFAULT (datetime('now','localtime')),") // time	XML接收时间（默认当前时间）
                    .append("  \"rt\" tinyint(1) NOT NULL DEFAULT 0,")                              // rt	转发瑞通系统状态：0 未转发（默认），1 转发成功，2 转发失败
                    .append("  \"plt\" tinyint(1) NOT NULL DEFAULT 0")                              // plt	转发普利通系统状态：0 未转发（默认），1 转发成功，2 转发失败
                    .append(");")
                    .toString();
            JDBC_UTIL.createPrepareStatement(sql);
            JDBC_UTIL.executeUpdate();
            JDBC_UTIL.commit();
            logger.info("初始化sqlite数据库[" + SQLITE_DB_NAME + ".db]成功！");
        } catch (Exception e) {
            logger.error("初始化sqlite数据库[" + SQLITE_DB_NAME + ".db]异常！", e);
        } finally {
            JDBC_UTIL.close();
        }
    }

    private SqliteDbInit() {
    }
}