package com.muv.technicalplan.profile;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.data.DataUser;

import java.util.HashMap;
import java.util.Map;

public class TabPagerFragmentAdapterProfile extends FragmentPagerAdapter
{
    private Map<Integer, AbstractTabFragment> tabs;
    private PageOneProfileFragment pageOneFragment;
    private PageTwoProfileFragment pageTwoFragment;

    private ProfileActivity activity;

    public void setTabPagerFragmentAdapter(TabPagerFragmentAdapterProfile tabsPagerAdapter)
    {
        pageTwoFragment.setTabPagerFragment(tabsPagerAdapter);
        pageOneFragment.setTabPagerFragment(tabsPagerAdapter);
    }

    public TabPagerFragmentAdapterProfile(Context context, FragmentManager fm, ProfileActivity activity)
    {
        super(fm);
        initTabMap(context);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabMap(Context context) {
        tabs = new HashMap<>();
        pageOneFragment = PageOneProfileFragment.getInstance(context);
        pageTwoFragment = PageTwoProfileFragment.getInstance(context);
        tabs.put(0, pageOneFragment);
        tabs.put(1, pageTwoFragment);
    }

    public PageOneProfileFragment getPageOneFragment()
    {
        return pageOneFragment;
    }

    public String getPath()
    {
        return pageOneFragment.getPath();
    }

    public void onPressedBack()
    {
        activity.setState_back(true);
        activity.onBackPressed();
    }

    public void getChangeTypeAccount()
    {
        pageTwoFragment.getChangeTypeAccount();
    }

    public String getSurNameUser()
    {
        return pageOneFragment.getSurNameUser();
    }

    public String getNameUser()
    {
        return pageOneFragment.getNameUser();
    }

    public String getSurNameFatherUser()
    {
        return pageOneFragment.getSurNameFatherUser();
    }

    public int getTypeAccountUser()
    {
        return pageOneFragment.getTypeAccountUser();
    }

    public void setOnClickListener(DataUser dataUser, String path)
    {
        pageTwoFragment.setOnClickListener(dataUser, path);
    }
}
