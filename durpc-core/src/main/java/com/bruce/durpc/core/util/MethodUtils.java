package com.bruce.durpc.core.util;

/**
 * @date 2024/3/11
 */
public class MethodUtils {

    public static boolean checkLocalMethod(final String method) {
        //本地方法不代理
        if ("toString".equals(method) ||
                "hashCode".equals(method) ||
                "notifyAll".equals(method) ||
                "equals".equals(method) ||
                "wait".equals(method) ||
                "getClass".equals(method) ||
                "notify".equals(method)) {
            return true;
        }
        return false;
    }
}
