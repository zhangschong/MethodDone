package com.dlz.mthdone;

import android.support.annotation.NonNull;

import com.dlz.mthdone.utils.IManager;

import static com.dlz.mthdone.rm.IRunnableManager.Factory.TYPE_IO;
import static com.dlz.mthdone.rm.IRunnableManager.Factory.TYPE_MAIN;
import static com.dlz.mthdone.rm.IRunnableManager.Factory.TYPE_THREAD;


/**
 * 方法执行者
 */

public interface IMethodDone extends IManager {

    /** 主线程 */
    int THREAD_TYPE_MAIN = TYPE_MAIN;
    /** 异步数据流处理 */
    int THREAD_TYPE_IO = TYPE_IO;
    /** 异步线程处理 */
    int THREAD_TYPE_THREAD = TYPE_THREAD;


    /**
     * 执行方法
     *
     * @param instance   执行的实例
     * @param mthTag     方法标签，默认为方法名，暂不支持其它标签
     * @param parameters 执行参数实例,可以不添加
     */
    void doIt(@NonNull Object instance, @NonNull String mthTag, Object... parameters);

    class Factory {
        public static final IMethodDone MethodDone = new MethodDone();
        static {
            MethodDone.init();
        }
    }
}
