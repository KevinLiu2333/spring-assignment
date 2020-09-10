package com.lagou.edu.utils;

import com.lagou.edu.anno.Autowired;
import com.lagou.edu.anno.Service;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: kevliu3
 * Date: 2020/9/6
 * Time: 9:59 AM
 * 事务管理器类:负责手动事务的开启,提交,回滚
 *
 * @author kevliu3
 */
@Service("transactionManager")
public class TransactionManager {

    @Autowired
    private ConnectionUtils connectionUtils;

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    private static TransactionManager transactionManager = new TransactionManager();

    public static TransactionManager getInstance() {
        return transactionManager;
    }

    //开启手动事务控制
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }

    //提交事务
    public void commit() throws SQLException {
        connectionUtils.getCurrentThreadConn().commit();
    }

    //回滚事务
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadConn().rollback();
    }
}
