package com.maosong.component;

import com.maosong.component.view.BaseListPresenter;
import com.maosong.component.view.BaseListView;

/**
 * @author zhouhao
 * @since 2019/04/19
 */
public class SimpleListPresenter<V extends BaseListView> extends SimpleBasePresenter<V> implements BaseListPresenter {

    public SimpleListPresenter(V mView) {
        super(mView);
    }

    @Override
    public void requestData(boolean isLoadMore, int page) {

    }
}
