package com.bruce.durpc.core.meta;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 描述provider参数
 *
 * @date 2024/3/13
 */
@Data
@Builder
public class ProviderMeta {

    Method method;

    String methodSign;

    Object serviceImpl;

}
