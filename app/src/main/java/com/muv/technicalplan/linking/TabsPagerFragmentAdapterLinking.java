package com.muv.technicalplan.linking;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.data.DataSearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabsPagerFragmentAdapterLinking extends FragmentPagerAdapter
{
    private LinkingActivity activity;
    private Map<Integer, AbstractTabFragment> tabs;

    private FragmentSearch fragmentSearch;
    private FragmentLinking fragmentLinking;

    public TabsPagerFragmentAdapterLinking(Context context, FragmentManager fm, LinkingActivity activity) {
        super(fm);
        this.activity = activity;
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

    public void getSearch(String query)
    {
        fragmentSearch.getSearch(query);
    }

    public void createActivityLinking()
    {
        fragmentLinking.createActivity();
    }

    public boolean getStateLinking()
    {
        return fragmentSearch.getStateLinking();
    }

    public void setStateLinking(boolean state)
    {
        fragmentSearch.setStateLinking(state);
    }

    public void setDataSearches(List<DataSearch> list)
    {
        fragmentSearch.setDataSearches(list);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabMap(Context context) {
        tabs = new HashMap<>();
        fragmentLinking = FragmentLinking.getInstance(context, activity);
        fragmentSearch = FragmentSearch.getInstance(context, activity);
        tabs.put(0, fragmentLinking);
        tabs.put(1, fragmentSearch);
    }
}
