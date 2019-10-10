package com.maosong.component.view;

import java.util.List;

/**
 * @author zhouhao
 * @since 2019/04/18
 */
public interface BaseListView<D> extends BaseView {

    /**
     * 获取数据成功
     *
     * @param isLoadMore 是否上拉加载
     * @param data       获取到的数据
     */
    void loadDataSuccess(boolean isLoadMore, List<D> data);

    /**
     * 获取数据失败
     *
     * @param isLoadMore 是否上拉加载
     * @param e          错误
     */
    void loadDataFailed(boolean isLoadMore, Throwable e);

    /**
     * 获取单次加载最多条数
     *
     * @return 条数
     */
    int getPageMaxSize();
}
