package com.maosong.component.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.maosong.component.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhongzheng on 16/6/29.
 */
public class SetAreaLayout extends LinearLayout {

    LinearLayout itemContainer;
    LinearLayout areaLayout;
    private String mTitle;

    private List<SetItemLayout> mItems = new ArrayList<>();

    public SetAreaLayout(Context context) {
        super(context);
        init();
    }

    public SetAreaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SetAreaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        removeAllViews();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.setting_area_layout, null);
        ButterKnife.bind(this, layout);
        addView(layout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemContainer = findViewById(R.id.item_container);
        areaLayout = findViewById(R.id.area_layout);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void addSetItem(SetItemLayout itemLayout){
        mItems.add(itemLayout);
        itemContainer.addView(itemLayout);
    }

    public SetItemLayout getItem(int index){
        if (mItems.size() > index){
            return mItems.get(index);
        }
        return null;
    }

    public List<SetItemLayout> getItems(){
        return mItems;
    }

    public void addTipToust(String toast){
        TextView toastView = new TextView(getContext());
        toastView.setText(toast);
        toastView.setGravity(Gravity.CENTER);
        toastView.setTextSize(11);
        toastView.setTextColor(Color.parseColor("#999999"));
        itemContainer.addView(toastView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}