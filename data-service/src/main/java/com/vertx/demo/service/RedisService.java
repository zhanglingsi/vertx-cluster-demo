package com.vertx.demo.service;

import com.vertx.demo.service.impl.RedisServiceImpl;
import com.vertx.demo.utils.RedisCommand;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.serviceproxy.ServiceProxyBuilder;

/**
 * Created by zhangls on 2020/7/6.
 *
 * @author zhangls
 */
@ProxyGen
@VertxGen
public interface RedisService {

    public static final String SERVICE_NAME = "redis-svr";

    public static final String SERVICE_ADDRESS = "redis-svr-addr";

    /**
     * 执行Redis命令
     *
     * @param cname 命令名称枚举
     * @param params 格式{"key":"设置的键值","val":"设置的值","field":"MAP中的KEY"}
     * @param handler 异步处理
     * @return
     */
    @Fluent
    RedisService execComm(RedisCommand cname, JsonObject params, Handler<AsyncResult<String>> handler);

    /**
     * 创建服务实例
     *
     * @param client
     * @return
     */
    @GenIgnore
    static RedisService create(Redis client) {
        return new RedisServiceImpl(client);
    }

    /**
     * 创建服务代理
     *
     * @param vertx
     * @param address
     * @return
     */
    @GenIgnore
    static RedisService createProxy(Vertx vertx, String address) {
        return new ServiceProxyBuilder(vertx).setAddress(address).build(RedisService.class);
    }

}
