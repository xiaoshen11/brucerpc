package com.bruce.durpc.core.api;

import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.meta.ServiceMeta;
import com.bruce.durpc.core.registry.ChangeListener;

import java.util.List;

/**
 * @date 2024/3/16
 */
public interface RegistryCenter {

    void start(); // provider/consumer

    void stop(); // provider/consumer

    // provider
    void register(ServiceMeta service, InstanceMeta instanceMeta);

    void unregister(ServiceMeta service, InstanceMeta instanceMeta);

    // consumer
    List<InstanceMeta> fetchAll(ServiceMeta service);

    void subscribe(ServiceMeta service, ChangeListener changeListener);

    class StaicRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StaicRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instanceMeta) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instanceMeta) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangeListener changeListener) {

        }
    }

}
