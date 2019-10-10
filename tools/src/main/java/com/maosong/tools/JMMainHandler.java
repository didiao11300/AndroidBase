package com.maosong.tools;

import android.os.Looper;

/**
 * Created by tory on 2018/5/16.
 */

public class JMMainHandler<T> extends JMHandler {
    private volatile static JMMainHandler sInstance;

    public static JMMainHandler getInstance() {
        if (null == sInstance) {
            synchronized (JMMainHandler.class) {
                if (null == sInstance) {
                    sInstance = new JMMainHandler();
                }
            }
        }
        return sInstance;
    }

    public <T> JMMainHandler() {
        this(null);
    }

    //采用弱引用的方式耦合父类
    public <T> JMMainHandler(T t) {
        super(Looper.getMainLooper(), t);
    }
}
