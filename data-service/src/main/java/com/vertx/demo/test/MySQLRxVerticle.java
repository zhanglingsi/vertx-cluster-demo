package com.vertx.demo.test;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import io.vertx.reactivex.ext.asyncsql.MySQLClient;
import io.vertx.reactivex.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * Created by zhangls on 2020/7/9.
 */
public class MySQLRxVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLRxVerticle.class);

    private MySQLPool client;

    private static final String SQL = "SELECT USER_ID,REAL_NAME,EMAIL,PASS_WORD,PHONE_NUM,UPDATE_TIME FROM TB_USER WHERE STATUS=? AND USER_ID=?";

    private final MySQLConnectOptions connectOptions = new MySQLConnectOptions()
            .setCachePreparedStatements(true)
            .setPreparedStatementCacheMaxSize(100)
            .setConnectTimeout(5000)
            .setPort(3306)
            .setHost("127.0.0.1")
            .setDatabase("test")
            .setUser("root")
            .setPassword("123456")
            .setCharset("utf8mb4")
            .setCharacterEncoding("utf-8")
            .addProperty("useUnicode", "true")
            .addProperty("allowMultiQueries", "true")
            .addProperty("serverTimezone", "Hongkong");

    private final PoolOptions mySQLPoolOptions = new PoolOptions().setMaxSize(5);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        JsonObject config = new JsonObject()
                .put("connection_string", "mongodb://localhost:27018")
                .put("db_name", "my_DB");


        client = MySQLPool.pool(vertx, connectOptions, mySQLPoolOptions);

        AsyncSQLClient asyncSQLClient =  MySQLClient.createShared(vertx, config, "mysql-dataSource");



        client.rxGetConnection().flatMap(conn -> conn.rxPrepare(SQL).doFinally(conn::close)).subscribe(resultSet -> {
            System.out.println("Results : " + resultSet.query());
        }, Throwable::printStackTrace);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        client.close();
    }
}
