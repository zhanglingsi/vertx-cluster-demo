package com.vertx.demo.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.backend.zookeeper.ZookeeperBackendService;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.MessageSource;

import java.util.List;
import java.util.Set;

/**
 * Created by zhangls on 2020/7/3.
 *
 * @author zhangls
 */
public class BaseVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseVerticle.class);

    protected ServiceDiscovery discovery;

    protected Set<Record> records = Sets.newConcurrentHashSet();

    @Override
    public void start(Promise<Void> promise) throws Exception {
        JsonObject backendJson = new JsonObject()
                .put("backend-name", ZookeeperBackendService.class.getName())
                .put("connection", config().getString("zk.ip") + ":" + config().getString("zk.port"))
                .put("maxRetries", 4)
                .put("baseSleepTimeBetweenRetries", 1000)
                .put("canBeReadOnly", false)
                .put("connectionTimeoutMs", 1000)
                .put("basePath", config().getString("basePath"))
                .put("guaranteed", true);

        ServiceDiscoveryOptions opt = new ServiceDiscoveryOptions()
                .setName(config().getString("zk.discovery.name"))
                .setBackendConfiguration(backendJson);

        discovery = ServiceDiscovery.create(vertx, opt);
    }

    @Override
    public void stop(Promise<Void> promise) throws Exception {
        LOGGER.info("start unpublish microservices.");
        List<Future> futures = Lists.newArrayList();
        records.forEach(record -> {
            Promise<Void> vp = Promise.promise();

            discovery.unpublish(record.getRegistration(), ar -> {
                if (ar.succeeded()) {
                    futures.add(vp.future());
                    vp.complete();
                } else {
                    vp.fail(ar.cause());
                }
            });
        });

        if (futures.isEmpty()) {
            discovery.close();
            promise.complete();
        } else {
            CompositeFuture.all(futures).onComplete(ar -> {
                discovery.close();
                if (ar.failed()) {
                    promise.fail(ar.cause());
                } else {
                    promise.complete();
                }
            }).onFailure(ar -> {
                promise.fail(ar.getCause());
            });
        }
    }

    private Future<Void> publish(Record record) {
        Promise<Void> promise = Promise.promise();

        if (discovery == null) {
            try {
                start();
            } catch (Exception e) {
                throw new IllegalStateException("Cannot create discovery service");
            }
        }

        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Service <" + ar.result().getName() + "> published");
                promise.complete();
            } else {
                promise.fail(ar.cause());
            }
        });

        return promise.future();
    }


    protected Future<Void> publishEBService(String name, String address, Class serviceClass) {
        Record record = EventBusService.createRecord(name, address, serviceClass);

        return publish(record);
    }

    protected Future<Void> publishHttpEndPoint(String name, String host, int port) {
        Record record = HttpEndpoint.createRecord(name, host, port, "/", new JsonObject().put("api.name", config().getString("api.name", "")));

        return publish(record);
    }

    protected Future<Void> publishApiGateway(String host, int port) {
        Record record = HttpEndpoint.createRecord("api-gateway", true, host, port, "/", null).setType("api-gateway");

        return publish(record);
    }

    protected Future<Void> publishMessageSource(String name, String address) {
        Record record = MessageSource.createRecord(name, address);

        return publish(record);
    }
}
