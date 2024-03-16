package com.bruce.durpc.core.api;

import java.util.List;

/**
 * @date 2024/3/16
 */
public interface RegistryCenter {

    void start();

    void stop();

    // provider
    void register(String service, String instance);

    void unregister(String service, String instance);

    // consumer
    List<String> fetchAll(String serviceName);

//    void subscribe();

    class StaicRegistryCenter implements RegistryCenter {

        List<String> providers;

        public StaicRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String serviceName) {
            return providers;
        }
    }

}
