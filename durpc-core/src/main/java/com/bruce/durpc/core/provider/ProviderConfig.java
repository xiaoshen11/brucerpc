package com.bruce.durpc.core.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @date 2024/3/7
 */
@Configuration
public class ProviderConfig {

    @Bean
    ProviderBootstrp providerBootstrp(){
        return new ProviderBootstrp();
    }
}
