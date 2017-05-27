package com.dlz.mthdone;

import android.support.annotation.NonNull;

import com.dlz.mthdone.mf.IMethodFinder;
import com.dlz.mthdone.rm.IRunnableManager;
import com.dlz.mthdone.utils.ResetNodeManager;

import java.lang.reflect.Method;

/**
 * {@link IMethodDone}默认实例对象
 */

class MethodDone implements IMethodDone {
    private final static String TAG = MethodDone.class.getSimpleName();

    private IMethodFinder mMethodFinder;

    private IRunnableManager[] mRunnableManagers;

    @Override
    public void init() {
        mRunnableManagers = new IRunnableManager[]{IRunnableManager.Factory.createRunnableManager(THREAD_TYPE_MAIN),
                IRunnableManager.Factory.createRunnableManager(THREAD_TYPE_IO),
                IRunnableManager.Factory.createRunnableManager(THREAD_TYPE_THREAD)};

        for (IRunnableManager manager : mRunnableManagers) {
            manager.init();
        }

        mMethodFinder = IMethodFinder.Factory.createMethodFinder(IMethodFinder.Factory.TYPE_DEFAULT);
        mMethodFinder.init();
    }

    @Override
    public void recycle() {
        mMethodFinder.recycle();
        for (IRunnableManager manager : mRunnableManagers) {
            manager.recycle();
        }
    }

    /**
     * 当出现异常错误时,回调此方法
     *
     * @param instance
     * @param mth
     * @param throwable
     */
    protected void err(Object instance, String mth, Throwable throwable) {
        try {
            doItWithException(instance, CALL_BACK_ERR,new Error(instance, mth, throwable));
        } catch (Exception e) {
            //没有错误回调方法则打印
            throwable.printStackTrace();
        }
    }

    @Override
    public void doIt(Object instance, String mthTag, Object... parameters) {
        try {
            doItWithException(instance, mthTag, parameters);
        } catch (Exception e) {
            err(instance, mthTag, e);
        }
    }

    @Override
    public void doItWithException(@NonNull Object instance, @NonNull final String mthTag, Object... parameters) throws Exception {
        Class cls;
        if (instance instanceof Class) {//如果为Class对像,则为静态方法请求
            cls = (Class) instance;
            instance = null;
        } else {
            cls = instance.getClass();
        }
        if (null == parameters) {//如果为空,则必然有一个参数,如果没有参数，应为空数组
            parameters = new Object[]{null};
        }
        Method method = mMethodFinder.findMethod(cls, mthTag, parameters);
        doMethodInner(instance, method, parameters);
    }

    private void doMethodInner(@NonNull Object instance, Method method, Object... parameters) {
        try {
            int threadType = THREAD_TYPE_MAIN;
            MethodTag tag = method.getAnnotation(MethodTag.class);
            if (null != tag) {
                threadType = tag.threadType();
            }
            mRunnableManagers[threadType].doRunnable(mNodeManager.pullT().setData(instance, method, parameters));
        } catch (Exception e) {
            err(instance, method.getName(), e);
        }
    }

    private ResetNodeManager<RunNode> mNodeManager = new ResetNodeManager<RunNode>() {
        @Override
        protected RunNode createNode() {
            return new RunNode();
        }
    };


    /**
     * 执行的Node元素
     */
    private class RunNode implements Runnable, ResetNodeManager.IResetNode {

        private Method mMethod;
        private Object mInstance;
        private Object[] mParameters;

        RunNode setData(Object instance, Method method, Object... parameters) {
            mMethod = method;
            mInstance = instance;
            mParameters = parameters;
            return this;
        }


        @Override
        public void run() {
            try {
                if (mMethod.isAccessible()) {
                    mMethod.invoke(mInstance, mParameters);
                } else {
                    mMethod.setAccessible(true);
                    mMethod.invoke(mInstance, mParameters);
                    mMethod.setAccessible(false);
                }
            } catch (Exception e) {
                err(mInstance, mMethod.getName(), e);
            } finally {
                mNodeManager.resetNode(this);
            }
        }

        @Override
        public void reset() {
            setData(null, null);
        }
    }
}
