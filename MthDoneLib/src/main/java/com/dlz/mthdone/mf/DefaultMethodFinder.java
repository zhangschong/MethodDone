package com.dlz.mthdone.mf;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的{@link IMethodFinder}
 */

public class DefaultMethodFinder implements IMethodFinder {
    /*
     * In newer class files, compilers may add methods. Those are called bridge or synthetic methods.
     * EventBus must ignore both. There modifiers are not public but defined in the Java class file format:
     * http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1
     */
    private static final int BRIDGE = 0x40;
    private static final int SYNTHETIC = 0x1000;

    /**
     * 需要忽略掉的方法
     */
    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT
            | Modifier.STATIC
            | Modifier.NATIVE
            | BRIDGE
            | SYNTHETIC;

    /**
     * 需要忽略掉的类
     */
    private static final String[] IGNORE_CLASSES;

    /**
     * 缓存解析到的方法
     */
    private static final Map<Class<?>, List<MethodInfo>> METHOD_CACHE = new ConcurrentHashMap<>();

    static {
        IGNORE_CLASSES = new String[]{
                "java.",
                "javax.",
                "android."
        };
    }

    @Override
    public void init() {

    }

    @Override
    public void recycle() {

    }

    @Override
    public Method findMethod(Class clazz, String methodName, Object... params) throws Exception {
        Class[] paramsTypes = null;

        //生成参数类型数组，主要是为了检查是否有基本类型
        if (params != null) {
            paramsTypes = new Class[params.length];
            for (int i = 0, j = params.length; i < j; i++) {
                paramsTypes[i] = params[i].getClass();
            }
        }

        Method method;
        try {
            //首先尝试获取当前类所声明或重写的方法，包含non-public
            method = clazz.getDeclaredMethod(methodName, paramsTypes);
        } catch (NoSuchMethodException e) {
            try {
                //否则就尝试获取父类的public方法
                method = clazz.getMethod(methodName, paramsTypes);
            } catch (NoSuchMethodException e1) {
                // 如果都获取失败，就尝试获取non-public的方法或重载或重写的方法
                method = loadMethodFromCache(clazz, methodName, paramsTypes);
            }
        }

        return method;
    }

    /**
     * 尝试获取non-public的方法或重载或重写的方法
     *
     * @param clazz 待解析的类型
     * @param methodName 方法名
     * @param paramsTypes 方法参数类型数组
     * @return 如果能找到匹配的方法就返回，否则返回null
     */
    private static Method loadMethodFromCache(Class clazz, String methodName, Class[] paramsTypes) {

        List<MethodInfo> methodInfos = METHOD_CACHE.get(clazz);
        if (methodInfos == null) {
            methodInfos = parseMethod(clazz);
            if (methodInfos != null && !methodInfos.isEmpty()) {
                METHOD_CACHE.put(clazz, methodInfos);
            } else {
                return null;
            }
        }

        List<MethodInfo> filteredMethods = new ArrayList<>();
        /*
         * 1. 过滤出方法名相同并且参数个数一样的方法
         *    MethodInfo.paramsCount == paramsTypes.length
         *        && MethodInfo.methodName == methodName
         */
        int paramsCount = 0;
        if (paramsTypes != null) {
            paramsCount = paramsTypes.length;
        }

        for (MethodInfo methodInfo : methodInfos) {
            if (TextUtils.equals(methodName, methodInfo.methodName)
                    && paramsCount == methodInfo.paramsCount) {
                filteredMethods.add(methodInfo);
            }
        }

        //2. 然后挨个对比参数
        for (MethodInfo methodInfo : filteredMethods) {
            if (checkParams(paramsTypes, methodInfo.paramsTypes)) {
                return methodInfo.method;
            }
        }
        return null;
    }

    /**
     * 挨个对比参数，如果能找到对应位置参数类型的父类型或对应位置基本类型的包装类型也认为找到了方法
     *
     * @param paramsRequest 请求的参数类型
     * @param paramsCache 缓存的方法参数类型
     * @return 如果都能匹配，就认为这些参数都匹配
     */
    private static boolean checkParams(Class[] paramsRequest, Class[] paramsCache) {
        for (int i = 0, count = paramsCache.length; i < count; i++) {
            Class requestType = paramsRequest[i];
            Class cacheType = paramsCache[i];
            //如果是基本类型，就转换成对应的包装类型
            if (cacheType.isPrimitive()) {
                cacheType = convertPrimitive(cacheType);
            }
            if (requestType.isPrimitive()) {
                requestType = convertPrimitive(requestType);
            }
            if (!cacheType.isAssignableFrom(requestType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 转换基本类型为对应的包装类型
     * @param clazz
     * @return
     */
    private static Class convertPrimitive(Class clazz) {
        if (clazz == byte.class) {
            return Byte.class;
        } else if (clazz == char.class) {
            return Character.class;
        } else if (clazz == int.class) {
            return Integer.class;
        } else if (clazz == long.class) {
            return Long.class;
        } else if (clazz == float.class) {
            return Float.class;
        } else if (clazz == double.class) {
            return Double.class;
        } else if (clazz == boolean.class) {
            return Boolean.class;
        }

        return null;
    }

    /**
     * 解析所给的类型及其父类的所有方法
     * @param clazz
     * @return
     */
    @SuppressLint("WrongConstant")
    private static List<MethodInfo> parseMethod(Class<?> clazz) {

        List<MethodInfo> methodInfos = new ArrayList<>();

        while (clazz != null && !shouldIgnore(clazz)) {
            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                //过滤掉需要忽略的方法
                if ((method.getModifiers() & MODIFIERS_IGNORE) != 0) {
                    continue;
                }

                MethodInfo methodInfo = new MethodInfo();
                methodInfo.method = method;
                methodInfo.methodName = method.getName();
                methodInfo.declareClass = clazz;
                methodInfo.paramsTypes = method.getParameterTypes();
                if (methodInfo.paramsTypes != null) {
                    methodInfo.paramsCount = methodInfo.paramsTypes.length;
                }

                methodInfos.add(methodInfo);
            }
            clazz = clazz.getSuperclass();
        }

        return methodInfos;
    }

    /**
     * 是否需要过滤掉这个class
     * @param clazz
     * @return
     */
    private static boolean shouldIgnore(Class<?> clazz) {
        final String[] ignoreClasses = IGNORE_CLASSES;
        String className = clazz.getName();
        for (int i = 0, j = ignoreClasses.length; i < j; i++) {
            if (className.startsWith(ignoreClasses[i])) {
                return true;
            }
        }

        return false;
    }
}
