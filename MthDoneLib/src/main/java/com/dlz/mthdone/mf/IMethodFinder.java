package com.dlz.mthdone.mf;


import com.dlz.mthdone.utils.IManager;

import java.lang.reflect.Method;

/**
 * 获取方法类
 */

public interface IMethodFinder extends IManager {

    /**
     * 方法获取
     * @param clazz
     * @param methodName
     * @param params
     */
    Method findMethod(Class clazz, String methodName, Object... params) throws Exception;


    class Factory{
        public final static int TYPE_DEFAULT = 1;

        public static IMethodFinder createMethodFinder(int type){
            return new DefaultMethodFinder();
        }

    }

}
