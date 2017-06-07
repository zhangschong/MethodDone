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
