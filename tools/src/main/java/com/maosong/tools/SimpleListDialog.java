/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.maosong.tools;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * edit by youfei on 2016/9/07.
 */
public class SimpleListDialog extends Dialog {

    //public class SimpleListDialog extends HideSystemUiDialog {
    private Context mContext;
    private String mTitle;

    private List<String> mItemList;
    private AdapterView.OnItemClickListener mItemsOnClick;
    private ListView mListViewContent;
    private TextView mTextviewTitle;
    private LinearLayout llTitle;

    public SimpleListDialog(Context context, List<String> itemList, String title,
                            AdapterView.OnItemClickListener itemsOnClick) {
        super(context, R.style.dialog_translucent);
        this.mContext = context;
        if (null != itemList) {
            mItemList = itemList;
        } else {
            mItemList = new ArrayList<>();
        }
        this.mTitle = title;
        this.mItemsOnClick = itemsOnClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_simple_list, null);
        setContentView(layout);
        Window dialogWindow = getWindow();
//        dialogWindow.setWindowAnimations(R.style.popwindow_animation_style);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = DensityUtil.getWidth();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);

        initView();
    }

    protected void initView() {
        mTextviewTitle = (TextView) findViewById(R.id.title);
        llTitle = (LinearLayout) findViewById(R.id.linearlayout_title);
        if (!TextUtils.isEmpty(mTitle)) {
            llTitle.setVisibility(View.VISIBLE);
            mTextviewTitle.setText(mTitle);
        } else {
            llTitle.setVisibility(View.GONE);
        }
        mListViewContent = (ListView) findViewById(R.id.listview_content);
        mListViewContent.setOnItemClickListener(mItemsOnClick);
        mListViewContent.setAdapter(new ListViewAdapter());
    }

    public ListView getListview() {
        return mListViewContent;
    }

    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class ListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (null == convertView) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_simple_list_item, null);
                tv = (TextView) convertView.findViewById(R.id.textview_content);
            } else {
                tv = (TextView) convertView.getTag();
            }

            tv.setText(mItemList.get(position));
            convertView.setTag(tv);
            return convertView;
        }
    }
}
