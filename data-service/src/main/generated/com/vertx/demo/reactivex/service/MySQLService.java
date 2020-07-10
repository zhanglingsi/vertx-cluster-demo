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
 * NOTE: This class has been automatically generated from the {@link com.vertx.demo.service.MySQLService original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(com.vertx.demo.service.MySQLService.class)
public class MySQLService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MySQLService that = (MySQLService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<MySQLService> __TYPE_ARG = new TypeArg<>(    obj -> new MySQLService((com.vertx.demo.service.MySQLService) obj),
    MySQLService::getDelegate
  );

  private final com.vertx.demo.service.MySQLService delegate;
  
  public MySQLService(com.vertx.demo.service.MySQLService delegate) {
    this.delegate = delegate;
  }

  public MySQLService(Object delegate) {
    this.delegate = (com.vertx.demo.service.MySQLService)delegate;
  }

  public com.vertx.demo.service.MySQLService getDelegate() {
    return delegate;
  }

  public com.vertx.demo.reactivex.service.MySQLService qryForJsonArray(String sql, JsonObject params, Handler<AsyncResult<JsonArray>> handler) { 
    delegate.qryForJsonArray(sql, params, handler);
    return this;
  }

  public Single<JsonArray> rxQryForJsonArray(String sql, JsonObject params) { 
    return AsyncResultSingle.toSingle(handler -> {
      qryForJsonArray(sql, params, handler);
    });
  }

  public static final String SERVICE_NAME = com.vertx.demo.service.MySQLService.SERVICE_NAME;
  public static final String SERVICE_ADDRESS = com.vertx.demo.service.MySQLService.SERVICE_ADDRESS;
  public static MySQLService newInstance(com.vertx.demo.service.MySQLService arg) {
    return arg != null ? new MySQLService(arg) : null;
  }

}
