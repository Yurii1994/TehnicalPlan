package com.muv.technicalplan.main;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.muv.technicalplan.AbstractTabFragment;
import com.muv.technicalplan.R;
import com.muv.technicalplan.data.DataMap;
import com.muv.technicalplan.data.DataMaps;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TabsPagerFragmentAdapterMain extends FragmentStatePagerAdapter
{
    private Map<Integer, AbstractTabFragment> tabs;
    private FragmentManager fragmentManager;

    private FragmentMap fragment1;
    private FragmentMap fragment2;
    private FragmentMap fragment3;
    private List<DataMaps> normal;
    private List<DataMaps> lightweight;
    private List<DataMaps> light;

    public TabsPagerFragmentAdapterMain(Context context, FragmentManager fm,
                                        List<DataMaps> normal, List<DataMaps> lightweight, List<DataMaps> light) {
        super(fm);
        this.fragmentManager = fm;
        this.normal = normal;
        this.lightweight = lightweight;
        this.light = light;
        initTabMap(context);
    }

    public void setExpandedPositionSet(HashSet<Integer> mExpandedPositionSet)
    {
        fragment1.setExpandedPositionSet(mExpandedPositionSet);
        fragment2.setExpandedPositionSet(mExpandedPositionSet);
        fragment3.setExpandedPositionSet(mExpandedPositionSet);
    }

    public void setListUpdateItem(List<DataMaps> normal, List<DataMaps> lightweight, List<DataMaps> light, String descriptionText, String positionText)
    {
        fragment1.setListUpdateItem(normal, descriptionText, positionText);
        fragment2.setListUpdateItem(lightweight, descriptionText, positionText);
        fragment3.setListUpdateItem(light, descriptionText, positionText);
    }

    public void setList(List<DataMaps> normal, List<DataMaps> lightweight, List<DataMaps> light)
    {
        this.normal = normal;
        this.lightweight = lightweight;
        this.light = light;
        fragment1.setDataMap(normal);
        fragment2.setDataMap(lightweight);
        fragment3.setDataMap(light);
    }

    public List<DataMaps> updateDataMap(List<DataMaps> dataMaps, String title)
    {
        Map<String, DataMaps> list = new LinkedHashMap<>();
        List<DataMaps> dataMapResult = new ArrayList<>();
        List<DataMaps> dataMapResultSorted = new ArrayList<>();
        ArrayList<String> type = new ArrayList<>();
        final List<DataMaps> dataMapFragment1 = fragment1.getDataMap();
        final List<DataMaps> dataMapFragment2 = fragment2.getDataMap();
        final List<DataMaps> dataMapFragment3 = fragment3.getDataMap();
        if (title.equals(fragment1.getTitle()))
        {
            for (int i = 0; i < dataMapFragment1.size(); i++)
            {
                DataMaps dataMap = dataMapFragment1.get(i);
                list.put(dataMap.getIdMap() + dataMap.getPosition(), dataMap);
            }
        }
        else
        if (title.equals(fragment2.getTitle()))
        {
            for (int i = 0; i < dataMapFragment2.size(); i++)
            {
                DataMaps dataMap = dataMapFragment2.get(i);
                list.put(dataMap.getIdMap() + dataMap.getPosition(), dataMap);
            }
        }
        else
        if (title.equals(fragment3.getTitle()))
        {
            for (int i = 0; i < dataMapFragment3.size(); i++)
            {
                DataMaps dataMap = dataMapFragment3.get(i);
                list.put(dataMap.getIdMap() + dataMap.getPosition(), dataMap);
            }
        }
        for (int i = 0; i < dataMaps.size(); i++)
        {
            DataMaps dataMap = dataMaps.get(i);
            String position = dataMap.getPosition();
            if (!type.contains(position))
            {
                type.add(position);
            }
            String id = dataMap.getIdMap() + dataMap.getPosition();
            if(!list.containsKey(id))
            {
                list.put(id, dataMap);
            }
        }
        for (Map.Entry entry : list.entrySet())
        {
            DataMaps dataMap = (DataMaps)entry.getValue();
            dataMapResult.add(dataMap);
        }
        for (int i = 0; i < type.size(); i++)
        {
            HashMap<Integer, DataMaps> sorted = new LinkedHashMap<>();
            String positionType = type.get(i);
            for (int j = 0; j < dataMapResult.size(); j++)
            {
                String position = dataMapResult.get(j).getPosition();
                int id = dataMapResult.get(j).getIdMap();
                if (position.equals(positionType))
                {
                    sorted.put(id, dataMapResult.get(j));
                }
            }
            int[] positionArray = new int[sorted.size()];
            int count = 0;
            for (Map.Entry entry : sorted.entrySet())
            {
                positionArray[count] = (int)entry.getKey();
                count++;
            }
            Arrays.sort(positionArray);
            for (int j = 0; j < positionArray.length; j++)
            {
                int position = positionArray[j];
                dataMapResultSorted.add(sorted.get(position));
            }
        }
        return dataMapResultSorted;
    }

    public void TableNull()
    {
        fragment1.TableNull();
        fragment2.TableNull();
        fragment3.TableNull();
    }

    public List<List<DataMaps>> getDataMap()
    {
        List<DataMaps> dataMapFragment1 = fragment1.getDataMap();
        List<DataMaps> dataMapFragment2 = fragment2.getDataMap();
        List<DataMaps> dataMapFragment3 = fragment3.getDataMap();
        List<List<DataMaps>> result = new ArrayList<>();
        result.add(dataMapFragment1);
        result.add(dataMapFragment2);
        result.add(dataMapFragment3);
        return result;
    }

    public void getProgressWheelVisible()
    {
        if (fragment1 != null & fragment2 != null & fragment2 != null)
        {
            if (fragment1.getProgressWheel() != null & fragment2.getProgressWheel() != null & fragment3.getProgressWheel() != null)
            {
                fragment1.getProgressWheel().setVisibility(View.VISIBLE);
                fragment2.getProgressWheel().setVisibility(View.VISIBLE);
                fragment3.getProgressWheel().setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        try{
            super.finishUpdate(container);
        } catch (NullPointerException nullPointerException){
            System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
        }
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
        fragment1 = FragmentMap.getInstance(context, context.getText(R.string.normal).toString(), normal);
        fragment2 = FragmentMap.getInstance(context, context.getText(R.string.lightweight).toString(), lightweight);
        fragment3 = FragmentMap.getInstance(context, context.getText(R.string.light).toString(), light);
        tabs.put(0, fragment1);
        tabs.put(1, fragment2);
        tabs.put(2, fragment3);
    }
}