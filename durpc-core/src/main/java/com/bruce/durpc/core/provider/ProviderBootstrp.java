package com.bruce.durpc.core.provider;

import com.bruce.durpc.core.annotation.DuProvider;
import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.meta.ProviderMeta;
import com.bruce.durpc.core.meta.ServiceMeta;
import com.bruce.durpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * @date 2024/3/7
 */
@Data
public class ProviderBootstrp implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private InstanceMeta instanceMeta;

    @Value("${server.port}")
    private String port;

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    RegistryCenter rc;

    @PostConstruct
    public void init(){
        Map<String,Object> providers = applicationContext.getBeansWithAnnotation(DuProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        providers.forEach((x,y) -> System.out.println(x));
        providers.values().forEach(this::genInterface);
    }

    @SneakyThrows
    public void start(){
        String ip = InetAddress.getLocalHost().getHostAddress();
        instanceMeta = InstanceMeta.http(ip,Integer.valueOf(port));
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
        ServiceMeta serviceMeta = ServiceMeta.builder().app(app).env(env).namespace(namespace).name(service).build();
        rc.register(serviceMeta, instanceMeta);
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder().app(app).env(env).namespace(namespace).name(service).build();
        rc.unregister(serviceMeta, instanceMeta);
    }

    private void genInterface(Object impl) {
        Arrays.stream(impl.getClass().getInterfaces()).forEach(
                service -> {
                    Method[] methods = service.getMethods();
                    for (Method method : methods) {
                        if (MethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        createProvider(service, impl, method);
                    }
                });
    }

    private void createProvider(Class<?> service, Object impl, Method method) {
        ProviderMeta meta = ProviderMeta.builder().method(method)
                .serviceImpl(impl).methodSign(MethodUtils.methodSign(method)).build();
        System.out.println("create a provider: " + meta);
        skeleton.add(service.getCanonicalName(),meta);
    }

}
