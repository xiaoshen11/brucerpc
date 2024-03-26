package com.bruce.durpc.core.api;

import java.util.List;

/**
 * 路由
 * @date 2024/3/16
 */
public interface Router<T> {

    List<T> route(List<T> providers);

    Router Default = p -> p;
}
