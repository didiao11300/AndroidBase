package com.maosong.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


/**
 * Created by tory on 2018/6/25.
 */

public class DialogController {
    private SimpleListDialog mMsgMoreInfoDialog;
    private static volatile DialogController sInstance;


    public static DialogController getInstance() {
        if (null == sInstance) {
            synchronized (DialogController.class) {
                if (null == sInstance) {
                    sInstance = new DialogController();
                }
            }
        }
        return sInstance;
    }

    public void showMoreDialog(Context context, List<String> contents, AdapterView.OnItemClickListener itemClickListener) {
        showMoreDialog(context, contents, null, itemClickListener);
    }

    public void showMoreDialog(Context context, List<String> contents, String title, AdapterView.OnItemClickListener itemClickListener) {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            return;
        }
        if (null == mMsgMoreInfoDialog) {
            mMsgMoreInfoDialog = new SimpleListDialog(context, contents, title, itemClickListener);
            mMsgMoreInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mMsgMoreInfoDialog = null;
                }
            });
        }
        if (!mMsgMoreInfoDialog.isShowing() && (context instanceof Activity && !((Activity) context).isFinishing())) {
            mMsgMoreInfoDialog.show();
        }
    }

    public void dismissMoreDialog() {
        SafeDialogOper.safeDismissDialog(mMsgMoreInfoDialog);
        mMsgMoreInfoDialog = null;
    }

    public void showYesNoDialog(Context context, String titleInfo, final DialogCallBack callBack) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(context);
        alterDialog.setMessage(titleInfo);
        alterDialog.setCancelable(true);
        alterDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                callBack.executeEvent();
            }
        });
        alterDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alterDialog.create();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        } else {
////            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
//            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        }
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public interface DialogCallBack {
        void executeEvent();

        void executeEditEvent(String editText);

        void updatePassword(String oldPassword, String newPassword);
    }

    public static class SimpleDialogCallBack implements DialogCallBack {
        @Override
        public void executeEvent() {

        }

        @Override
        public void executeEditEvent(String editText) {

        }

        @Override
        public void updatePassword(String oldPassword, String newPassword) {

        }
    }


    public void showEditDialog(Context context, String title, String hintText, String OKText, final DialogCallBack callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_edittext, null);
        if (!TextUtils.isEmpty(title)) {
            TextView tvTitle = layout.findViewById(R.id.tv_title);
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        } else {
            layout.findViewById(R.id.tv_title).setVisibility(View.GONE);
        }
        builder.setView(layout);
        final EditText et_search = layout.findViewById(R.id.searchC);
        et_search.setHint(hintText);
        TextView pos = layout.findViewById(R.id.tv_pos);
        if (!TextUtils.isEmpty(OKText)) {
            pos.setText(OKText);
        }
        final Dialog dialog = builder.create();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callBack) {
                    String s = et_search.getText().toString().trim();
                    callBack.executeEditEvent(s);
                }
                dialog.dismiss();
            }
        });
        layout.findViewById(R.id.tv_neg).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}
