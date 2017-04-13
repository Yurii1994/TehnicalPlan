package com.muv.technicalplan.enterprise;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.muv.technicalplan.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentPosition extends Fragment
{
    private static final int LAYOUT = R.layout.fragment_position;
    private Context context;
    private String position;
    private CheckBox checkBox;
    private String name_table;
    private Boolean isChecked;

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setName_table(String name_table) {
        this.name_table = name_table;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("Position", position);
        outState.putString("Name_table", name_table);
        outState.putBoolean("IsChecked", checkBox.isChecked());
    }

    public static FragmentPosition newInstance(String position, String name_table)
    {
        Bundle args = new Bundle();
        FragmentPosition fragment = new FragmentPosition();
        fragment.setArguments(args);
        fragment.setPosition(position);
        fragment.setName_table(name_table);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT, container, false);
        context = getContext();
        if (savedInstanceState != null)
        {
            position = savedInstanceState.getString("Position");
            name_table = savedInstanceState.getString("Name_table");
        }
        checkBox = (CheckBox)view.findViewById(R.id.checkbox_position);
        checkBox.setText(position);
        if (savedInstanceState == null)
        {
            if (position.equals(context.getText(R.string.all)))
            {
                checkBox.setChecked(true);
            }
        }
        else
        {
            isChecked = savedInstanceState.getBoolean("IsChecked");
            checkBox.setChecked(isChecked);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                List<Fragment> fragments = getFragmentManager().getFragments();
                List<FragmentPosition> fragmentPositions = new ArrayList<>();
                for (int i = 0; i < fragments.size(); i++)
                {
                    try
                    {
                        FragmentPosition fragmentPosition = (FragmentPosition) fragments.get(i);
                        fragmentPositions.add(fragmentPosition);
                    }
                    catch (Exception e)
                    {}
                }
                boolean state = false;
                for (int i = 0; i < fragmentPositions.size(); i++)
                {
                    if (fragmentPositions.get(i).checkBox.isChecked())
                    {
                        state = true;
                    }
                }
                if (!state)
                {
                    checkBox.setChecked(true);
                }
                if (isChecked)
                {
                    try
                    {
                        for (int i = 0; i < fragmentPositions.size(); i++)
                        {
                            String all = context.getText(R.string.all).toString();
                            if (buttonView.getText().equals(all))
                            {
                                if (!fragmentPositions.get(i).getCheckBox().getText().equals(all))
                                {
                                    fragmentPositions.get(i).getCheckBox().setChecked(false);
                                }
                            }
                            else
                            {
                                if (fragmentPositions.get(i).getCheckBox().getText().equals(all))
                                {
                                    fragmentPositions.get(i).getCheckBox().setChecked(false);
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {}
                }
            }
        });

        return view;
    }
}
