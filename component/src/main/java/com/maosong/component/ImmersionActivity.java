package com.maosong.component;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jaeger.library.StatusBarUtil;


/**
 * 沉浸式上层Activity, 可独立出来
 * 默认状态栏 theme底 黑字 留出状态栏高度.
 *
 * @author zhouhao
 * @since 2018/7/10
 */
public class ImmersionActivity extends AppCompatActivity {
    protected View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!needTitleBar()) {
            if (this instanceof AppCompatActivity) {
                supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            } else {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
        }

        //是否需要一个statusbar的占位高度
        int placeHolderColor = addStatusPlaceHolderAndSetColor();
        if (placeHolderColor > 0) {
            // 添加RootView
            LinearLayout rootView = new LinearLayout(this);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rootView.setOrientation(LinearLayout.VERTICAL);

            // 添加statusBar的placeHolder.
            View view = new View(this);
//            view.setBackgroundColor(StatusBarUtils.STATUS_COLOR);
            view.setBackgroundColor(placeHolderColor);
            StatusBarUtil statusBarUtil=new StatusBarUtil();
            int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");

            rootView.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,this.getResources().getDimensionPixelSize(resourceId)));
            this.rootView = rootView;
        }

        if (needFullScreen()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (needImmersive()) {
            int bgcolor = getStatusbarBGColor();
            if (bgcolor > 0) {
                StatusBarUtil.setColor(this, getStatusbarBGColor(), getStatusbarAlpha());
            } else {
                StatusBarUtil.setTranslucent(this, getStatusbarAlpha());
            }
            if (isStatusFrontDark()) {
                StatusBarUtil.setDarkMode(this);
            } else {
                StatusBarUtil.setLightMode(this);
            }
        }

        // 设置状态栏
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null, false);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        if (addStatusPlaceHolderAndSetColor() > 0) {
            ((LinearLayout) rootView).addView(view);
        } else {
            rootView = view;
        }
//        if (!isStatusFrontDark() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(StatusBarUtils.STATUS_COLOR);
//        }
        super.setContentView(rootView);
    }


    /**
     * 获取RootView
     *
     * @return 返回contentView
     */
    public View getRootView() {
        return rootView;
    }

    /**
     * 是否状态栏文字是否是黑色
     *
     * @return 默认否
     */
    protected boolean isStatusFrontDark() {
        return false;
    }


    /**
     * 设置状态栏背景色
     **/
    @ColorInt
    protected int getStatusbarBGColor() {
        return -1;
    }

    /**
     * 设置状态栏背景透明都
     **/
    protected int getStatusbarAlpha() {
        return StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA;
    }


    /**
     * 是否需要沉浸式状态栏
     *
     * @return 默认非全屏
     */
    protected boolean needImmersive() {
        return false;
    }

    /**
     * 是否添加statusBar的placeHolder并设置颜色
     *
     * @return 默认否, 全屏的时候可以考虑添加 或者 直接通过fitSystemWindow=true留出statusBar的高度.
     */
    @ColorInt
    protected int addStatusPlaceHolderAndSetColor() {
//        return StatusBarUtils.STATUS_COLOR;
        return -1;
    }

    /**
     * 是否需要titlebar
     **/
    protected boolean needTitleBar() {
        return false;
    }

    /**
     * 是否需要全屏幕，这个就没有什么titlebar和状态bar了
     **/
    protected boolean needFullScreen() {
        return false;
    }
}