package com.muv.technicalplan.main;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.R;

import java.util.HashMap;
import java.util.Map;

public class TabsPagerFragmentAdapterMain extends FragmentStatePagerAdapter
{
    private MainActivity activity;
    private Map<Integer, AbstractTabFragment> tabs;
    private FragmentManager fragmentManager;

    private FragmentMap fragment1;
    private FragmentMap fragment2;
    private FragmentMap fragment3;

    public TabsPagerFragmentAdapterMain(Context context, FragmentManager fm, MainActivity activity) {
        super(fm);
        this.activity = activity;
        this.fragmentManager = fm;
        initTabMap(context);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return tabs.size();
    }


    private void initTabMap(Context context) {
        tabs = new HashMap<>();
        fragment1 = FragmentMap.getInstance(context, activity, context.getText(R.string.normal).toString());
        fragment2 = FragmentMap.getInstance(context, activity, context.getText(R.string.lightweight).toString());
        fragment3 = FragmentMap.getInstance(context, activity, context.getText(R.string.light).toString());
        tabs.put(0, fragment1);
        tabs.put(1, fragment2);
        tabs.put(2, fragment3);
    }
}