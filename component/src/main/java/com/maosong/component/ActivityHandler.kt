package com.maosong.component

import android.app.Activity

/**
 *
 * @author zhouhao
 * @since 2019/04/17
 */
object ActivityHandler {
    /**
     * activity container
     */
    private val activityList = ArrayList<Activity>()

    /**
     * 添加activity
     */
    fun addActivity(activity: Activity) {
        activityList.add(activity)
    }

    /**
     * 移除activity
     */
    fun removeActivity(activity: Activity) {
        activityList.remove(activity)
    }

    /**
     * finish 所有 activity
     */
    fun finishAllActivity() {
        activityList.forEach {
            it.finish()
        }
    }

    /**
     * 退出应用程序
     * 这里关闭的是所有的 Activity，没有关闭 Activity 之外的其他组件;
     * android.os.Process.killProcess(android.os.Process.myPid())
     * 杀死进程关闭了整个应用的所有资源，有时候是不合理的，通常是用
     * 堆栈管理 Activity;System.exit(0) 杀死了整个进程，这时候活动所占的
     * 资源也会被释放,它会执行所有通过 Runtime.addShutdownHook 注册的 shutdown hooks.
     * 它能有效的释放 JVM 之外的资源,执行清除任务，运行相关的 finalizer 方法终结对象，
     * 而 finish 只是退出了 Activity。
     */
    fun AppExitWithSleep() {
        // app主线程等待3秒，让用户处理好崩溃异常后，杀死进程
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        AppExit()
    }

    /**
     * 完全退出activity
     */
    fun AppExit() {
        try {
            finishAllActivity()
            //DalvikVM的本地方法
            // 杀死该应用进程
            //            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(1)
        }

    }
}