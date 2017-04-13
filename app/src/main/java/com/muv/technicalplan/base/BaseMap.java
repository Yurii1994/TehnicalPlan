package com.muv.technicalplan.base;


import com.muv.technicalplan.data.DataMaps;

import java.util.List;

public class BaseMap
{
    public void createBase(List<DataMaps> dataMaps)
    {
        deleteBase();
        for (int i = 0; i < dataMaps.size(); i++)
        {
            int id = dataMaps.get(i).getIdMap();
            String code = dataMaps.get(i).getCode();
            String general = dataMaps.get(i).getGeneral();
            String relative = dataMaps.get(i).getRelative();
            String description = dataMaps.get(i).getDescription();
            String normal = dataMaps.get(i).getNormal();
            String lightweight = dataMaps.get(i).getLightweight();
            String light = dataMaps.get(i).getLight();
            String date = dataMaps.get(i).getDate();
            String comment_manager = dataMaps.get(i).getComment_manager();
            String comment_performer = dataMaps.get(i).getComment_performer();
            String position = dataMaps.get(i).getPosition();
            String name_table = dataMaps.get(i).getName_table();
            String state_performance = dataMaps.get(i).getState_performance();
            String stitched = dataMaps.get(i).getStitched();
            String login = dataMaps.get(i).getLogin();
            String name = dataMaps.get(i).getName();
            String surname = dataMaps.get(i).getSurname();
            String surname_father = dataMaps.get(i).getSurname_father();
            DataMaps base = new DataMaps(id, code, general, relative, description, normal,
                    lightweight, light, date, comment_manager, comment_performer, position, name_table, state_performance, stitched,
                    login, name, surname, surname_father);
            base.save();
        }
    }

    public void deleteBase()
    {
        try
        {
            DataMaps.deleteAll(DataMaps.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
