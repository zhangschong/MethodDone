package com.dlz.mthdone.rm;

import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 默认的{@link IRunnableManager}
 */

class MainThreadRunnableManager implements IRunnableManager{

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Thread mMainThread = Looper.getMainLooper().getThread();

    @Override
    public void init() {
    }

    @Override
    public void recycle() {

    }

    @Override
    public void doRunnable(Runnable runnable) {
        if(Thread.currentThread() != mMainThread){
            mMainHandler.post(runnable);
        }else{
            runnable.run();
        }
    }

    @Override
    public void doRunnable(final Runnable runnable, int delay) throws Exception {
        if(Thread.currentThread() != mMainThread){
            mMainHandler.postDelayed(runnable, delay);
        }else{
            runnable.run();
        }
    }
}
