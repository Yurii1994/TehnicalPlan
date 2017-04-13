package com.muv.technicalplan.enterprise;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.R;

import java.util.HashMap;
import java.util.Map;

public class TabsPagerFragmentAdapterEnterprise extends FragmentStatePagerAdapter
{


    private Map<Integer, AbstractTabFragment> tabs;
    private FragmentManager fragmentManager;

    private FragmentSettings fragmentSettings;
    private FragmentReport fragmentReport;
    public static TabsPagerFragmentAdapterEnterprise adapter;

    public TabsPagerFragmentAdapterEnterprise(Context context, FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
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
        adapter = this;
        fragmentSettings = FragmentSettings.getInstance(context, context.getText(R.string.settings_enterprise).toString());
        fragmentReport = FragmentReport.getInstance(context, context.getText(R.string.report_enterprise).toString());
        tabs.put(0, fragmentReport);
        tabs.put(1, fragmentSettings);
    }
}
