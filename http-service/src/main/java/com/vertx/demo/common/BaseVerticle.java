package com.vertx.demo.common;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zhangls on 2020/7/7.
 *
 * @author zhangls
 */
public class BaseVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(BaseVerticle.class);

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


    protected Future<Void> publishEventBusService(String name, String address, Class serviceClass) {
        Record record = EventBusService.createRecord(name, address, serviceClass);
        return publish(record);
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

        // publish the service
        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                logger.info("Service <" + ar.result().getName() + "> published");
                promise.complete();
            } else {
                promise.fail(ar.cause());
            }
        });

        return promise.future();
    }

    @Override
    public void stop(Promise<Void> promise) throws Exception {
        logger.info("start unpublish microservices.");

        List<Future> futures = new ArrayList<>();
        records.forEach(record -> {
            Future<Void> cleanupFuture = Future.future();
            futures.add(cleanupFuture);
            discovery.unpublish(record.getRegistration(), cleanupFuture.completer());
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
            });
        }
    }

}
