package com.bruce.durpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bruce.durpc.core.api.LoadBalancer;
import com.bruce.durpc.core.api.Router;
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
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            Class<?> type = method.getReturnType();
            if(data instanceof JSONObject jsonResult) {
                if(Map.class.isAssignableFrom(type)){
                    Map resultMap = new HashMap();
                    Type genericReturnType = method.getGenericReturnType();
                    if(genericReturnType instanceof ParameterizedType parameterizedType){
                        Class<?> keyType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
                        Class<?> valueType = (Class<?>)parameterizedType.getActualTypeArguments()[1];
                        jsonResult.entrySet().stream().forEach(
                                e -> {
                                    Object key = TypeUtils.cast(e.getKey(),keyType);
                                    Object value = TypeUtils.cast(e.getValue(),valueType);
                                    resultMap.putIfAbsent(key,value);
                                }
                        );

                    }
                    return resultMap;
                }
                return jsonResult.toJavaObject(type);
            }else if(data instanceof JSONArray jsonArray){
                Object[] array = jsonArray.toArray();
                if(type.isArray()){
                    Class<?> componentType = type.getComponentType();
                    Object resultArray = Array.newInstance(componentType, array.length);
                    for (int i = 0; i < array.length; i++) {
                        if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                            Array.set(resultArray, i, array[i]);
                        } else {
                            Object castObject = TypeUtils.cast(array[i], componentType);
                            Array.set(resultArray, i, castObject);
                        }
                    }
                    return resultArray;
                } else if(List.class.isAssignableFrom(type)){
                    List resultList = new ArrayList<>();
                    Type genericReturnType = method.getGenericReturnType();
                    if(genericReturnType instanceof ParameterizedType parameterizedType){
                        Type actualType = parameterizedType.getActualTypeArguments()[0];
                        for (Object o : array) {
                            resultList.add(TypeUtils.cast(o, (Class<?>) actualType));
                        }
                    }else{
                        resultList.addAll(Arrays.asList(args));
                    }
                    return resultList;
                }else {
                    return null;
                }
            }else{
                return TypeUtils.cast(data,method.getReturnType());
            }
        }else {
            Exception ex = response.getEx();
//            ex.printStackTrace();
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
