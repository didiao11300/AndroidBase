package com.maosong.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;

/**
 * To safely operation dialog show and hide
 * @author lzh
 */
public class SafeDialogOper {

    public static void safeShowDialog(final Dialog dialog) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            safeShowDialogOnMainThread(dialog);
            return;
        }

        JMMainHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                safeShowDialogOnMainThread(dialog);
            }
        });

    }

    private static void safeShowDialogOnMainThread (Dialog dialog) {
        if (dialog == null || dialog.isShowing()) {
            return;
        }
        Activity bindAct = getActivity(dialog);

        if (bindAct == null || bindAct.isFinishing()) {
            Log.d("Dialog shown failed:","The Dialog bind's Activity was recycled or finished!");
            return;
        }

        dialog.show();
    }

    private static Activity getActivity(Dialog dialog) {
        Activity bindAct = null;
        Context context = dialog.getContext();
        do {
            if (context instanceof Activity) {
                bindAct = (Activity) context;
                break;
            } else if (context instanceof ContextThemeWrapper) {
                context = ((ContextThemeWrapper) context).getBaseContext();
            } else {
                break;
            }
        } while (true);
        return bindAct;
    }

    public static void safeDismissDialog(final Dialog dialog) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            safeDismissDialogOnMainThread(dialog);
            return;
        }

        JMMainHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                safeDismissDialogOnMainThread(dialog);
            }
        });
    }

    private static void safeDismissDialogOnMainThread(Dialog dialog) {
        if (dialog == null || !dialog.isShowing()) {
            return;
        }

        Activity bindAct = getActivity(dialog);
        if (bindAct != null && !bindAct.isFinishing()) {
            dialog.dismiss();
        }
    }
}
