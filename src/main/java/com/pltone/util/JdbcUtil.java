package com.pltone.util;

import com.pltone.init.FilePathInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * jdbc数据库操作工具类
 * 
 * @author chenlong
 * @version 1.0 2018-02-13
 */
public class JdbcUtil {
	private static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);
	/** 数据库驱动 */
	private String driver;
	/** 数据库地址 */
	private String url;
	/** 数据库用户名 */
	private String username;
	/** 数据库密码 */
	private String password;
	/** 数据库的连接 */
	private Connection connection;
	/** 预编译的 SQL语句的对象 */
	private PreparedStatement preparedStatement;
	/** 查询返回的结果集合 */
	private ResultSet resultSet;

	public JdbcUtil() {

	}

	public JdbcUtil(String driver, String url, String username, String password) {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public PreparedStatement getPreparedStatement() {
		return preparedStatement;
	}

	public void setPreparedStatement(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * 数据源配置
	 * 
	 * @param dbConfigFilePath
	 *            数据源配置文件路径
	 */
	public void dbConfig(String dbConfigFilePath) {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader().getResourceAsStream(dbConfigFilePath));
			this.driver = properties.getProperty("jdbc.driverClassName");
			this.url = properties.getProperty("jdbc.url");
			this.username = properties.getProperty("jdbc.username");
			this.password = properties.getProperty("jdbc.password");
		} catch (IOException e) {
			logger.error("读取数据源配置文件异常！", e);
		}
	}

	/**
	 * 建立数据库连接
	 */
	public void createConnection() {
		try {
			Class.forName(driver); // 加载驱动
			connection = DriverManager.getConnection(url, username, password); // 获取连接
			connection.setAutoCommit(false);// 设置不自动提交
		} catch (Exception e) {
			logger.error("建立数据库连接异常！", e);
		}
	}

	/**
	 * 建立sqlite数据库连接
	 * 
	 * @param sqliteDbName
	 *            sqlite数据库名称
	 */
	public void createSqliteConnection(String sqliteDbName) {
		try {
			setDriver("org.sqlite.JDBC");
			Class.forName(driver); // 加载驱动
			url = new StringBuffer("jdbc:sqlite:").append(FilePathInit.DATA_DIR).append('/').append(sqliteDbName)
					.append(".db").toString();
			connection = DriverManager.getConnection(url);// sqlite不需要用户名密码
			connection.setAutoCommit(false);// 设置不自动提交
		} catch (Exception e) {
			logger.error("建立sqlite数据库连接异常！", e);
		}
	}

	/**
	 * 实例化预编译的 SQL语句的对象
	 * 
	 * @param sql
	 *            SQL语句
	 */
	public void createPrepareStatement(String sql) {
		try {
			if (connection != null) {
				preparedStatement = connection.prepareStatement(sql);
			} else {
				throw new Exception("connection is null");
			}
		} catch (Exception e) {
			logger.error("实例化预编译的 SQL语句的对象异常！", e);
		}

	}

	/**
	 * 将指定参数设置为给定 Java </code>boolean</code> 值。在将此值发送到数据库时，驱动程序将它转换成一个 SQL
	 * </code>BIT</code> 或 </code>BOOLEAN</code> 值。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		preparedStatement.setBoolean(parameterIndex, x);
	}

	/**
	 * 将指定参数设置为给定 Java </code>byte</code> 值。在将此值发送到数据库时，驱动程序将它转换成一个 SQL
	 * </code>TINYINT</code> 值。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		preparedStatement.setByte(parameterIndex, x);
	}

	/**
	 * 将指定参数设置为给定 Java </code>short</code> 值。在将此值发送到数据库时，驱动程序将它转换成一个 SQL
	 * </code>SMALLINT</code> 值。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		preparedStatement.setShort(parameterIndex, x);
	}

	/**
	 * 将指定参数设置为给定 Java </code>int</code> 值。在将此值发送到数据库时，驱动程序将它转换成一个 SQL
	 * </code>INTEGER</code> 值。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		preparedStatement.setInt(parameterIndex, x);
	}

	/**
	 * 将指定参数设置为给定 Java </code>long</code> 值。在将此值发送到数据库时，驱动程序将它转换成一个 SQL
	 * </code>BIGINT</code> 值。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		preparedStatement.setLong(parameterIndex, x);
	}

	/**
	 * 将指定参数设置为给定 Java </code>float</code> 值。在将此值发送到数据库时，驱动程序将它转换成一个 SQL
	 * </code>REAL</code> 值。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		preparedStatement.setFloat(parameterIndex, x);
	}

	/**
	 * 将指定参数设置为给定 Java </code>double</code> 值。在将此值发送到数据库时，驱动程序将它转换成一个 SQL
	 * </code>DOUBLE</code> 值。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		preparedStatement.setDouble(parameterIndex, x);
	}

	/**
	 * 将指定参数设置为给定 Java <code>String</code> 值。在将此值发送给数据库时，驱动程序将它转换成一个 SQL
	 * <code>VARCHAR</code> 或 <code>LONGVARCHAR</code> 值（取决于该参数相对于驱动程序在
	 * <code>VARCHAR</code> 值上的限制的大小）。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		preparedStatement.setString(parameterIndex, x);
	}

	/**
	 * 将指定参数设置为给定 Java byte 数组。在将此值发送给数据库时，驱动程序将它转换成一个 SQL
	 * <code>VARBINARY</code> 或 <code>LONGVARBINARY</code> 值（取决于该参数相对于驱动程序在
	 * <code>VARBINARY</code> 值上的限制的大小）。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setBytes(int parameterIndex, byte x[]) throws SQLException {
		preparedStatement.setBytes(parameterIndex, x);
	}

	/**
	 * <p>
	 * 使用给定对象设置指定参数的值。第二个参数必须是 Object 类型；所以，应该对内置类型使用 java.lang 的等效对象。
	 * 
	 * <p>
	 * JDBC 规范指定了一个从 Java <code>Object</code> 类型到 SQL
	 * 类型的标准映射关系。在发送到数据库之前，给定参数将被转换为相应 SQL 类型。
	 * 
	 * <p>
	 * 注意，通过使用特定于驱动程序的 Java 类型，此方法可用于传递特定于数据库的抽象数据类型。如果对象是实现
	 * <code>SQLData</code> 接口的类，则 JDBC 驱动程序应该调用 <code>SQLData.writeSQL</code>
	 * 方法将它写入 SQL 数据流中。另一方面，如果该对象是实现
	 * <code>Ref</code>、<code>Blob</code>、<code>Clob</code>、<code>NClob</code>、<code>Struct</code>、<code>java.net.URL</code>、<code>RowId</code>、<code>SQLXML</code>
	 * 或 <code>Array</code> 的类，则驱动程序应该将它作为相应 SQL 类型的值传递给数据库。
	 * 
	 * <p>
	 * <b>注：</b>并非所有的数据库都允许将非类型 Null 发送给后端。为了获得最大的可移植性，应该使用 <code>setNull</code>
	 * 或 <code>setObject(int parameterIndex, Object x, int sqlType)</code> 方法替代
	 * <code>setObject(int parameterIndex, Object x)</code>。
	 * 
	 * <p>
	 * <b>注：</b>如果出现混淆，例如，如果该对象是实现多个上述指定接口的类，则此方法抛出异常。
	 * 
	 * @param parameterIndex
	 *            第一个参数是 1，第二个参数是 2，……
	 * @param x
	 *            参数值
	 * @throws SQLException
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		preparedStatement.setObject(parameterIndex, x);
	}

	/**
	 * 在 PreparedStatement 对象中执行 SQL 语句，该语句必须是一个 SQL 数据操作语言（Data Manipulation
	 * Language，DML）语句，比如 INSERT、UPDATE 或 DELETE 语句；或者是无返回内容的 SQL 语句，比如 DDL 语句。
	 * 
	 * @return (1) SQL 数据操作语言 (DML) 语句的行数 (2) 对于无返回内容的 SQL 语句，返回 0
	 * @throws SQLException
	 */
	public int executeUpdate() throws SQLException {
		return preparedStatement.executeUpdate();
	}

	/**
	 * 获取自增ID
	 * 
	 * @param columnIndex 
	 *            the first column is 1, the second is 2, ...
	 * @return (1) 成功插入一条数据，返回其当前记录的自增ID (2) 插入失败，返回-1
	 * @throws SQLException
	 */
	public int getGeneratedKey(int columnIndex) throws SQLException {
		resultSet = preparedStatement.getGeneratedKeys();
		if (resultSet.next()) {
			return resultSet.getInt(columnIndex);
		}
		return -1;
	}

	/**
	 * 提交
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		connection.commit();
	}

	/**
	 * 回滚
	 */
	public void rollback() {
		try {
			connection.rollback();
		} catch (SQLException e) {
			logger.error("回滚异常！", e);
		}
	}

	/**
	 * 关闭资源,先开后关
	 */
	public void close() {
		try {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				resultSet = null;
			} finally {
				try {
					if (preparedStatement != null) {
						preparedStatement.close();
					}
					preparedStatement = null;
				} finally {
					if (connection != null) {
						connection.close();
					}
					connection = null;
				}
			}
		} catch (Exception e) {
			logger.error("关闭资源异常！", e);
		}
	}

	/**
	 * 增删改
	 * 
	 * @param sql
	 *            SQL语句
	 * @param parameters
	 *            不定长度参数
	 */
	public void update(String sql, Object... parameters) {
		try {
			createConnection();
			createPrepareStatement(sql);
			for (int i = 0, len = parameters.length; i < len; i++) {
				setObject(i + 1, parameters[i]);
			}
			int i = executeUpdate();
			commit();
			logger.info("SQL语句执行成功，影响" + i + "条记录！");
		} catch (Exception e) {
			logger.error("增删改操作异常！", e);
			rollback();
		} finally {
			close();
		}
	}

	/**
	 * 根据表名查询最大自增ID
	 * 
	 * @param tableName
	 *            表名
	 * @return 最大自增ID
	 */
	public int selectMaxId(String tableName) {
		String sql = new StringBuffer("SELECT MAX(id) id FROM ").append(tableName).toString();
		createPrepareStatement(sql);
		try {
			resultSet = getPreparedStatement().executeQuery();
			while (resultSet.next()) {
				return resultSet.getInt("id");
			}
		} catch (SQLException e) {
			logger.error("查询最大ID异常！", e);
		}
		return 0;
	}

	/**
	 * 查询需要转发的XML
	 * 
	 * @param sql
	 *            SQL语句
	 * @return 需要转发的XML
	 */
	public List<Map<String, Object>> selectForwordXml(String sql) {
		createPrepareStatement(sql);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			resultSet = getPreparedStatement().executeQuery();
			Map<String, Object> map = new HashMap<String, Object>();
			while (resultSet.next()) {
				map.put("id", resultSet.getInt(1));
				map.put("xml", resultSet.getString(2));
				list.add(map);
			}
		} catch (SQLException e) {
			logger.error("查询需要转发的XML异常！", e);
		}
		return list;
	}
}
