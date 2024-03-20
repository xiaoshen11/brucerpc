package com.bruce.durpc.core.api;

import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.registry.ChangeListener;

import java.util.List;

/**
 * @date 2024/3/16
 */
public interface RegistryCenter {

    void start(); // provider/consumer

    void stop(); // provider/consumer

    // provider
    void register(String service, InstanceMeta instanceMeta);

    void unregister(String service, InstanceMeta instanceMeta);

    // consumer
    List<InstanceMeta> fetchAll(String serviceName);

    void subscribe(String service, ChangeListener changeListener);

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
        public void register(String service, InstanceMeta instanceMeta) {

        }

        @Override
        public void unregister(String service, InstanceMeta instanceMeta) {

        }

        @Override
        public List<InstanceMeta> fetchAll(String serviceName) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangeListener changeListener) {

        }
    }

}
