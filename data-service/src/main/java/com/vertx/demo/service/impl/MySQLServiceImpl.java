package com.vertx.demo.service.impl;

import com.vertx.demo.service.MySQLService;
import com.vertx.demo.utils.BaseUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

/**
 * Created by zhangls on 2020/7/6.
 *
 * @author zhangls
 */
public class MySQLServiceImpl implements MySQLService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLServiceImpl.class);

    private SqlConnection connection;

    private MySQLPool client;

    private static final String QRY_USER_INFO = "SELECT USER_ID,REAL_NAME,EMAIL,PASS_WORD,PHONE_NUM,UPDATE_TIME FROM TB_USER WHERE STATUS=? AND USER_ID=?";

    public MySQLServiceImpl(MySQLPool client) {
        this.client = client;

        client.getConnection(ar -> {
            if (ar.succeeded()) {
                connection = ar.result();
            } else {
                LOGGER.error("Could not MySQL connect: " + ar.cause().getMessage());
            }
        });
    }

    @Override
    public MySQLService qryForJsonArray(String sql, JsonObject params, Handler<AsyncResult<JsonArray>> handler) {
        System.out.println("sql = " + sql);
        System.out.println("params = " + params.toString());

        client.preparedQuery(sql).execute(Tuple.of(params.getValue("arg0"), params.getValue("arg1")), ar -> {
            if (ar.succeeded()) {
                JsonArray res = new JsonArray();
                RowIterator<Row> rows = ar.result().iterator();
                while (rows.hasNext()) {
                    JsonObject json = new JsonObject();
                    Row row = rows.next();
                    for (int i = 0; i < row.size(); i++) {
                        json.put(BaseUtils.underline2Camel(row.getColumnName(i), true), String.valueOf(row.getValue(i)));
                    }
                    res.add(json);
                }
                System.out.println("return JsonArray = " + res.toString());
                handler.handle(Future.succeededFuture(res));
            } else {
                handler.handle(Future.failedFuture(ar.cause()));
            }

        });

        return this;
    }

}
