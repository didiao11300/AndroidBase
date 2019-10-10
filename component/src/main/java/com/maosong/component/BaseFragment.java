package com.maosong.component;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.maosong.component.event.MessageEvent;
import com.maosong.component.view.BasePresenter;
import com.maosong.component.view.BaseView;
import com.maosong.component.view.PermissionListener;
import com.maosong.component.net.RxNetLife;
import com.maosong.component.net.TokenException;
import com.maosong.component.view.TopBarView;
import com.maosong.component.view.impl.BaseViewImpl;
import com.maosong.component.view.impl.TopBarViewImpl;
import com.maosong.tools.SPUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <P> 具体的presenter
 * @author unknown
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView, TopBarView {

    /**
     * fragment懒加载模式时首次进入判断
     */
    protected boolean isFirst = false;
    private Boolean isPrepared = false;
    /**
     * 当前fragment 的View
     */
    protected View convertView;
    /**
     * 空布局上的文字展示
     */
    private String mEmptyText = "";
    /**
     * 空数据布局
     */
    private View mEmptyView;
    /**
     * 当前界面presenter
     */
    public P mPresenter;
    /**
     * 默认转场动画
     */
    protected static final TransitionConfig SLIDE_TRANSITION_CONFIG = new TransitionConfig(
            R.anim.slide_in_right, R.anim.slide_out_left,
            R.anim.slide_in_left, R.anim.slide_out_right);

    /**
     * 请求权限 Callback
     */
    private PermissionListener mPermissionListener;
    /**
     * topbar 相关
     */
    private TopBarView mTopBarImpl;

    /**
     * baseview 相关
     */
    private BaseViewImpl mBaseViewImpl;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (setFragmentView() != 0) {
            convertView = inflater.inflate(setFragmentView(), container, false);
            mEmptyView = LayoutInflater.from(getContext()).inflate(R.layout.layout_simple_empty_view, null, false);
            TextView tv = mEmptyView.findViewById(R.id.tv_empty_text);
            if (null != tv && !TextUtils.isEmpty(mEmptyText)) {
                tv.setText(mEmptyText);
            }
            mTopBarImpl = new TopBarViewImpl(convertView.findViewById(R.id.base_toolbar_frame));
            mTopBarImpl.setBack(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popBackStack();
                }
            });
            mBaseViewImpl = new BaseViewImpl(getContext());
            EventBus.getDefault().register(this);
            return convertView;
        } else {
            return convertView = super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.onAttach(getContext());
        }
        initView();
    }

    /**
     * 在这里取出数据
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isFirst = true;
        initPrepare();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
    }

    /**
     * 这里保存数据
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setFirstRuned();
        RxNetLife.getNetLife().clear(getNetKey());
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        setFirstRuned();
        EventBus.getDefault().unregister(this);
        mPresenter = null;
    }

    public abstract void initView();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirst) {
                initPrepare();
            } else if (isPrepared) {
                onUserVisible();
            }
        } else {
            onUserInVisible();
        }
    }

    private synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
            isFirst = false;
        } else {
            isPrepared = true;
        }
    }

    public void onFirstUserVisible() {

    }

    public void onUserVisible() {
    }

    public void onUserInVisible() {
    }


    public abstract int setFragmentView();

    public abstract P createPresenter();

    ////////界面跳转动画
    public static final class TransitionConfig {
        public final int enter;
        public final int exit;
        public final int popenter;
        public final int popout;

        TransitionConfig(int enter, int exit, int popenter, int popout) {
            this.enter = enter;
            this.exit = exit;
            this.popenter = popenter;
            this.popout = popout;
        }
    }

    /**
     * 转场动画控制
     */
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }

    public Object onLastFragmentFinish() {
        return null;
    }

    public void startFragment(BaseFragment fragment) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) getActivity()).startFragment(fragment);
        }

    }


    public void popBackStack() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) getActivity()).popBackStack();
        }
    }

    /**
     * 使用默认的EmptyView来提示用户
     * {R.layout.layout_simple_empty_view.xml}
     */
    public void setEmptyViewText(String text) {
        mEmptyText = text;
        if (null != mEmptyView) {
            TextView tv = mEmptyView.findViewById(R.id.tv_empty_text);
            if (null != tv && !TextUtils.isEmpty(mEmptyText)) {
                tv.setText(mEmptyText);
            }
        }
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    protected boolean isFirstRunning() {
        return isFirstRunning(true);
    }

    /**
     * 初次实例化这个类
     *
     * @param clearAble true,可以清除，一般用于首次展示界面
     *                  false,标志不可以被清除,一般用于首次安装
     */
    protected boolean isFirstRunning(boolean clearAble) {
        boolean firstRun = true;
        if (clearAble) {
            firstRun = SPUtils.getInstance().getBoolean("firstRun" + this.getClass().getSimpleName(), true);
        } else {
            SharedPreferences sp = getContext().getSharedPreferences("first_install_guide", Context.MODE_PRIVATE);
            firstRun = sp.getBoolean("firstRun" + this.getClass().getSimpleName(), true);
        }
        return firstRun;
    }

    /**
     * 设置firstrun 已经运行过了
     */
    protected void setFirstRuned() {
        SPUtils.getInstance().put("firstRun" + this.getClass().getSimpleName(), false);
        SharedPreferences sp = getContext().getSharedPreferences("first_install_guide", Context.MODE_PRIVATE);
        sp.edit().putBoolean("firstRun" + this.getClass().getSimpleName(), true).apply();
    }

    /**
     * 检查和处理运行时权限，并将用户授权的结果通过PermissionListener进行回调。
     *
     * @param permissions 要检查和处理的运行时权限数组
     * @param listener    用于接收授权结果的监听器
     */
    protected void handlePermissions(String[] permissions, @Nullable PermissionListener listener) {
        if (permissions == null || getActivity() == null) {
            return;
        }
        mPermissionListener = listener;
        List<String> requestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionList.add(permission);
            }
        }
        if (!requestPermissionList.isEmpty()) {
            requestPermissions(permissions, 1);
        } else if (listener != null) {
            listener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length != 0) {
                List<String> deniedPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    int grantResult = grantResults[i];
                    String permission = permissions[i];
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permission);
                    }
                }
                if (mPermissionListener == null) {
                    return;
                }
                if (deniedPermissions.isEmpty()) {
                    mPermissionListener.onGranted();
                } else {
                    mPermissionListener.onDenied(deniedPermissions);
                }
            }
        }
    }

    /**
     * Baseview 相关
     */
    @Override
    public void showLoading() {
        mBaseViewImpl.showLoading();
    }

    @Override
    public void showLoading(String message) {
        mBaseViewImpl.showLoading(message);
    }

    @Override
    public void dismissLoading() {
        mBaseViewImpl.dismissLoading();
    }

    @Override
    public void showTipMessage(String msg) {
        mBaseViewImpl.showTipMessage(msg);
    }

    @Override
    public void showNetErrorMsg(Throwable throwable) {
        mBaseViewImpl.showNetErrorMsg(throwable);
    }

    @Override
    public void showAlertDialog(String message) {
        mBaseViewImpl.showAlertDialog(message);
    }

    @Override
    public void showAlertDialog(String message, DialogInterface.OnClickListener listener) {
        mBaseViewImpl.showAlertDialog(message, listener);
    }

    @Override
    public String getNetKey() {
        return getClass().getSimpleName();
    }

    @Override
    public void toLoginActBySessionError(TokenException te) {
        mBaseViewImpl.toLoginActBySessionError(te);
    }

    /********************
     * topbar 区域
     * ******************/
    @Override
    public void setTopBarDivideLineGone(boolean visiable) {
        mTopBarImpl.setTopBarDivideLineGone(visiable);
    }

    @Override
    public TextView setTopBarTitle(CharSequence title) {
        return mTopBarImpl.setTopBarTitle(title);
    }

    @Override
    public void hideTopBar() {
        mTopBarImpl.hideTopBar();
    }

    @Override
    public void showTopBar() {
        mTopBarImpl.showTopBar();
    }

    @Override
    public View setTopBarColor(int color) {
        return mTopBarImpl.setTopBarColor(color);
    }

    @Override
    public Button addTextButtonToRight(String text) {
        return mTopBarImpl.addTextButtonToRight(text);
    }

    @Override
    public ImageButton addImageButtonToRight(int imgRes) {
        return mTopBarImpl.addImageButtonToRight(imgRes);
    }

    @Override
    public ImageView setBack(@Nullable View.OnClickListener listener) {
        return mTopBarImpl.setBack(listener);
    }
}
