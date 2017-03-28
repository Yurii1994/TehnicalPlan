package com.muv.technicalplan.registration;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.data.DataUser;

import java.util.HashMap;
import java.util.Map;


public class TabPagerFragmentAdapterRegistration extends FragmentPagerAdapter
{
    private Map<Integer, AbstractTabFragment> tabs;
    private PageOneRegistrationFragment pageOneFragment;
    private PageTwoRegistrationFragment pageTwoFragment;

    public TabPagerFragmentAdapterRegistration(Context context, FragmentManager fm)
    {
        super(fm);
        initTabMap(context);
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
        pageOneFragment = PageOneRegistrationFragment.getInstance(context);
        pageTwoFragment = PageTwoRegistrationFragment.getInstance(context);
        tabs.put(0, pageOneFragment);
        tabs.put(1, pageTwoFragment);
    }
}
