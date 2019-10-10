package com.maosong.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.util.Stack;

/**
 * Created by tory on 2018/9/21.
 */

public class AppLifeCircleUtil {


    private static Stack<Activity> activityStack;

    private static AppLifeCircleUtil instance;

    private AppLifeCircleUtil() {
    }

    /**
     * 单一实例
     */
    public static AppLifeCircleUtil getInstance() {
        if (instance == null) {
            instance = new AppLifeCircleUtil();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void pushActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }


    public int getActivitySize() {
        return activityStack.size();
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     * 并设置HomePage为栈低
     * 规则：每个activity遵照先启动再finish的
     */
    public void finishActivity(final Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !activity.isDestroyed()) {
                activity.finish();
            } else {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void appExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
