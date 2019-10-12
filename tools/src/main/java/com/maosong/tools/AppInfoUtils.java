package com.maosong.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import com.maosong.tools.LogUtil;
import com.maosong.tools.ToastUtils;

import java.io.*;
import java.util.Collection;
import java.util.List;

/**
 * 获取appinfo的相关信息
 */
public class AppInfoUtils {
    private AppInfoUtils() {
    }

    public static boolean isMainProcess(Context context) {
        if (context == null) {
            return false;
        }

        String packageName = context.getApplicationContext().getPackageName();
        String processName = getProcessName(context);
        return packageName.equals(processName);
    }

    /**
     * 检查app是否安装
     * **/
    public static boolean checkAppInstalled(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;//true为安装了，false为未安装
        }
    }

    public static String getProcessName(Context context) {
        String processName = getProcessFromFile();
        if (processName == null) {
            // 如果装了xposed一类的框架，上面可能会拿不到，回到遍历迭代的方式
            processName = getProcessNameByAM(context);
        }
        return processName;
    }

    private static String getProcessFromFile() {
        BufferedReader reader = null;
        try {
            int pid = android.os.Process.myPid();
            String file = "/proc/" + pid + "/cmdline";
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "iso-8859-1"));
            int c;
            StringBuilder processName = new StringBuilder();
            while ((c = reader.read()) > 0) {
                processName.append((char) c);
            }
            return processName.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getProcessNameByAM(Context context) {
        String processName = null;

        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (am == null) {
            return null;
        }

        while (true) {
            List<ActivityManager.RunningAppProcessInfo> plist = am.getRunningAppProcesses();
            if (plist != null) {
                for (ActivityManager.RunningAppProcessInfo info : plist) {
                    if (info.pid == android.os.Process.myPid()) {
                        processName = info.processName;

                        break;
                    }
                }
            }

            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            try {
                // take a rest and again
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean isMainProcessLive(Context context) {
        if (context == null) {
            return false;
        }

        final String processName = context.getPackageName();
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> plist = am.getRunningAppProcesses();
            if (plist != null) {
                for (ActivityManager.RunningAppProcessInfo info : plist) {
                    if (info.processName.equals(processName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public void test() throws Exception {
        File srcFile = new File("file/src.txt");
        //2 创建文件字节输入流对象，并接在源上
        InputStream in = new FileInputStream(srcFile);
        //3 IO操作（具体的读取操作）
        /*
         * 如果读到最后没有数据。则返回-1
         * int read() 读取一个字节，并返回读取的字节
         * int read(byte[] b) 读取多个字节，并存储到数组b中，从数组b的索引为0的地方开始存储，返回读取了几个字节
         * int read(byte[] b,int off,int len)读取len个字节，从数组b的索引为off的地方开始存储
         */
        //int data = in.read();获取src.txt 文件中第一个字节
        byte[] buffer = new byte[5];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            String str = new String(buffer, 0, len);
            System.out.println(str);
        }
        //4 关闭文件
    }

    public static String formatInt(int count) {
        if (count < 1000) {
            return new Integer(count).toString();
        } else if (count < 1000000) {
            float fount = count / 1000.0f;
            return String.format("%.1fK", fount);
        } else {
            float fount = count / 1000000.0f;
            return String.format("%.1fM", fount);
        }
    }

    public static String getMetaString(Context context, String key) {
        try {
            //for test
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);

            String value = appInfo.metaData.getString(key);
            LogUtil.i("test", "value=" + value);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e("test", e);
            e.printStackTrace();
        }
        return "";
    }

    public static int getAppVersionCode(Context context) {
        int versionCode = 100;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;

        } catch (Exception e) {
            LogUtil.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "1.0";
            }
        } catch (Exception e) {
            LogUtil.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
}
