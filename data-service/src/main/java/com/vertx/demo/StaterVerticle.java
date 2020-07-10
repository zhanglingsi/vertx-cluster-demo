package com.vertx.demo;

import com.vertx.demo.provider.DataProviderVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

/**
 * Created by zhangls on 2020/7/3.
 *
 * @author zhangls
 */
public class StaterVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> promise) throws Exception {
        vertx.deployVerticle(DataProviderVerticle.class.getName(), new DeploymentOptions().setConfig(config()));
    }
}
