package com.maosong.component.view;

/**
 * @author zhouhao
 * @since 2019/04/18
 */
public interface BaseListPresenter extends BasePresenter {
    /**
     * 获取数据
     *
     * @param isLoadMore 是否上拉加载
     * @param page       当前页
     */
    void requestData(boolean isLoadMore, int page);
}
