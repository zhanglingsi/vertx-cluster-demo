package com.vertx.demo;

import com.hazelcast.internal.json.Json;
import com.vertx.demo.common.BaseVerticle;
import com.vertx.demo.service.MySQLService;
import com.vertx.demo.service.RedisService;
import com.vertx.demo.utils.RedisCommand;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;

/**
 * Created by zhangls on 2020/7/7.
 *
 * @author zhangls
 */
public class StarterVerticle extends BaseVerticle {

    private static final Logger logger = LoggerFactory.getLogger(StarterVerticle.class);

    private static final String sql = "SELECT USER_ID,REAL_NAME,EMAIL,PASS_WORD,PHONE_NUM,UPDATE_TIME FROM TB_USER WHERE STATUS=? AND USER_ID=?";

//    String consumer_host = config().getString("consumer.host");

    /**
     * http://127.0.0.1:8080/ebServiceProxy/qryRedisExists/zls
     * http://127.0.0.1:8080/api/qryData
     *
     * @param promise
     * @throws Exception
     */
    @Override
    public void start(Promise<Void> promise) throws Exception {
        super.start(promise);

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        // 返回key字符的长度
        router.get("/:key").handler(this::index);
        // 返回redis中key的值
        router.get("/redis/get/:key").handler(this::get);
        router.route("/api/*").handler(BodyHandler.create());
        router.post("/api/set").handler(this::set);
        router.post("/api/qryData").handler(this::qryData);

        server.requestHandler(router).listen(config().getInteger("http.port"), config().getString("http.host"));
    }

    private void index(RoutingContext context) {
        String key = context.request().getParam("key");

        context.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.array(String.valueOf(key.length())).toString());
    }

    private void set(RoutingContext context) {
        long startTime = System.currentTimeMillis();
        JsonObject params = new JsonObject(context.getBodyAsString());

        EventBusService.getProxy(discovery, RedisService.class, ar -> {
            if (ar.succeeded()) {
                RedisService service = ar.result();
                service.execComm(RedisCommand.SET, params, r -> {
                    context.response()
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(r.result());
                    long endTime = System.currentTimeMillis();
                    logger.info("程序运行时间：" + (endTime - startTime) + "ms");
                    ServiceDiscovery.releaseServiceObject(discovery, service);
                });
            } else {
                System.out.println(ar.cause().getMessage());
            }
        });
    }

    private void get(RoutingContext context) {
        long startTime = System.currentTimeMillis();
        String key = context.request().getParam("key");

        EventBusService.getProxy(discovery, RedisService.class, ar -> {
            if (ar.succeeded()) {
                RedisService service = ar.result();
                service.execComm(RedisCommand.GET, new JsonObject().put("key", key), r -> {
                    context.response()
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(r.result());
                    long endTime = System.currentTimeMillis();
                    logger.info("程序运行时间：" + (endTime - startTime) + "ms");
                    ServiceDiscovery.releaseServiceObject(discovery, service);
                });
            } else {
                System.out.println(ar.cause().getMessage());
            }
        });
    }

    private void qryData(RoutingContext context) {
        long startTime = System.currentTimeMillis();
        JsonObject params = new JsonObject(context.getBodyAsString());

        System.out.println("params = " + params.toString());

        String sql = String.valueOf(params.getValue("sql"));
        System.out.println("sql = " + sql);
        String arg0 = String.valueOf(params.getValue("arg0"));
        System.out.println("arg0 = " + arg0);
        String arg1 = String.valueOf(params.getValue("arg1"));
        System.out.println("arg1 = " + arg1);

        JsonObject jsonParams = new JsonObject();
        jsonParams.put("arg0", arg0);
        jsonParams.put("arg1", arg1);

        EventBusService.getProxy(discovery, MySQLService.class, ar -> {
            if (ar.succeeded()) {
                MySQLService service = ar.result();
                service.qryForJsonArray(sql, jsonParams, r -> {
                    context.response()
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(String.valueOf(r.result()));
                    long endTime = System.currentTimeMillis();
                    logger.info("程序运行时间：" + (endTime - startTime) + "ms");
                    ServiceDiscovery.releaseServiceObject(discovery, service);
                });
            } else {
                System.out.println(ar.cause().getMessage());
            }
        });
    }

    @Override
    public void stop(Promise<Void> promise) throws Exception {
        discovery.close();
    }
}
