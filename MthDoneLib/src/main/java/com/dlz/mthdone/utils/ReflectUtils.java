package com.dlz.mthdone.utils;

/**
 * 反射工具类
 */

public class ReflectUtils {

    /**
     * 通过实例，获取当前的实例对像
     * @param instances 不能为空
     * @return 实例的class 对像
     */
    public static Class[] getClassesFromInstances(Object... instances){
        Class[] clses = new Class[instances.length];
        for (int i = instances.length -1; i >=0 ;i--){
            clses[i] = instances[i].getClass();
        }
        return clses;
    }

}
