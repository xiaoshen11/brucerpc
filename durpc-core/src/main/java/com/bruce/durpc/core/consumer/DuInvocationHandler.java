package com.bruce.durpc.core.consumer;

import com.bruce.durpc.core.api.Filter;
import com.bruce.durpc.core.api.RpcContext;
import com.bruce.durpc.core.api.RpcException;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import com.bruce.durpc.core.consumer.http.OkHttpInvoker;
import com.bruce.durpc.core.governance.SlidingTimeWindow;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.util.MethodUtils;
import com.bruce.durpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消费端动态代理处理类
 *
 * @date 2024/3/10
 */
@Slf4j
public class DuInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext context;
    List<InstanceMeta> providers;

    List<InstanceMeta> isolateProviders = new ArrayList<>();
    List<InstanceMeta> halfOpenProviders = new ArrayList<>();

    Map<String, SlidingTimeWindow> windows = new HashMap<>();

    HttpInvoker httpInvoker;

    ScheduledExecutorService executor;

    public DuInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("app.timeout","1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executor = Executors.newScheduledThreadPool(1);
        this.executor.scheduleWithFixedDelay(this::halfOpen,10,60, TimeUnit.SECONDS);
    }


    /**
     * 探活
     */
    private void halfOpen() {
        log.debug(" ====> halfOpen halfOpenProviders:" + isolateProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolateProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 处理本地方法
        if(MethodUtils.checkLocalMethod(method)){
            return null;
        }

        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethodSign(MethodUtils.methodSign(method));
        request.setArgs(args);

        int retries = Integer.parseInt(context.getParameters().getOrDefault("app.retries","2"));
        while (retries -- > 0) {
            log.info("====> retries: " + retries);
            try {
                List<Filter> filters = context.getFilters();
                for (Filter filter : filters) {
                    Object result = filter.preFilter(request);
                    if (result != null) {
                        log.info(filter.getClass().getName() + " preFilter: " + result);
                        return result;
                    }
                }

                InstanceMeta instance;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        List<InstanceMeta> instances = context.getRouter().route(this.providers);
                        instance = context.getLoadBalancer().choose(instances);
                        log.debug("loadBalancer.choose(instances) ======= {}", instance);
                    } else {
                        instance = halfOpenProviders.remove(0);
                        log.debug("check alive instance ======= {}", instance);
                    }
                }

                RpcResponse response;
                Object result;
                String url = instance.toUrl();

                try {
                    response = httpInvoker.post(request, instance.toUrl());
                    result = castResponseToResult(method, response);
                }catch (Exception e){
                    // 故障的规则统计和隔离
                    // 每一次异常，记录一次，统计30s的异常数


                    SlidingTimeWindow window = windows.get(url);
                    if(window == null){
                        window = new SlidingTimeWindow();
                        windows.put(url,window);
                    }
                    synchronized (window) {
                        window.record(System.currentTimeMillis());
                        log.debug("instance {} in window with {}", url, window.getSum());
                        // 发生了10次就做故障隔离
                        if (window.getSum() >= 10) {
                            isolate(instance);
                        }
                    }

                    throw e;
                }

                synchronized (providers) {
                    if (!providers.contains(instance)) {
                        isolateProviders.remove(instance);
                        providers.add(instance);
                        log.debug("instance {} is recovered,isolateProviders={},providers={}  ", instance, isolateProviders, providers);
                    }
                }

                for (Filter filter : filters) {
                    result = filter.postFilter(request, response, result);
                }

                return result;
            } catch (RuntimeException e) {
                if (!(e.getCause() instanceof SocketTimeoutException)) {
                    break;
                }

            }
        }
        return null;
    }

    private void isolate(InstanceMeta instance) {
        log.debug(" ==> isolate instance: " + instance);
        providers.remove(instance);
        log.debug(" ==> providers: = {}" + providers);
        isolateProviders.add(instance);
        log.debug(" ==> isolateProviders: = {}" + isolateProviders);
    }

    @Nullable
    private static Object castResponseToResult(Method method, RpcResponse response) {
        if(response.isStatus()){
            return TypeUtils.castMethodResult(method, response.getData());
        }else {
            Exception exception = response.getEx();
            if(exception instanceof RpcException ex){
                throw ex;
            }
            throw new RuntimeException(exception);
        }
    }

}
