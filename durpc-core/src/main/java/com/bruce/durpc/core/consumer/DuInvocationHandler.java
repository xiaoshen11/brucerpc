package com.bruce.durpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.bruce.durpc.core.api.RpcContext;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import com.bruce.durpc.core.util.MethodUtils;
import com.bruce.durpc.core.util.TypeUtils;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @date 2024/3/10
 */
public class DuInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext context;
    List<String> providers;

    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    public DuInvocationHandler(Class<?> service, RpcContext context, List<String> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
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

        List<String> urls = context.getRouter().route(this.providers);
        String url = (String) context.getLoadBalancer().choose(urls);
        System.out.println("loadBalancer.choose(urls) ======= " + url);

        RpcResponse response = post(request, url);
        if(response.isStatus()){
            Object data = response.getData();
            return TypeUtils.castMethodResult(method, args, data);
        }else {
            Exception ex = response.getEx();
            throw new RuntimeException(ex);
        }
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16,60, TimeUnit.SECONDS))
            .readTimeout(1,TimeUnit.SECONDS)
            .writeTimeout(1,TimeUnit.SECONDS)
            .connectTimeout(1,TimeUnit.SECONDS)
            .build();

    public RpcResponse post(RpcRequest rpcRequest,String url){
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.println("reqJson ====== " + reqJson);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson,JSONTYPE))
                .build();
        String respJson = null;
        try {
            respJson = client.newCall(request).execute().body().string();
            System.out.println("respJson ====== " + respJson);
            RpcResponse response = JSON.parseObject(respJson,RpcResponse.class);
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
