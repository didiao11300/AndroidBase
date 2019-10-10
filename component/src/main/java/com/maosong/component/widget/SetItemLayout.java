package com.maosong.component.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.maosong.component.R;
import com.maosong.tools.QMUIDisplayHelper;

/**
 *
 */
public class SetItemLayout extends LinearLayout {
    protected TextView itemName;
    protected LinearLayout itemLayout;
    ImageView itemTip;
    TextView subTitle;
    ImageView redPoint;

    public SetItemLayout(Context context) {
        super(context);
        init();
    }

    public SetItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SetItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setFocusable(true);
        init();
        initByAttrs(context, attrs);
    }

    protected void init() {
        removeAllViews();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                QMUIDisplayHelper.dp2px(getContext(), 44)));
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.setting_item_layout, null);
        addView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, QMUIDisplayHelper.dp2px(getContext(), 44)));
        itemName = findViewById(R.id.item_name);
        itemLayout = findViewById(R.id.item_layout);
        itemTip = findViewById(R.id.item_tip);
        subTitle = findViewById(R.id.sub_title);
        redPoint = findViewById(R.id.red_point);

        initClickEvent();
        //大部分选项都不能点击，箭头改为默认不显示，能点击的选项再单独调用show方法显示箭头
        itemTip.setVisibility(View.GONE);
    }

    private void initByAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
//
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SetItemLayout);
//        setTitle(ta.getText(R.styleable.SetItemLayout_title));
//        setSubTitle(ta.getText(R.styleable.SetItemLayout_subTitle));
//        if (ta.getBoolean(R.styleable.SetItemLayout_showRedDot,false)) {
//            showRedPoint();
//        } else {
//            hideRedPoint();
//        }
//        ta.recycle();
    }

    protected void initClickEvent() {
        itemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInClickEvent) {
                    return;
                }
                isInClickEvent = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isInClickEvent = false;
                    }
                }, 1500);
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });
    }

    private Handler handler = new Handler();
    private boolean isInClickEvent;

    public void setTitle(CharSequence title) {
        itemName.setText(title);
    }

    public void setSubTitle(CharSequence title) {
        subTitle.setText(title);
        subTitle.setVisibility(View.VISIBLE);
    }

    protected OnClickEventListener mListener;

    public interface OnClickEventListener {
        void onClick();
    }

    public void setClickEventListener(OnClickEventListener listener) {
        mListener = listener;
    }

    public void enable() {
        itemLayout.setEnabled(true);
    }

    public void disable() {
        itemLayout.setEnabled(false);
    }

    public boolean isEnable() {
        return itemLayout.isEnabled();
    }

    public void setSwitch() {
        if (isEnable()) {
            disable();
        } else {
            enable();
        }
    }

    public void showRedPoint() {
        redPoint.setVisibility(View.VISIBLE);
    }

    public void hideRedPoint() {
        redPoint.setVisibility(View.GONE);
    }

    public View getCLickEventView() {
        return itemLayout;
    }

    public void showArrowIcon() {
        itemTip.setVisibility(View.VISIBLE);
    }

    public void hideArrowIcon() {
        itemTip.setVisibility(View.GONE);
    }
}

