package com.maosong.component.view;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.maosong.component.R;

/**
 * create by colin on 2019-05-08
 * topTitleBar的一些公共方法
 */
public interface TopBarView {

    /**
     * 设置分割线显示
     *
     * @param visiable true 显示
     */
    void setTopBarDivideLineGone(boolean visiable);

    /**
     * 设置标题
     */
    TextView setTopBarTitle(CharSequence title);

    /**
     * 隐藏topbar
     */
    void hideTopBar();

    /**
     * 显示topbar
     */
    void showTopBar();

    /**
     * 设置topbar的背景颜色
     */
    View setTopBarColor(int color);

    /**
     * 右上角添加textbutton
     */
    Button addTextButtonToRight(String text);

    /**
     * @param imgRes <=0 not set
     **/
    ImageButton addImageButtonToRight(int imgRes);

    ImageView setBack(@Nullable View.OnClickListener listener);
}
