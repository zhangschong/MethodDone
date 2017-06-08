package com.dlz.mthdone.rm;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步处理者
 */

public class FixedThreadRunnableManager implements IRunnableManager{

    private ExecutorService mFixedThreadExecutor;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private final static String TAG = FixedThreadRunnableManager.class.getName();

    @Override
    public void init() {
        mFixedThreadExecutor = Executors.newFixedThreadPool(4);
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public void recycle() {
        mFixedThreadExecutor.shutdownNow();
    }

    @Override
    public void doRunnable(Runnable runnable) {
        mFixedThreadExecutor.submit(runnable);
    }

    /**
     * 延时执行
     * @param runnable
     * @param delay
     * @throws Exception
     */
    @Override
    public void doRunnable(final Runnable runnable, int delay) throws Exception {
        mHandler.postDelayed(runnable, delay);
    }
}
