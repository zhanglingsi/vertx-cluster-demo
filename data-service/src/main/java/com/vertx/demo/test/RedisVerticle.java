package com.vertx.demo.test;

import com.google.common.collect.Lists;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.RedisOptions;

/**
 * Created by zhangls on 2020/7/8.
 */
public class RedisVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisVerticle.class);
    private final RedisOptions redisConnOptions = new RedisOptions()
            .setMaxPoolSize(8).setMaxWaitingHandlers(32)
            .setConnectionString("redis://127.0.0.1:6379/");

    private Redis client;

    @Override
    public void start(Promise<Void> promise) throws Exception {
        client = Redis.createClient(vertx, redisConnOptions);

        String commandName = "set";

        JsonObject params = new JsonObject();
        params.put("key", "test02");
        params.put("val", "test02");

        client.connect(conn -> {
            if (conn.succeeded()) {
                RedisAPI redisAPI = RedisAPI.api(conn.result());
                redisAPI.set(Lists.newArrayList("zhangls", "123456"), ar -> {
                    if (ar.succeeded()) {
                        System.out.println(ar.result());
                    } else {
                        promise.fail(ar.cause());
                    }
                });

                redisAPI.get("zhangls", ar -> {
                    if (ar.succeeded()){
                        System.out.println(ar.result());
                    }else {
                        promise.fail(ar.cause());
                    }
                });
            } else {
                LOGGER.error("Could not Redis connect: " + conn.cause().getMessage());
                promise.fail(conn.cause());
            }
        });
    }


    private Future<RedisConnection> getRedisConn() {
        Promise<RedisConnection> promise = Promise.promise();

        client.connect(conn -> {
            if (conn.succeeded()) {
                promise.complete(conn.result());
            } else {
                LOGGER.error("Could not Redis connect: " + conn.cause().getMessage());
                promise.fail(conn.cause());
            }
        });

        return promise.future();
    }

    private Future<String> execRedisCommand(RedisConnection conn, String commandName, JsonObject params) {
        Promise<String> promise = Promise.promise();

        RedisAPI redisAPI = RedisAPI.api(conn);
        switch (commandName) {
            case "set":
                set(redisAPI, params);
            default:
                promise.complete("");
        }

        return promise.future();
    }

    private Future<String> set(RedisAPI api, JsonObject params) {
        Promise<String> promise = Promise.promise();

        String key = String.valueOf(params.getValue("key"));
        String val = String.valueOf(params.getValue("val"));

        api.set(Lists.newArrayList(key, val), ar -> {
            if (ar.succeeded()) {
                promise.complete(ar.result().toString());
            } else {
                promise.fail(ar.cause());
            }
        });

        return promise.future();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        client.close();
    }
}
