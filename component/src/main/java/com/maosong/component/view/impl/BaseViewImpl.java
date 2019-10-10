package com.maosong.component.view.impl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import com.maosong.component.R;
import com.maosong.component.net.ApiException;
import com.maosong.component.net.TokenException;
import com.maosong.component.view.BaseView;
import com.maosong.tools.SPUtils;
import com.maosong.tools.ToastUtils;
import retrofit2.HttpException;

/**
 * create by colin on 2019-05-08
 */
public class BaseViewImpl implements BaseView {
    private Context mContext;
    private ProgressDialog mLoadDialog;

    public BaseViewImpl(Context context) {
        mContext = context;
    }

    @Override
    public void showLoading() {
        showLoading(null);
    }

    @Override
    public void showLoading(String message) {
        if (mLoadDialog == null) {
            mLoadDialog = new ProgressDialog(mContext);
            mLoadDialog.setCancelable(false);
        }
        if (TextUtils.isEmpty(message)) {
            mLoadDialog.setMessage(mContext.getString(R.string.wait));
        } else {
            mLoadDialog.setMessage(message);
        }
//        if (!mLoadDialog.isShowing())
        mLoadDialog.show();
    }

    @Override
    public void dismissLoading() {
        if (mLoadDialog != null && mLoadDialog.isShowing())
            mLoadDialog.dismiss();
    }

    @Override
    public void showTipMessage(String msg) {
        ToastUtils.showLongToast(msg);
    }


    @Override
    public String getNetKey() {
        return "";
    }

    @Override
    public void toLoginActBySessionError(TokenException te) {
        showTipMessage(mContext.getString(R.string.token_error));
        SPUtils.getInstance().clear(true);
        SPUtils.getInstance("loginTemp").clear(true);
    }

    @Override
    public void showNetErrorMsg(Throwable throwable) {
        if (throwable instanceof ApiException) {
            ApiException exception = (ApiException) throwable;
            if (exception.getCode() != 500)
                showTipMessage(exception.getMsg());
            else showTipMessage(mContext.getString(R.string.http_error) + mContext.getString(R.string.server_500));
        } else if (throwable instanceof HttpException) {
            showTipMessage(mContext.getString(R.string.http_error));
        } else if (throwable instanceof TokenException) {
            toLoginActBySessionError((TokenException) throwable);
        } else {
            showTipMessage(mContext.getString(R.string.some_thing) + throwable.getMessage());
            throwable.printStackTrace();
        }
    }

    @Override
    public void showAlertDialog(String message) {
        showAlertDialog(message, (dialog, which) -> dialog.dismiss());
    }

    @Override
    public void showAlertDialog(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setCancelable(false)
                .show();
    }
}
