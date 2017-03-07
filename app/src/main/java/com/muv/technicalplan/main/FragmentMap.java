package com.muv.technicalplan.main;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataLinking;
import com.muv.technicalplan.data.DataUser;
import com.muv.technicalplan.linking.AdapterRecyclerLinking;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

public class FragmentMap  extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.fragment_main;
    private MainActivity activity;
    private RecyclerView recyclerView;
    private AdapterRecyclerLinking adapter;
    private ProgressWheel progressWheel;
    private TextView hint;
    private List<DataUser> user;
    private List<DataLinking> linking;

    public static FragmentMap getInstance(Context context, MainActivity activity, String title)
    {
        Bundle args = new Bundle();
        FragmentMap fragment = new FragmentMap();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(title);
        fragment.setMainActivity(activity);
        return fragment;
    }

    private void setMainActivity(MainActivity activity)
    {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        user = DataUser.listAll(DataUser.class);
        linking = DataLinking.listAll(DataLinking.class);

        progressWheel = (ProgressWheel)view.findViewById(R.id.progress_main);
        hint = (TextView)view.findViewById(R.id.hint_main);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        createFragment();

        return view;
    }

    public void createFragment()
    {
        if (user.size() > 0)
        {
            if (user.get(0).getEnterprise() == null || user.get(0).getEnterprise().equals("null"))
            {
                progressWheel.setVisibility(View.GONE);
                if (user.get(0).getType_account() == 1)
                {
                    hint.setText(context.getText(R.string.not_linked_manager));
                }
                else
                if (user.get(0).getType_account() == 2)
                {
                    if (linking.size() > 0)
                    {
                        if (linking.get(0).getState().equals(""))
                        {
                            hint.setText(context.getText(R.string.not_linked_confirm));
                        }
                        else
                        {
                            hint.setText(context.getText(R.string.not_linked_performer));
                        }
                    }
                    else
                    {
                        hint.setText(context.getText(R.string.not_linked_performer));
                    }
                }
            }
            else
            {
                hint.setVisibility(View.GONE);
            }
        }
    }
}
