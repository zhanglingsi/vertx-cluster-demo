package com.vertx.demo.service;

import com.vertx.demo.service.impl.MySQLServiceImpl;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.serviceproxy.ServiceProxyBuilder;

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

    /**
     * 创建服务实例
     *
     * @param client
     * @return
     */
    @GenIgnore
    static MySQLService create(MySQLPool client) {
        return new MySQLServiceImpl(client);
    }

    /**
     * 创建服务代理
     *
     * @param vertx
     * @param address
     * @return
     */
    @GenIgnore
    static MySQLService createProxy(Vertx vertx, String address) {
        return new ServiceProxyBuilder(vertx).setAddress(address).build(MySQLService.class);
    }
}
