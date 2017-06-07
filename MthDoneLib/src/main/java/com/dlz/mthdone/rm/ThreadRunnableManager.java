package com.dlz.mthdone.rm;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 异步线程处理
 */

public class ThreadRunnableManager implements IRunnableManager {
    private final static String TAG = ThreadRunnableManager.class.getName();

    private Handler mHandler;
    private HandlerThread mThread;

    @Override
    public void init() {
        mThread = new HandlerThread(TAG);
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    @Override
    public void recycle() {
        mThread.quitSafely();
    }

    @Override
    public void doRunnable(Runnable runnable) {
        if (Thread.currentThread() != mThread) {
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public void doRunnable(final Runnable runnable, long delay) throws Exception {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                doRunnable(runnable);
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, delay);
    }
}
