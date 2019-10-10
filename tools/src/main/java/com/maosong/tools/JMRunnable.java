package com.maosong.tools;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by tory on 2018/5/16.
 */

public abstract class JMRunnable<T> implements Runnable {
    protected WeakReference<T> weak;

    public JMRunnable() {
        //解除与this$0的引用关系
        try {
            Field f = getClass().getDeclaredField("this$0");
            f.setAccessible(true);
            f.set(this, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public JMRunnable(T t) {
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
