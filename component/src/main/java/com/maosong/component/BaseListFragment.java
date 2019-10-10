package com.maosong.component;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.maosong.component.view.BaseListPresenter;
import com.maosong.component.view.BaseListView;
import com.maosong.component.widget.EmptyLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 常用的数据列表类型Fragment
 *
 * @author zhouhao
 * @since 2018/09/20
 */
public abstract class BaseListFragment<P extends BaseListPresenter, D> extends BaseFragment<P> implements BaseListView<D>, OnRefreshLoadMoreListener {
    /**
     * 单页最多条数
     */
    public final static int PAGE_MAX_SIZE = 10;
    /**
     * 数据源
     */
    protected List<D> mListDatas = new ArrayList<>();
    /**
     * 分页值
     */
    protected int page = 1;
    /**
     * 是否启用上拉加载, 下拉刷新
     */
    protected boolean mEnableRefresh = true, mEnableLoadMore = true;
    /**
     * recyclerView
     */
    protected RecyclerView mRecyclerView;
    /**
     * SmartRefreshLayout
     */
    protected SmartRefreshLayout mSmartRefreshLayout;
    /**
     * 空布局
     */
    protected EmptyLayout mEmptyLayout;
    /**
     * 当前界面Adapter
     */
    protected BaseQuickAdapter<D, ?> mAdapter;
    /**
     * SmartRefreshLayout顶部和底部固定区域添加视图, 不跟随滑动
     */
    protected FrameLayout mTopContainer, mBottomContainer;

    @Override
    public int setFragmentView() {
        return R.layout.common_list_layout;
    }

    @Override
    public P createPresenter() {
        return null;
    }

    public void initView() {
        initLayout();
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(this);
        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(getLayoutManager());
        if (getItemDecoration() != null) {
            mRecyclerView.addItemDecoration(getItemDecoration());
        }

        // 添加独立于刷新外的顶部view
        if (getTopView() != null) {
            mTopContainer.addView(getTopView());
        }

        // 添加独立于刷新外的底部view
        if (getBottomView() != null) {
            mBottomContainer.addView(getBottomView());
        }

        initListener();
        initData();
    }

    protected void initListener() {
        // 默认重试为刷新
        mEmptyLayout.setOnRetryListener(() -> {
            mEmptyLayout.setState(EmptyLayout.STATE_LOADING);
            onRefresh(mSmartRefreshLayout);
        });
    }

    protected void initData() {
        if (mPresenter == null) {
            throw new AssertionError(getClass().getSimpleName() + ": The presenter must be initialized.");
        }
        mEmptyLayout.setState(EmptyLayout.STATE_LOADING);
        mPresenter.requestData(false, page = 1);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshlayout) {
        page++;
        mPresenter.requestData(true, page);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        page = 1;
        mPresenter.requestData(false, page);
    }

    @Override
    public void loadDataSuccess(boolean isLoadMore, List<D> data) {
        // 刷新时数据的变化
        if (!isLoadMore) {
            mSmartRefreshLayout.finishRefresh();
            mListDatas.clear();
            // 无数据的时候
            if (data.isEmpty()) {
                mEmptyLayout.setState(EmptyLayout.STATE_NO_DATA);
            } else {
                mEmptyLayout.setState(EmptyLayout.STATE_HIDE);
                // 是否可以加载下一页
                mSmartRefreshLayout.setEnableLoadMore(data.size() >= PAGE_MAX_SIZE && mEnableLoadMore);
                mListDatas.addAll(data);
                mAdapter.notifyDataSetChanged();
            }
        } else { // 上拉加载的数据变动
            mSmartRefreshLayout.finishLoadMore();
            if (data.isEmpty()) {
                page--;
                mSmartRefreshLayout.setEnableLoadMore(false);
            } else {
                mListDatas.addAll(data);
                mSmartRefreshLayout.setEnableLoadMore(data.size() >= PAGE_MAX_SIZE && mEnableLoadMore);
                mAdapter.notifyItemRangeInserted(mListDatas.size() - data.size() + 1, data.size());
            }
        }
    }

    @Override
    public void loadDataFailed(boolean isLoadMore, Throwable e) {
        if (!isLoadMore) {
            mEmptyLayout.setState(EmptyLayout.STATE_NETWORK_ERROR);
        } else {
            page--;
            showNetErrorMsg(e);
        }
    }

    /**
     * 初始化是否需要支持上拉加载/下拉刷新
     *
     * @param enableRefresh  是否启用上拉刷新
     * @param enableLoadMore 是否启用下拉加载
     */
    @SuppressWarnings("unused")
    public void setEnableStatus(boolean enableRefresh, boolean enableLoadMore) {
        mSmartRefreshLayout.setEnableRefresh(mEnableRefresh = enableRefresh);
        mSmartRefreshLayout.setEnableLoadMore(mEnableLoadMore = enableLoadMore);
    }

    /**
     * 初始化必要的视图, 包含RecyclerView, EmptyLayout, SmartRefreshLayout
     *
     * @see #mRecyclerView
     * @see #mEmptyLayout
     * @see #mSmartRefreshLayout
     */
    @SuppressWarnings("ConstantConditions")
    protected void initLayout() {
        mRecyclerView = getView().findViewById(R.id.recycler);
        mSmartRefreshLayout = getView().findViewById(R.id.smart_refresh_layout);
        mEmptyLayout = getView().findViewById(R.id.empty_layout);
        mTopContainer = getView().findViewById(R.id.top_view);
        mBottomContainer = getView().findViewById(R.id.bottom_view);
    }

    /**
     * 获取当且界面Adapter
     *
     * @return Adapter
     */
    protected abstract BaseQuickAdapter<D, ?> getAdapter();

    /**
     * recyclerView的layoutManager
     *
     * @return layoutManager
     */
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    /**
     * recyclerView的ItemDecoration
     *
     * @return ItemDecoration
     */
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return null;
    }

    @Override
    public int getPageMaxSize() {
        return PAGE_MAX_SIZE;
    }

    /**
     * 独立于smartRefreshLayout外的顶部View
     *
     * @return null will be ignored.
     */
    protected View getTopView() {
        return null;
    }

    /**
     * 独立于smartRefreshLayout外的底部View
     *
     * @return null will be ignored.
     */
    protected View getBottomView() {
        return null;
    }
}
