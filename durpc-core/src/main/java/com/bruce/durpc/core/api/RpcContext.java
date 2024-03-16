package com.bruce.durpc.core.api;

import lombok.Data;

import java.util.List;

/**
 * @date 2024/3/16
 */
@Data
public class RpcContext {

    List<Filter> filters; //TODO

    Router router;

    LoadBalancer loadBalancer;

}
