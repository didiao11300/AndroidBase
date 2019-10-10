package com.maosong.tools;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
    private static Toast toast;//实现不管我们触发多少次Toast调用，都只会持续一次Toast显示的时长
    private static Toast redToast;

    public static void showShortToast(int resId) {
        showShortToast(ToolsApp.getAppContext().getResources().getString(resId), 0);
    }

    /**
     * 短时间显示Toast【居下】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToast(String msg) {
        showShortToast(msg, 0);
    }

    public static void showShortToast(String msg, int delay) {
//        if(Thread.currentThread().getId() !=JMMainHandler.getInstance())
        if (ToolsApp.getAppContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(ToolsApp.getAppContext(), msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            //1、setGravity方法必须放到这里，否则会出现toast始终按照第一次显示的位置进行显示（比如第一次是在底部显示，那么即使设置setGravity在中间，也不管用）
            //2、虽然默认是在底部显示，但是，因为这个工具类实现了中间显示，所以需要还原，还原方式如下：
            toast.setGravity(Gravity.BOTTOM, 0, dip2px(ToolsApp.getAppContext(), 64));

            JMMainHandler.getInstance().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.show();
                }
            }, delay);
        }
    }

    /**
     * 短时间显示Toast【居中】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToastCenter(String msg) {
        if (ToolsApp.getAppContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(ToolsApp.getAppContext(), msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 短时间显示Toast【居上】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToastTop(String msg) {
        if (ToolsApp.getAppContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(ToolsApp.getAppContext(), msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
    }

    /**
     * 长时间显示Toast【居下】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showLongToast(String msg) {
        if (ToolsApp.getAppContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(ToolsApp.getAppContext(), msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.BOTTOM, 0, dip2px(ToolsApp.getAppContext(), 64));
            toast.show();
        }
    }

    /**
     * 长时间显示Toast【居中】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showLongToastCenter(String msg) {
        if (ToolsApp.getAppContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(ToolsApp.getAppContext(), msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 长时间显示Toast【居上】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showLongToastTop(String msg) {
        showLongToastTop(msg, 0);
    }

    public static void showLongToastTop(String msg, int delay) {
        if (ToolsApp.getAppContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(ToolsApp.getAppContext(), msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.TOP, 0, 0);
            JMMainHandler.getInstance().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                    toast.show();
                }
            }, delay);
        }
    }

    public static void showRedToastShort(String msg) {
        if (ToolsApp.getAppContext() != null) {
            if (redToast == null) {
                redToast = new Toast(ToolsApp.getAppContext());
                View view = LayoutInflater.from(ToolsApp.getAppContext()).inflate(R.layout.toast_red, null, false);
                TextView title = view.findViewById(R.id.toast_title);
                title.setText(msg);
                redToast.setView(view);
            } else {
                TextView title = redToast.getView().findViewById(R.id.toast_title);
                title.setText(msg);
            }
            //1、setGravity方法必须放到这里，否则会出现toast始终按照第一次显示的位置进行显示（比如第一次是在底部显示，那么即使设置setGravity在中间，也不管用）
            //2、虽然默认是在底部显示，但是，因为这个工具类实现了中间显示，所以需要还原，还原方式如下：
            redToast.setGravity(Gravity.BOTTOM, 0, dip2px(ToolsApp.getAppContext(), 64));

            JMMainHandler.getInstance().postDelayed(new Runnable() {
                @Override
                public void run() {
                    redToast.show();
                }
            }, 0);
        }
    }

    /*=================================常用公共方法============================*/
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
