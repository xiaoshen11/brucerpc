package com.bruce.durpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @date 2024/5/10
 */
@Data
@ConfigurationProperties(prefix = "durpc.app")
public class AppProperties {

    // for app instance
    private String id = "app1";

    private String namespace = "public";

    private String env = "dev";

}
