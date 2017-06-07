package com.dlz.mthdone.rm;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步处理者
 */

public class FixedThreadRunnableManager implements IRunnableManager{

    private ExecutorService mFixedThreadExecutor;

    @Override
    public void init() {
        mFixedThreadExecutor = Executors.newFixedThreadPool(4);
    }

    @Override
    public void recycle() {
        mFixedThreadExecutor.shutdownNow();
    }

    @Override
    public void doRunnable(Runnable runnable) throws Exception {
        mFixedThreadExecutor.submit(runnable);
    }

    /**
     * 延时执行
     * @param runnable
     * @param delay
     * @throws Exception
     */
    @Override
    public void doRunnable(final Runnable runnable, long delay) throws Exception {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mFixedThreadExecutor.submit(runnable);
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, delay);
    }
}
