package com.muv.technicalplan.base;

import com.muv.technicalplan.data.DataLinking;

import java.util.List;


public class BaseLinking
{
    public void createBase(List<DataLinking> data)
    {
        deleteBase();
        for (int i = 0; i < data.size(); i++)
        {
            String where_user = data.get(i).getWhere_user();
            String from_user = data.get(i).getFrom_user();
            String enterprise = data.get(i).getEnterprise();
            String position = data.get(i).getPosition();
            String code = data.get(i).getCode();
            String state = data.get(i).getState();
            DataLinking base = new DataLinking(where_user, from_user, enterprise, position, code, state);
            base.save();
        }
    }

    public void deleteBase()
    {
        try
        {
            DataLinking.deleteAll(DataLinking.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
