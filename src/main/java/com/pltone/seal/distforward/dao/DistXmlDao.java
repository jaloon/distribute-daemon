package com.pltone.seal.distforward.dao;

import com.pltone.seal.distforward.bean.ForwardXmlState;
import com.pltone.seal.distforward.init.SqliteDbInit;
import com.pltone.seal.distforward.util.JdbcUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 配送报文持久层
 *
 * @author chenlong
 * @version 1.0 2019-01-08
 */
public class DistXmlDao {
    private static DistXmlDao instance = new DistXmlDao();

    public static DistXmlDao getInstance() {
        return instance;
    }

    private DistXmlDao() {}

    /**
     * 添加配送报文记录
     *
     * @param xml 配送报文
     * @return 记录ID
     */
    public long add(String xml) {
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.createSqliteConnection(SqliteDbInit.SQLITE_DB_NAME);
        String sql = "INSERT INTO tbl_distribute(xml) values(?)";
        jdbcUtil.createPrepareStatement(sql);
        try {
            jdbcUtil.setString(1, xml);
            jdbcUtil.executeUpdate();
            long id = jdbcUtil.getGeneratedKey(1);
            jdbcUtil.commit();
            return id;
        } catch (SQLException e) {
            jdbcUtil.rollback();
            throw new RuntimeException(e);
        } finally {
            jdbcUtil.close();
        }
    }

    /**
     * 更新瑞通转发状态
     *
     * @param forwardXmlState 转发状态
     */
    public void updateRtForwardState(ForwardXmlState forwardXmlState) {
        updateForwardState("UPDATE tbl_distribute SET rt = ? WHERE id = ?", forwardXmlState);
    }

    /**
     * 更新普利通转发状态
     *
     * @param forwardXmlState 转发状态
     */
    public void updatePltForwardState(ForwardXmlState forwardXmlState) {
        updateForwardState("UPDATE tbl_distribute SET plt = ? WHERE id = ?", forwardXmlState);
    }

    /**
     * 更新转发状态
     *
     * @param sql          更新SQL语句
     * @param forwardXmlState 转发状态
     */
    private void updateForwardState(String sql, ForwardXmlState forwardXmlState) {
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.createSqliteConnection(SqliteDbInit.SQLITE_DB_NAME);
        jdbcUtil.createPrepareStatement(sql);
        try {
            jdbcUtil.setInt(1, forwardXmlState.getState());
            jdbcUtil.setLong(2, forwardXmlState.getId());
            jdbcUtil.executeUpdate();
            jdbcUtil.commit();
        } catch (SQLException e) {
            jdbcUtil.rollback();
            throw new RuntimeException(e);
        } finally {
            jdbcUtil.close();
        }
    }

    /**
     * 查询需要转发瑞通的物流报文
     *
     * @return 物流报文信息列表
     */
    public List<ForwardXmlState> findRtForwardXmlList() {
        return findForwardXmlList("rt");
    }

    /**
     * 查询需要转发普利通的物流报文
     *
     * @return 物流报文信息列表
     */
    public List<ForwardXmlState> findPltForwardXmlList() {
        return findForwardXmlList("plt");
    }

    /**
     * 查询需要转发的物流报文
     *
     * @param target 转发目标
     * @return 物流报文信息列表
     */
    private List<ForwardXmlState> findForwardXmlList(String target) {
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.createSqliteConnection(SqliteDbInit.SQLITE_DB_NAME);
        String sql = new StringBuilder()
                .append("SELECT\n")
                .append("  \"id\",\n")
                .append("  \"xml\"\n")
                .append("FROM\n")
                .append("  \"tbl_distribute\"\n")
                .append("WHERE\n")
                .append("  \"").append(target).append("\" = 2\n")
                .append("  AND \"time\" BETWEEN DATETIME('now', 'localtime', '-30 minute') AND DATETIME('now', 'localtime');")
                .toString();
        jdbcUtil.createPrepareStatement(sql);
        try {
            List<ForwardXmlState> list = new ArrayList<>();
            ResultSet resultSet = jdbcUtil.getPreparedStatement().executeQuery();
            ForwardXmlState forwardXmlState;
            while (resultSet.next()) {
                forwardXmlState = new ForwardXmlState();
                forwardXmlState.setId(resultSet.getLong(1));
                forwardXmlState.setXml(resultSet.getString(2));
                forwardXmlState.setState(2);
                list.add(forwardXmlState);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            jdbcUtil.close();
        }
    }

}
