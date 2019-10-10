package com.maosong.tools;

import android.content.Context;

/**
 * create by colin 2018/9/30
 */
public class ToolsApp {

    public static Context mApp;

    public static void initContext(Context context) {
        mApp = context;
    }

    public static Context getAppContext() {
        return mApp.getApplicationContext();
    }
}
