package com.maosong.component;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.maosong.component.event.ForceFinishEvent;
import com.maosong.component.event.MessageEvent;
import com.maosong.component.net.RxNetLife;
import com.maosong.component.net.TokenException;
import com.maosong.component.view.BasePresenter;
import com.maosong.component.view.BaseView;
import com.maosong.component.view.PermissionListener;
import com.maosong.component.view.TopBarView;
import com.maosong.component.view.impl.BaseViewImpl;
import com.maosong.component.view.impl.TopBarViewImpl;
import com.maosong.tools.AppLifeCircleUtil;
import com.maosong.tools.LogUtil;
import com.maosong.tools.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


/**
 * Created by tianweiping on 2017/12/11.
 * 1，包含 title的处理
 * 2，包含 默认empty的处理
 * 3，包含权限的处理
 */

public abstract class BaseActivity<P extends BasePresenter> extends ImmersionActivity implements BaseView, TopBarView {

    public P mPresenter;

    //空页面的字
    private String mEmptyText = "";
    /**
     * 空布局
     */
    private View mEmptyView;
    /**
     * 当activity作为容器时的内部fragment
     */
    protected BaseFragment mFragment;
    /**
     * 当前activity
     */
    protected Activity activity;
    /**
     * 当前activity是否活跃
     */
    protected boolean isAlive;
    /**
     * 权限检查CallBack
     */
    private PermissionListener mPermissionListener;
    private TopBarView mTopBarImpl;
    private BaseView mBaseViewImpl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        ActivityHandler.INSTANCE.addActivity(this);
        EventBus.getDefault().register(this);
        setContentView(getContentViewRes());
        ButterKnife.bind(this);
        initInner();
        AppLifeCircleUtil.getInstance().pushActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isAlive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAlive = false;
    }

    public abstract @LayoutRes
    int getContentViewRes();

    public abstract P createPresenter();

    private void initInner(){
        mPresenter = createPresenter();
        if (mPresenter != null)
            mPresenter.onAttach(this);
//        View parentView = getWindow().getDecorView().getRootView();
        mEmptyView = LayoutInflater.from(this).inflate(R.layout.layout_simple_empty_view, null, false);
        TextView tv = mEmptyView.findViewById(R.id.tv_empty_text);
        if (null != tv && !TextUtils.isEmpty(mEmptyText)) {
            tv.setText(mEmptyText);
        }
        View topTitle = findViewById(R.id.base_toolbar_frame);
        mTopBarImpl = new TopBarViewImpl(topTitle);
        mBaseViewImpl = new BaseViewImpl(this);
        mTopBarImpl.setBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
    public void initView() {

    }

    @Override
    public void onBackPressed() {
        BaseFragment fragment = getCurrentFragment();
        if (fragment != null) {
            fragment.popBackStack();
        } else {
            popBackStack();
//            if (!isFinishing())
//                super.onBackPressed();
        }
    }

    public Context getContext() {
        return this;
    }

    /**
     * get the current Fragment.
     */
    public BaseFragment getCurrentFragment() {
        int contextViewId = getContextViewId();
        if (contextViewId != 0)
            return (BaseFragment) getSupportFragmentManager().findFragmentById(getContextViewId());
        else return null;
    }


    public void startFragment(BaseFragment fragment) {
        LogUtil.i("startFragment");
        BaseFragment.TransitionConfig transitionConfig = fragment.onFetchTransitionConfig();
        String tagName = fragment.getClass().getSimpleName();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(transitionConfig.enter, transitionConfig.exit, transitionConfig.popenter, transitionConfig.popout)
                .replace(getContextViewId(), fragment, tagName)
                .addToBackStack(tagName)
                .commit();
        mFragment = fragment;
    }

    public void startFragmentNoAnim(BaseFragment fragment) {
        LogUtil.i("startFragment");
        String tagName = fragment.getClass().getSimpleName();
        getSupportFragmentManager().beginTransaction()
                .replace(getContextViewId(), fragment, tagName)
                .commit();
        mFragment = fragment;
    }

    public void startFragmentNoReplace(BaseFragment fragment) {
        LogUtil.i("startFragment");
        BaseFragment.TransitionConfig transitionConfig = fragment.onFetchTransitionConfig();
        String tagName = fragment.getClass().getSimpleName();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(transitionConfig.enter, transitionConfig.exit, transitionConfig.popenter, transitionConfig.popout);
        if (mFragment != null) {
            fragmentTransaction.hide(mFragment);
        }
        fragmentTransaction.add(getContextViewId(), fragment, tagName)
                .addToBackStack(tagName)
                .commit();
        mFragment = fragment;
    }

    public void startFragmentNoBackStack(BaseFragment fragment) {
        LogUtil.i("startFragmentNoBackStack");
        BaseFragment.TransitionConfig transitionConfig = fragment.onFetchTransitionConfig();
        String tagName = fragment.getClass().getSimpleName();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(transitionConfig.enter, transitionConfig.exit, transitionConfig.popenter, transitionConfig.popout)
                .replace(getContextViewId(), fragment, tagName)
                .commit();
        mFragment = fragment;
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
     * 退出当前的 Fragment。
     */
    public void popBackStack() {
        LogUtil.i("popBackStack: getSupportFragmentManager().getBackStackEntryCount() = " + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            BaseFragment fragment = getCurrentFragment();
            if (fragment == null) {
                finish();
                return;
            }
            BaseFragment.TransitionConfig transitionConfig = fragment.onFetchTransitionConfig();
            Object toExec = fragment.onLastFragmentFinish();
            if (toExec != null) {
                if (toExec instanceof BaseFragment) {
                    BaseFragment mFragment = (BaseFragment) toExec;
                    startFragment(mFragment);
                } else if (toExec instanceof Intent) {
                    Intent intent = (Intent) toExec;
                    startActivity(intent);
                    overridePendingTransition(transitionConfig.popenter, transitionConfig.popout);
                    finish();
                } else {
                    throw new Error("can not handle the result in onLastFragmentFinish");
                }
            } else {
                finish();
            }
        } else {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    protected void onDestroy() {
        AppLifeCircleUtil.getInstance().removeActivity(this);
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        mPresenter = null;
        activity = null;
        ActivityHandler.INSTANCE.removeActivity(this);
        EventBus.getDefault().unregister(this);
        RxNetLife.getNetLife().clear(getNetKey());
        setFirstRuned();
        super.onDestroy();
    }

    public @IdRes
    int getContextViewId() {
        return 0;
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


    /**
     * 检查和处理运行时权限，并将用户授权的结果通过PermissionListener进行回调。
     *
     * @param permissions 要检查和处理的运行时权限数组
     * @param listener    用于接收授权结果的监听器
     */
    protected void handlePermissions(String[] permissions, @Nullable PermissionListener listener) {
        if (permissions == null || activity == null) {
            return;
        }
        mPermissionListener = listener;
        List<String> requestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionList.add(permission);
            }
        }
        if (!requestPermissionList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissions, 1);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        if (messageEvent instanceof ForceFinishEvent) {
            if (activity != null && activity.getClass().getSimpleName().equals(((ForceFinishEvent) messageEvent).getClassName())) {
                activity.finish();
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
