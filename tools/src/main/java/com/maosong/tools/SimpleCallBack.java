package com.maosong.tools;

/**
 * create by colin on 2019/4/18
 */
public interface SimpleCallBack {
    void onSuccess(String msg);

    void onFailed(Throwable throwable);
}
