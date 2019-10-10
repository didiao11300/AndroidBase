package com.maosong.tools;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by tory on 2018/5/16.
 */

public class JMHandler<T> extends Handler {
    protected WeakReference<T> weak;

    public JMHandler(Looper looper, T t) {
        super(looper);
        //解除与this$0的引用关系
        try {
            Field f = getClass().getDeclaredField("this$0");
            f.setAccessible(true);
            f.set(this, null);
        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        }
        if (null != t) {
            weak = new WeakReference<T>(t);
        }
    }

    @Nullable
    public T obtain() {
        if (null != weak) {
            return weak.get();
        }
        return null;
    }
}
