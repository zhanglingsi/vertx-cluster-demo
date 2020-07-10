package com.vertx.demo.test;

import com.google.common.collect.Lists;
import com.vertx.demo.utils.BaseUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;

import java.util.List;

/**
 * Created by zhangls on 2020/7/7.
 *
 * @author zhangls
 */
public class MySQLVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLVerticle.class);

    private MySQLPool client;

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
    public void start(Promise<Void> promise) throws Exception {
        client = MySQLPool.pool(vertx, connectOptions, mySQLPoolOptions);

        vertx.executeBlocking(promise1 -> {
            Future<List<JsonObject>> future = queryUserInfo("SELECT USER_ID,REAL_NAME,EMAIL,PASS_WORD,PHONE_NUM,UPDATE_TIME FROM TB_USER WHERE STATUS=? AND USER_ID=?",new JsonObject());
            future.onComplete(ar -> {
                if (ar.succeeded()) {
                    List<JsonObject> list = ar.result();
                    for (JsonObject json : list) {
                        System.out.println(json.toString());
                    }
                } else {
                    System.out.println(ar.cause().getMessage());
                }
            });
            promise1.complete(future);
        }, asyncResult -> {
            System.out.println(asyncResult.result());
        });

    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        client.close();
    }


    private Future<SqlConnection> getConn() {
        Long startTime = System.currentTimeMillis();
        Promise<SqlConnection> promise = Promise.promise();
        client.getConnection(conn -> {
            if (conn.succeeded()) {
                promise.complete(conn.result());
                System.out.println("getConn method time:" + String.valueOf(System.currentTimeMillis() - startTime));
            } else {
                promise.fail(conn.cause());
            }
        });

        return promise.future();
    }

    private Future<List<JsonObject>> queryUserInfo(String sql, JsonObject params) {
        Long startTime = System.currentTimeMillis();
        Promise<List<JsonObject>> promise = Promise.promise();

        client.preparedQuery(sql).execute(BaseUtils.crtTuple(params), ar -> {
            System.out.println("client method time:" + String.valueOf(System.currentTimeMillis() - startTime));
            if (ar.succeeded()) {
                List<JsonObject> list = Lists.newArrayList();
                RowIterator<Row> iterator = ar.result().iterator();
                while (iterator.hasNext()) {
                    JsonObject jsonRow = new JsonObject();
                    Row row = iterator.next();
                    for (int i = 0; i < row.size(); i++) {
                        jsonRow.put(BaseUtils.underline2Camel(row.getColumnName(i), true), String.valueOf(row.getValue(i)));
                    }
                    list.add(jsonRow);
                }
                promise.complete(list);
                System.out.println("ALL method time:" + String.valueOf(System.currentTimeMillis() - startTime));
            } else {
                promise.fail(ar.cause());
            }
        });

        return promise.future();
    }

    private Future<RowSet<Row>> qryUserInfo(SqlConnection conn, String status) {
        Long startTime = System.currentTimeMillis();
        Promise<RowSet<Row>> promise = Promise.promise();

        conn.query("SELECT REAL_NAME,EMAIL FROM TB_USER WHERE STATUS=" + status).execute(res -> {
            conn.close();
            if (res.succeeded()) {
                promise.complete(res.result());
                System.out.println("qryUserInfo method time:" + String.valueOf(System.currentTimeMillis() - startTime));
            } else {
                promise.fail(res.cause());
            }
            conn.close();
        });

        return promise.future();
    }

    private Future<List<JsonObject>> qryData(RowSet<Row> rows) {
        Long startTime = System.currentTimeMillis();
        Promise<List<JsonObject>> promise = Promise.promise();

        RowIterator<Row> rowIterator = rows.iterator();
        List<JsonObject> list = Lists.newArrayList();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String userName = String.valueOf(row.getValue("REAL_NAME"));
            String email = String.valueOf(row.getValue("EMAIL"));

            JsonObject jsonObject = new JsonObject();
            jsonObject.put("name", userName);
            jsonObject.put("email", email);
            list.add(jsonObject);
        }
        System.out.println("qryData method time:" + String.valueOf(System.currentTimeMillis() - startTime));
        promise.complete(list);

        return promise.future();
    }
}
