package com.bruce.durpc.core.provider;

import com.bruce.durpc.core.annotation.DuProvider;
import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import com.bruce.durpc.core.meta.ProviderMeta;
import com.bruce.durpc.core.util.MethodUtils;
import com.bruce.durpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @date 2024/3/7
 */
@Data
public class ProviderBootstrp implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private String instance;

    @Value("${server.port}")
    private String port;

    RegistryCenter rc;

    @PostConstruct
    public void init(){
        Map<String,Object> providers = applicationContext.getBeansWithAnnotation(DuProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        providers.forEach((x,y) -> System.out.println(x));
        providers.values().forEach(x -> genInterface(x));
    }

    @SneakyThrows
    public void start(){
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + "_" + port;
        rc.start();
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop(){
        System.out.println("=====> stop");
        skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    private void registerService(String service) {
        rc.register(service,instance);
    }

    private void unregisterService(String service) {
        rc.unregister(service,instance);
    }

    private void genInterface(Object x) {
        Arrays.stream(x.getClass().getInterfaces()).forEach(
                itface -> {
                    Method[] methods = itface.getMethods();
                    for (Method method : methods) {
                        if (MethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        createProvider(itface, x, method);
                    }
                });
    }

    private void createProvider(Class<?> itface, Object x, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setServiceImpl(x);
        meta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println("create a provider: " + meta);
        skeleton.add(itface.getCanonicalName(),meta);
    }

    public RpcResponse invoke(RpcRequest request) {
        RpcResponse rpcResponse = new RpcResponse();
        List<ProviderMeta> providerMetaList = skeleton.get(request.getService());
        try {
            ProviderMeta providerMeta = findProviderMeta(providerMetaList,request.getMethodSign());
            if(providerMeta != null){
                Method method = providerMeta.getMethod();
                Object[] args = processArgs(request.getArgs(),method.getParameterTypes());
                Object result = method.invoke(providerMeta.getServiceImpl(),args);
                rpcResponse.setStatus(true);
                rpcResponse.setData(result);
            }
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if(args == null || args.length == 0){
            return args;
        }
        Object[] actualArgs = new Object[args.length];
        for (int i = 0; i < actualArgs.length; i++) {
            actualArgs[i] = TypeUtils.cast(args[i],parameterTypes[i]);
        }
        return actualArgs;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetaList, String methodSign) {
        Optional<ProviderMeta> optional = providerMetaList.stream().filter(x -> x.getMethodSign().equals(methodSign)).findFirst();
        return optional.orElse(null);
    }

}
