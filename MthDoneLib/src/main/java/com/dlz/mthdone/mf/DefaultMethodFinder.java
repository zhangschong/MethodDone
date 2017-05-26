package com.dlz.mthdone.mf;

import java.lang.reflect.Method;
import java.util.HashMap;

import static com.dlz.mthdone.utils.ReflectUtils.getClassesFromInstances;

/**
 * 默认的{@link IMethodFinder}
 */

public class DefaultMethodFinder implements IMethodFinder {

    private HashMap<Class, ClsMthNode> mClsMthNodes = new HashMap<>(200);


    @Override
    public void init() {

    }

    @Override
    public void recycle() {

    }

    @Override
    public Method findMethod(Class cls, String methodName, Object... parameters) throws Exception {
        ClsMthNode node = mClsMthNodes.get(cls);
        if (null == node) {
            synchronized (mClsMthNodes) {
                node = mClsMthNodes.get(cls);
                if (null == node) {
                    node = new ClsMthNode();
                    mClsMthNodes.put(cls, node);
                }
            }
        }
        return node.findMethod(cls, methodName, parameters);
    }

    private class ClsMthNode {

        private HashMap<String, MthNode> mMthNodes = new HashMap<>(20);

        public Method findMethod(Class cls, String methodName, Object... parameters) throws Exception {
            MthNode node = mMthNodes.get(methodName);
            if (null == node) {
                synchronized (mMthNodes) {
                    node = mMthNodes.get(methodName);
                    if (null == node) {
                        node = new MthNode();
                        mMthNodes.put(methodName, node);
                    }
                }
            }
            return node.findMethod(cls, methodName, parameters);
        }
    }

    private class MthNode {

        private Method mMth;

        public Method findMethod(Class cls, String methodName, Object... parameters) throws Exception {
            if (null == mMth) {
                synchronized (this) {
                    if (null == mMth) {
                        mMth = findMethodFromCls(cls, methodName, parameters);
                    }
                }
            }
            return mMth;
        }
    }

    private static Method findMethodFromCls(Class cls, String methodName, Object... parameters) throws NoSuchMethodException {
        return cls.getDeclaredMethod(methodName, getClassesFromInstances(parameters));
    }
}
