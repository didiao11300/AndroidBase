package com.maosong.component.view;

import android.content.Context;
import android.content.DialogInterface;
import com.maosong.component.net.TokenException;

/**
 * Created by tianweiping on 2017/12/11.
 */

public interface BaseView {

    /**
     * 显示一个loading
     */
    void showLoading();

    void showLoading(String message);

    /**
     * 关闭loading
     */
    void dismissLoading();

    /**
     * 弹一个吐司提示
     * <p>
     * * @param msg  提示信息
     */
    void showTipMessage(String msg);


    /**
     * 管理 网络请求生命周期的 key
     *
     * @return key
     */
    String getNetKey();

    /**
     * 因为token相关错误需要跳转到登录页面
     */
    void toLoginActBySessionError(TokenException te);

    /**
     * 显示网络错误信息
     */
    void showNetErrorMsg(Throwable throwable);

    void showAlertDialog(String message);

    void showAlertDialog(String message, DialogInterface.OnClickListener listener);
}
