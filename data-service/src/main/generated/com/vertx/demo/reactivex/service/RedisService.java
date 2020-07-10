/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.vertx.demo.reactivex.service;

import io.reactivex.Observable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.vertx.reactivex.RxHelper;
import io.vertx.reactivex.ObservableHelper;
import io.vertx.reactivex.FlowableHelper;
import io.vertx.reactivex.impl.AsyncResultMaybe;
import io.vertx.reactivex.impl.AsyncResultSingle;
import io.vertx.reactivex.impl.AsyncResultCompletable;
import io.vertx.reactivex.WriteStreamObserver;
import io.vertx.reactivex.WriteStreamSubscriber;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;
import io.vertx.lang.rx.MappingIterator;

/**
 * Created by zhangls on 2020/7/6.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link com.vertx.demo.service.RedisService original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(com.vertx.demo.service.RedisService.class)
public class RedisService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RedisService that = (RedisService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<RedisService> __TYPE_ARG = new TypeArg<>(    obj -> new RedisService((com.vertx.demo.service.RedisService) obj),
    RedisService::getDelegate
  );

  private final com.vertx.demo.service.RedisService delegate;
  
  public RedisService(com.vertx.demo.service.RedisService delegate) {
    this.delegate = delegate;
  }

  public RedisService(Object delegate) {
    this.delegate = (com.vertx.demo.service.RedisService)delegate;
  }

  public com.vertx.demo.service.RedisService getDelegate() {
    return delegate;
  }

  /**
   * 执行Redis命令
   * @param cname 命令名称枚举
   * @param params 格式{"key":"设置的键值","val":"设置的值","field":"MAP中的KEY"}
   * @param handler 异步处理
   * @return 
   */
  public com.vertx.demo.reactivex.service.RedisService execComm(com.vertx.demo.utils.RedisCommand cname, JsonObject params, Handler<AsyncResult<String>> handler) { 
    delegate.execComm(cname, params, handler);
    return this;
  }

  /**
   * 执行Redis命令
   * @param cname 命令名称枚举
   * @param params 格式{"key":"设置的键值","val":"设置的值","field":"MAP中的KEY"}
   * @return 
   */
  public Single<String> rxExecComm(com.vertx.demo.utils.RedisCommand cname, JsonObject params) { 
    return AsyncResultSingle.toSingle(handler -> {
      execComm(cname, params, handler);
    });
  }

  public static final String SERVICE_NAME = com.vertx.demo.service.RedisService.SERVICE_NAME;
  public static final String SERVICE_ADDRESS = com.vertx.demo.service.RedisService.SERVICE_ADDRESS;
  public static RedisService newInstance(com.vertx.demo.service.RedisService arg) {
    return arg != null ? new RedisService(arg) : null;
  }

}
