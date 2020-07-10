package com.vertx.demo.service.impl;

import com.google.common.collect.Lists;
import com.vertx.demo.service.RedisService;
import com.vertx.demo.utils.RedisCommand;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;


/**
 * Created by zhangls on 2020/7/6.
 *
 * @author zhangls
 */
public class RedisServiceImpl implements RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceImpl.class);

    private RedisAPI redisAPI;

    public RedisServiceImpl(Redis client) {
        client.connect(conn -> {
            if (conn.succeeded()) {
                redisAPI = RedisAPI.api(conn.result());
            } else {
                LOGGER.error("Could not Redis connect: " + conn.cause().getMessage());
            }
        });
    }

    @Override
    public RedisService execComm(RedisCommand cname, JsonObject params, Handler<AsyncResult<String>> handler) {
        String key = String.valueOf(params.getValue("key"));
        String val = String.valueOf(params.getValue("val", ""));

        switch (cname) {
            case GET:
                this.get(key).onComplete(ar -> {
                    if (ar.succeeded()) {
                        handler.handle(Future.succeededFuture(ar.result()));
                    } else {
                        handler.handle(Future.failedFuture(ar.cause()));
                    }
                });
                break;
            case SET:
                this.set(key, val).onComplete(ar -> {
                    if (ar.succeeded()) {
                        handler.handle(Future.succeededFuture(ar.result()));
                    } else {
                        handler.handle(Future.failedFuture(ar.cause()));
                    }
                });
                break;
            default:
                break;
        }

        return this;
    }

    private Future<String> get(String key) {
        Promise<String> promise = Promise.promise();
        redisAPI.get(key, res -> {
            if (res.failed()) {
                LOGGER.error("Redis exec command GET Key=" + key + " is fail", res.cause());
                promise.fail(res.cause());
            } else {
                promise.complete(String.valueOf(res.result()));
            }
        });

        return promise.future();
    }

    private Future<String> set(String key, String val) {
        Promise<String> promise = Promise.promise();
        redisAPI.set(Lists.newArrayList(key, val), res -> {
            if (res.failed()) {
                LOGGER.error("Redis exec command SET Key=" + key + " is fail", res.cause());
                promise.fail(res.cause());
            } else {
                promise.complete(String.valueOf(res.result()));
            }
        });

        return promise.future();
    }
}
