package org.synapse.quartz.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ClassUtils {
    public static <T> Class<T> resolveGenericType(Class<?> declaredClass) {
        ParameterizedType parameterizedType = (ParameterizedType) declaredClass.getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<T>) actualTypeArguments[0];
    }

    private ClassUtils() {
    }
}
