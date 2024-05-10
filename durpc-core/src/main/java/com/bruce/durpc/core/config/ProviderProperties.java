package com.bruce.durpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @date 2024/5/10
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "durpc.provider")
public class ProviderProperties {

    // for provider

    Map<String, String> metas = new HashMap<>();

}
