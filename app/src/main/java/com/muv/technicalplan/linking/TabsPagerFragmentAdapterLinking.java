package com.muv.technicalplan.linking;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.data.DataMaps;
import com.muv.technicalplan.data.DataSearch;
import com.muv.technicalplan.profile.PageTwoProfileFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabsPagerFragmentAdapterLinking extends FragmentPagerAdapter
{
    private Map<Integer, AbstractTabFragment> tabs;

    private FragmentSearch fragmentSearch;
    private FragmentLinking fragmentLinking;

    public TabsPagerFragmentAdapterLinking(Context context, FragmentManager fm) {
        super(fm);
        initTabMap(context);
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
        fragmentLinking = FragmentLinking.getInstance(context);
        fragmentSearch = FragmentSearch.getInstance(context);
        tabs.put(0, fragmentLinking);
        tabs.put(1, fragmentSearch);
    }
}
