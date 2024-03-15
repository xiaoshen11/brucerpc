package com.bruce.durpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @date 2024/3/13
 */
@Data
public class ProviderMeta {

    Method method;

    String methodSign;

    Object serviceImpl;

}
