package com.vertx.demo.service;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by zhangls on 2020/7/6.
 *
 * @author zhangls
 */
@ProxyGen
@VertxGen
public interface MySQLService {

    public static final String SERVICE_NAME = "mysql-svr";

    public static final String SERVICE_ADDRESS = "mysql-svr-addr";

    @Fluent
    MySQLService qryForJsonArray(String sql, JsonObject params, Handler<AsyncResult<JsonArray>> handler);
}
