package com.pinzhikeji.service;

import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.otter.canal.protocol.CanalEntry;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DealService {

    private static final Log log = LogFactory.get();

    public void executeInsert(String tableName, List<CanalEntry.Column> columnsList) {
        Entity entity = Entity.create(tableName);
        for (CanalEntry.Column column : columnsList) {
            entity.set(column.getName(), column.getValue());
        }
        try {
            Db.use().insert(entity);
        } catch (SQLException e) {
            log.info(e);
        }
    }

    public void executeUpdate(String tableName, List<CanalEntry.Column> columnsList) {
        Entity entityUpdate = Entity.create(); //修改的数据
        Entity entityWhere = Entity.create(tableName); // where条件
        for (CanalEntry.Column column : columnsList) {
            if (column.getIsKey()) {
                entityWhere.set(column.getName(), column.getValue());
            } else if (column.getUpdated()) {
                entityUpdate.set(column.getName(), column.getValue());
            }
        }
        try {
            Db.use().update(entityUpdate, entityWhere);
        } catch (SQLException e) {
            log.info(e);
        }
    }

    public void executeDelete(String tableName, List<CanalEntry.Column> columnsList) {
        Entity entityWhere = Entity.create(tableName); // where条件
        for (CanalEntry.Column column : columnsList) {
            if (column.getIsKey()) {
                entityWhere.set(column.getName(), column.getValue());
            }
        }
        try {
            Db.use().del(entityWhere);
        } catch (SQLException e) {
            log.info(e);
        }
    }

    public void executeDdl(String sql) {
        Connection conn = null;
        DataSource ds = new SimpleDataSource("jdbc:mysql://localhost:3306/test?characterEncoding=utf8&useSSL=false", "root", "root");
        try {
            try {
                conn = ds.getConnection();
            } catch (SQLException e) {
                log.info(e);
            }
            if (conn != null) {
                // 执行非查询语句，返回影响的行数
                int count = SqlExecutor.execute(conn, sql);
                System.out.println(count);
                // 执行非查询语句，返回自增的键，如果有多个自增键，只返回第一个
//            Long generatedKey = SqlExecutor.executeForGeneratedKey(conn, "UPDATE " + TABLE_NAME + " set field1 = ? where id = ?", 0, 0);

                /* 执行查询语句，返回实体列表，一个Entity对象表示一行的数据，Entity对象是一个继承自HashMap的对象，存储的key为字段名，value为字段值 */
//            List<Entity> entityList = SqlExecutor.query(conn, "select * from " + TABLE_NAME + " where param1 = ?", new EntityListHandler(), "值");
            }
        } catch (SQLException e) {
            log.info(e);
        } finally {
            DbUtil.close(conn);
        }
    }
}
