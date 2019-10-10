package com.maosong.tools;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by tory on 2018/7/4.
 */

public class JMWorkHandler extends Handler {
    public static final String TAG = JMWorkHandler.class.getSimpleName();

    //lazy load for memory
    private static JMWorkHandler sInstance;
    //因为使用不频繁，暂时不用DLC检查和volatile修饰
    public static JMWorkHandler getInstance() {
        if (null == sInstance) {
            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            sInstance = new JMWorkHandler(handlerThread.getLooper());
        }
        return sInstance;
    }

    private JMWorkHandler(Looper looper) {
        super(looper);
    }
}
