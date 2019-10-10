package com.maosong.component.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class SimpleViewPagerFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fList;
    private List<String> titles;

    public SimpleViewPagerFragmentAdapter(FragmentManager fm, List<Fragment> fList, List<String> titles) {
        super(fm);
        this.fList = fList;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fList.get(position);
    }

    @Override
    public int getCount() {
        return fList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
