package com.muv.technicalplan.base;

import com.muv.technicalplan.data.DataUsers;

import java.util.List;

public class BaseUsers
{
    public void createBase(List<DataUsers> data)
    {
        deleteBase();
        for (int i = 0; i < data.size(); i++)
        {
            String name = data.get(i).getName();
            String surname = data.get(i).getSurname();
            String surname_father = data.get(i).getSurname_father();
            String enterprise = data.get(i).getEnterprise();
            String position = data.get(i).getPosition();
            String login = data.get(i).getLogin();
            String email = data.get(i).getEmail();
            String image = data.get(i).getImage();
            DataUsers base = new DataUsers(name, surname, surname_father, enterprise, position,login, email, image);
            base.save();
        }
    }

    public void deleteBase()
    {
        try
        {
            DataUsers.deleteAll(DataUsers.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
