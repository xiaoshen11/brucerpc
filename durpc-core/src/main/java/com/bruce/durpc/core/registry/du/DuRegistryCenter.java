package com.bruce.durpc.core.registry.du;

import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.meta.ServiceMeta;
import com.bruce.durpc.core.registry.ChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @date 2024/5/9
 */
@Slf4j
public class DuRegistryCenter implements RegistryCenter {

    @Value("${duregitstry.servers}")
    private String servers;

    @Override
    public void start() {
        log.info("  ======>> [DuRegistryCenter] : start with servers : {}" ,servers);

    }

    @Override
    public void stop() {
        log.info("  ======>> [DuRegistryCenter] : stop with servers : {}" ,servers);

    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info("  ======>> [DuRegistryCenter] : register instance {} for {}" ,instance, service);
        dur
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instanceMeta) {

    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        return null;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangeListener changeListener) {

    }
}
