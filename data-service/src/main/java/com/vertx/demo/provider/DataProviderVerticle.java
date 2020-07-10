package com.vertx.demo.provider;

import com.vertx.demo.common.BaseVerticle;
import com.vertx.demo.service.MySQLService;
import com.vertx.demo.service.RedisService;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisClientType;
import io.vertx.redis.client.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.sqlclient.PoolOptions;

/**
 * Created by zhangls on 2020/7/6.
 *
 * @author zhangls
 */
public class DataProviderVerticle extends BaseVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataProviderVerticle.class);

    private MySQLPool mySQLPool;

    private Redis redisClient;

    private final RedisOptions redisConnOptions = new RedisOptions()
            .setMaxPoolSize(8).setMaxWaitingHandlers(32)
            .setConnectionString("redis://127.0.0.1:6379/");

    private final RedisOptions redisClusterOptions = new RedisOptions().setType(RedisClientType.CLUSTER)
            .setMaxPoolSize(8).setMaxWaitingHandlers(32)
            .setConnectionString("redis://172.22.241.25:20011")
            .setConnectionString("redis://172.22.241.26:20011")
            .setConnectionString("redis://172.22.241.27:20011")
            .setConnectionString("redis://172.22.245.216:20011")
            .setConnectionString("redis://172.22.245.217:20011")
            .setConnectionString("redis://172.22.245.218:20011");

    private final MySQLConnectOptions mySQLConnOptions = new MySQLConnectOptions()
            .setPort(3306)
            .setHost("127.0.0.1")
            .setDatabase("test")
            .setUser("root")
            .setPassword("123456")
            .setCharset("utf8")
            .setCollation("utf8_general_ci");

    private final PoolOptions mySQLPoolOptions = new PoolOptions().setMaxSize(5);

    @Override
    public void start(Promise<Void> promise) throws Exception {
        super.start(promise);

        redisClient = Redis.createClient(vertx, redisConnOptions);
        RedisService redisService = RedisService.create(redisClient);
        new ServiceBinder(vertx).setAddress(RedisService.SERVICE_ADDRESS).register(RedisService.class, redisService);
        publishEBService(RedisService.SERVICE_NAME, RedisService.SERVICE_ADDRESS, RedisService.class);


        mySQLPool = MySQLPool.pool(vertx, mySQLConnOptions, mySQLPoolOptions);
        MySQLService mySQLService = MySQLService.create(mySQLPool);
        new ServiceBinder(vertx).setAddress(MySQLService.SERVICE_ADDRESS).register(MySQLService.class, mySQLService);
        publishEBService(MySQLService.SERVICE_NAME, MySQLService.SERVICE_ADDRESS, MySQLService.class);

    }

    @Override
    public void stop(Promise<Void> promise) throws Exception {
        super.stop(promise);
        mySQLPool.close();
        redisClient.close();
    }
}
