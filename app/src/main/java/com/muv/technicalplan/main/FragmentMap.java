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
import com.muv.technicalplan.DialogFragmentProgress;
import com.muv.technicalplan.Internet;
import com.muv.technicalplan.R;
import com.muv.technicalplan.RecyclerViewMargin;
import com.muv.technicalplan.data.DataLinking;
import com.muv.technicalplan.data.DataMaps;
import com.muv.technicalplan.data.DataUser;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FragmentMap  extends AbstractTabFragment
{
    private static final int LAYOUT = R.layout.fragment_main;
    private RecyclerView recyclerView;
    private ProgressWheel progressWheel;
    private TextView hint;
    private List<DataUser> user;
    private List<DataLinking> linking;
    private List<DataMaps> dataMap = new ArrayList<>();
    private AdapterRecycler adapterRecycler;
    private Internet internet = new Internet();

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("ExpandCard" , adapterRecycler.getExpandedPositionSet());
        outState.putString("Title", getTitle());
    }

    public ProgressWheel getProgressWheel() {
        return progressWheel;
    }

    public static FragmentMap getInstance(Context context, String title, List<DataMaps> dataMap)
    {
        Bundle args = new Bundle();
        FragmentMap fragment = new FragmentMap();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(title);
        fragment.setDataMap(dataMap);
        return fragment;
    }


    public void setDataMap(List<DataMaps> dataMap)
    {
        this.dataMap = new ArrayList<>();
        this.dataMap = dataMap;
    }

    public List<DataMaps> getDataMap()
    {
        try
        {
            return adapterRecycler.getDataMap();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }

    public void setExpandedPositionSet(HashSet<Integer> mExpandedPositionSet)
    {
        if (adapterRecycler != null)
        {
            adapterRecycler.setExpandedPositionSet(mExpandedPositionSet);
        }
    }

    public void setListUpdateItem(List<DataMaps> list, String descriptionText, String positionText)
    {
        adapterRecycler.setListUpdateItem(list, descriptionText, positionText);
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        context = getContext();
        ArrayList<Integer> expandCard;
        HashSet<Integer> expandCardHashSet = new HashSet<>();
        if (savedInstanceState != null)
        {
            expandCard = savedInstanceState.getIntegerArrayList("ExpandCard");
            for (int i = 0; i < expandCard.size();i++)
            {
                expandCardHashSet.add(expandCard.get(i));
            }
            setTitle(savedInstanceState.getString("Title"));
        }

        user = DataUser.listAll(DataUser.class);
        linking = DataLinking.listAll(DataLinking.class);

        progressWheel = (ProgressWheel)view.findViewById(R.id.progress_main);
        hint = (TextView)view.findViewById(R.id.hint_main);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapterRecycler = new AdapterRecycler(dataMap, getContext(), expandCardHashSet, recyclerView, this);
        adapterRecycler.setScrollListener(recyclerView);
        adapterRecycler.setActivity((MainActivity) getActivity());
        RecyclerViewMargin decoration = new RecyclerViewMargin(pxFromDp(10), dataMap.size());
        recyclerView.addItemDecoration(decoration);
        recyclerView.setItemViewCacheSize(dataMap.size());
        recyclerView.setAdapter(adapterRecycler);
        createFragment();

        return view;
    }

    private int pxFromDp(float dp)
    {
        return (int) Math.ceil(dp * getContext().getApplicationContext().getResources().getDisplayMetrics().density);
    }

    public void createFragment()
    {
        if (user.size() > 0)
        {
            if (user.get(0).getEnterprise() != null)
            {
                if (internet.isOnline(getContext()))
                {
                    if (user.get(0).getEnterprise().equals("false"))
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
                    if (dataMap.size() > 0)
                    {
                        hint.setVisibility(View.GONE);
                        progressWheel.setVisibility(View.GONE);
                    }
                }
                else
                {
                    hint.setText(context.getText(R.string.not_network));
                    progressWheel.setVisibility(View.GONE);
                }
            }
        }
    }

    public void TableNull()
    {
        user = DataUser.listAll(DataUser.class);
        if (user.size() > 0)
        {
            if (dataMap != null)
            {
                if (user.get(0).getName_table() != null)
                {
                    if (dataMap.size() == 0 & !user.get(0).getName_table().equals("false"))
                    {
                        hint.setText(context.getText(R.string.table_null));
                        progressWheel.setVisibility(View.GONE);
                    }
                }
            }
            else
            {
                hint.setText(context.getText(R.string.table_null));
                progressWheel.setVisibility(View.GONE);
            }
        }
    }
}
