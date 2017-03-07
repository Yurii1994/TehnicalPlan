package com.muv.technicalplan.enterprise;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.R;

public class FragmentReport extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.fragment_report;

    public static FragmentReport getInstance(Context context, String title)
    {
        Bundle args = new Bundle();
        FragmentReport fragment = new FragmentReport();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(title);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);



        return view;
    }
}