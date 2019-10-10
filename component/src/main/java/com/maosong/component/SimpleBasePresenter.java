package com.maosong.component;

import android.content.Context;
import com.maosong.component.view.BasePresenter;
import com.maosong.component.view.BaseView;

/**
 * @author zhouhao
 * @since 2019/04/19
 */
public class SimpleBasePresenter<V extends BaseView> implements BasePresenter {

    protected V mView;

    protected Context mContext;

    public SimpleBasePresenter(V mView) {
        this.mView = mView;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
    }

    @Override
    public void onDestroy() {
        mView = null;
        mContext = null;
    }
}
