package com.voocoo.pet.common.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型工具
 */

public class TypeUtil {
    /**
     * 获取泛型参数类型数组
     * @param cls
     * @return
     */
    public static Type[] getParameterizedType(Class cls){
        Type type = cls.getGenericSuperclass();
        if(type instanceof ParameterizedType){
            return ((ParameterizedType) type).getActualTypeArguments();
        }else{
            return null;
        }
    }
}
