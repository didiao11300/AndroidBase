package com.maosong.component.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.maosong.component.R;
import com.maosong.tools.QMUIDisplayHelper;

public class SettingItemView extends LinearLayout {

    private TextView mTvTitle, mTvSubInfo, mTvCount;
    private int count = 0;
    private ImageView endIconView;
    private SwitchCompat mSwitch;
    private ImageView mIconNext;

    public SettingItemView(Context context) {
        this(context, null);

    }

    public SettingItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int pd = QMUIDisplayHelper.dp2px(context, 15);
        setBackgroundResource(R.drawable.btn_list_item_bg);
        setPadding(pd, 0, pd, 0);
        if (attrs == null)
            return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        int iconRes = typedArray.getResourceId(R.styleable.SettingItemView_icon, -1);
        float iconSize = typedArray.getDimension(R.styleable.SettingItemView_iconSize, QMUIDisplayHelper.dp2px(context, 25));
        float textSize = typedArray.getDimension(R.styleable.SettingItemView_titleSize, QMUIDisplayHelper.sp2px(context, 14));
        float infoSize = typedArray.getDimension(R.styleable.SettingItemView_infoSize, QMUIDisplayHelper.sp2px(context, 12));
        boolean hasNext = typedArray.getBoolean(R.styleable.SettingItemView_hasNext, false);
        boolean endIcon = typedArray.getBoolean(R.styleable.SettingItemView_endIcon, false);
        boolean isSwitchModel = typedArray.getBoolean(R.styleable.SettingItemView_isSwitchModel, false);
        String title = typedArray.getString(R.styleable.SettingItemView_titleText);
        String infoText = typedArray.getString(R.styleable.SettingItemView_infoText);
        typedArray.recycle();
        if (iconRes != -1) { //左边的ICON
            int wh = (int) iconSize;
            ImageView icon = new ImageView(context);
            LayoutParams params = new LayoutParams(wh, wh);
            icon.setImageResource(iconRes);
            icon.setAdjustViewBounds(true);
            addView(icon, params);
        }
        //主标题
        mTvTitle = new TextView(context);
        LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2);
        if (iconRes != -1)
            params.leftMargin = QMUIDisplayHelper.dp2px(context, 15);
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mTvTitle.setText(title);
        mTvTitle.setTextColor(ContextCompat.getColor(context, R.color.font_black));
        addView(mTvTitle, params);

        //第二个信息
        if (!endIcon && !isSwitchModel) {
            mTvSubInfo = new TextView(context);
            LayoutParams params2 = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 3);
            mTvSubInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, infoSize);
            mTvSubInfo.setText(infoText);
            mTvSubInfo.setGravity(Gravity.END);
            mTvSubInfo.setTextColor(ContextCompat.getColor(context, R.color.selection_text));
            addView(mTvSubInfo, params2);
        }
        if (TextUtils.isEmpty(infoText)) {
            if (mTvSubInfo != null) {
                mTvSubInfo.setVisibility(View.GONE);
            }
        }
        //右边的ICON
        if (endIcon && !isSwitchModel) {
            int wh = QMUIDisplayHelper.dp2px(context, 25);
            endIconView = new ImageView(context);
            LayoutParams params3 = new LayoutParams(wh, wh);
            addView(endIconView, params3);
        }

        if (isSwitchModel && !endIcon) {
            //添加switch
            mSwitch = new SwitchCompat(context);
            LayoutParams params4 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            addView(mSwitch, params4);
        }

        //添加count 的TextView
        mTvCount = new TextView(context);
        mTvCount.setTextColor(Color.WHITE);
//        mTvCount.setBackgroundColor(Color.RED);
        mTvCount.setBackgroundResource(R.drawable.bg_round_count);
        mTvCount.setGravity(Gravity.CENTER);
        mTvCount.setTextSize(10);
        addView(mTvCount, new LayoutParams(QMUIDisplayHelper.dp2px(context, 15), QMUIDisplayHelper.dp2px(context, 15)));
        mTvCount.setVisibility(GONE);

        if (hasNext && !isSwitchModel) {
            mIconNext = new ImageView(context);
            mIconNext.setImageResource(R.mipmap.icon_arrow_right);
            LayoutParams params1 = new LayoutParams(QMUIDisplayHelper.dp2px(context, 7), QMUIDisplayHelper.dp2px(context, 10));
            addView(mIconNext, params1);
            if (mTvSubInfo != null)
                mTvSubInfo.setPadding(0, 0, 15, 0);
            if (endIconView != null) {
                endIconView.setPadding(0, 0, 15, 0);
            }
        }
    }

    public void setGoneNext() {
        if (null != mIconNext) {
            mIconNext.setVisibility(GONE);
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        if (mTvCount == null)
            return;
        if (count > 0) {
            mTvCount.setText(String.valueOf(count));
            mTvCount.setVisibility(VISIBLE);
        } else {
            mTvCount.setText(String.valueOf(0));
            mTvCount.setVisibility(GONE);
        }
    }

    public void setTitleText(CharSequence title) {
        if (mTvTitle != null)
            mTvTitle.setText(title);
    }

    public CharSequence getTitleText() {
        if (mTvTitle != null) {
            return mTvTitle.getText().toString();
        }
        return "";
    }

    public void setSubInfoText(CharSequence subInfo) {
        if (mTvSubInfo != null) {
            mTvSubInfo.setText(subInfo);
            mTvSubInfo.setVisibility(VISIBLE);
        }
    }

    public CharSequence getSubInfoText() {
        if (mTvSubInfo != null) {
            return mTvSubInfo.getText().toString();
        }
        return "";
    }

    public ImageView getEndIconView() {
        return endIconView;
    }

    public void setEndIconResource(@DrawableRes int resource) {
        if (endIconView != null) {
            endIconView.setImageResource(resource);
        }
    }

    public void setOnSwitchCheckedListener(CompoundButton.OnCheckedChangeListener listener) {
        if (mSwitch != null) {
            mSwitch.setOnCheckedChangeListener(listener);
        }
    }

    public boolean isSwitchChecked() {
        return null != mSwitch && mSwitch.isChecked();
    }

    public void setSwitchChecked(boolean isChecked) {
        if (null != mSwitch) {
            mSwitch.setChecked(isChecked);
        }
    }
}
