package com.bruce.durpc.core.util;

import com.bruce.durpc.core.annotation.DuConsumer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static boolean checkLocalMethod(final Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    public static String methodSign(Method method){
        StringBuilder sb = new StringBuilder(method.getName());
        sb.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(
                p -> sb.append("_").append(p.getCanonicalName())
        );
        return sb.toString();
    }

    public static void main(String[] args) {
        Arrays.stream(MethodUtils.class.getMethods()).forEach(
                m -> System.out.println(MethodUtils.methodSign(m))
        );
    }

    public static List<Field> findAnnotatedField(Class<?> aClass,Class clazz) {
        List<Field> result = new ArrayList<>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(clazz)) {
                    result.add(f);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }

}
