package com.dlz.mthdone.mf;


import com.dlz.mthdone.utils.IManager;

import java.lang.reflect.Method;

/**
 * 获取方法类
 */

public interface IMethodFinder extends IManager {

    /**
     * 方法获取
     * @param cls
     * @param methodName
     * @param parameters
     */
    Method findMethod(Class cls, String methodName, Object... parameters) throws Exception;


    class Factory{
        public final static int TYPE_DEFAULT = 1;

        public static IMethodFinder createMethodFinder(int type){
            return new DefaultMethodFinder();
        }

    }

}
