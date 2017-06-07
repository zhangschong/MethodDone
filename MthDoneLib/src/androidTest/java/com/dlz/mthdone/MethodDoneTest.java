package com.dlz.mthdone;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.dlz.mthdone.IMethodDone.Factory.MethodDone;
import static org.junit.Assert.assertTrue;

/**
 * 测试用例
 */
@RunWith(AndroidJUnit4.class)
public class MethodDoneTest {

    private Locker mLocker = new Locker();

    @Test
    public void testMethodDone(){
        Log.e("Leon","testMethodDone");
        MethodDone.doIt(this,"testtest",1);
        mLocker.lock(10*1000);
        assertTrue(mLocker.isUnLock());
    }

    @MethodTag(threadType = IMethodDone.THREAD_TYPE_IO)
    private void testtest(int a1){
        Log.e("Leon","testtest int" + Thread.currentThread().getId());
        MethodDone.doIt(this,"test2");
    }

    @MethodTag(threadType = IMethodDone.THREAD_TYPE_MAIN, threadDelayTime = 2000)
    private void test2(){
        Log.e("Leon","test2 " + Thread.currentThread().getId());
        mLocker.unlock();
    }
}