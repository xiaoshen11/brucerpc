package com.bruce.durpc.core.registry.du;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.consumer.HttpInvoker;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.meta.ServiceMeta;
import com.bruce.durpc.core.registry.ChangeListener;
import com.bruce.durpc.core.registry.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2024/5/9
 */
@Slf4j
public class DuRegistryCenter implements RegistryCenter {

    private static final String REG_PATH = "/reg";
    private static final String UNREG_PATH = "/unreg";
    private static final String FINDALL_PATH = "/findAll";
    private static final String VERSION_PATH = "/version";
    private static final String RENEWS_PATH = "/renews";

    @Value("${duregistry.servers}")
    private String servers;

    Map<String, Long> VERSIONS = new HashMap<>();
    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();
    DuHealthChecker healthChecker = new DuHealthChecker();


    @Override
    public void start() {
        log.info("  ======>> [DuRegistry] : start with servers : {}" ,servers);
        healthChecker.start();
        providerCheck();
    }

    private void providerCheck() {
        healthChecker.providerCheck(() -> {
            RENEWS.keySet().stream().forEach(instance -> {
                Long timestamp = HttpInvoker.httpPost(JSON.toJSONString(instance),renewsPath(RENEWS.get(instance)),Long.class);
                log.info(" =======>>>> [DuRegistry] renew instance {} at {}", instance, timestamp );
            });
        });
    }

    @Override
    public void stop() {
        log.info("  ======>> [DuRegistry] : stop with servers : {}" ,servers);
        healthChecker.stop();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info("  ======>> [DuRegistry] : register instance {} for {}" ,instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance),regPath(service), InstanceMeta.class);
        log.info(" ====>>>> [DuRegistry] : registered {}", instance);
        RENEWS.add(instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info("  ======>> [DuRegistry] : unregister instance {} for {}" ,instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance),unregPath(service), InstanceMeta.class);
        log.info(" ====>>>> [DuRegistry] : unregister {}", instance);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>>> [DuRegistry] : find all instances for {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(findAllPath(service), new TypeReference<List<InstanceMeta>>(){});
        log.info(" ====>>>> [DuRegistry] : findAll = {}", instances);
        return instances;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        healthChecker.consumerCheck(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(versionPath(service),Long.class);
            log.info(" ====>>>> [DuRegistry] : version = {}, newVersion = {}", version, newVersion);
            if(newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        });
    }

    private String regPath(ServiceMeta service) {
        return path(REG_PATH, service);
    }
    private String unregPath(ServiceMeta service) {
        return path(UNREG_PATH, service);
    }
    private String findAllPath(ServiceMeta service) {
        return path(FINDALL_PATH, service);
    }
    private String versionPath(ServiceMeta service) {
        return path(VERSION_PATH, service);
    }
    private String path(String context, ServiceMeta service) {
        return servers + context + "?service=" + service.toPath();
    }

    private String renewsPath(List<ServiceMeta> serviceList) {
        return path(RENEWS_PATH, serviceList);
    }

    private String path(String context, List<ServiceMeta> serviceList) {
        StringBuffer sb = new StringBuffer();
        for (ServiceMeta service : serviceList) {
            sb.append(service.toPath()).append(",");
        }
        String services = sb.toString();
        if(services.endsWith(",")) services = services.substring(0, services.length() - 1);
        log.info(" ====>>>> [DuRegistry] : renew instance for {}", services);
        return servers + context + "?services=" + services;
    }
}
