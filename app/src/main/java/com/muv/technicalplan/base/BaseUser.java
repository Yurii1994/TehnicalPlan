package com.muv.technicalplan.base;


import com.muv.technicalplan.data.DataUser;

public class BaseUser
{
    public void createBase(DataUser dataUser)
    {
        deleteBase();
        String name = dataUser.getName();
        String surname = dataUser.getSurname();
        String surname_father = dataUser.getSurname_father();
        String enterprise = dataUser.getEnterprise();
        String position = dataUser.getPosition();
        String login = dataUser.getLogin();
        String email = dataUser.getEmail();
        int type_account = dataUser.getType_account();
        String password = dataUser.getPassword();
        String image = dataUser.getImage();
        String table_position = dataUser.getName_table();
        DataUser base = new DataUser(name, surname, surname_father, enterprise, position,login, email, type_account, password, image, table_position);
        base.save();
    }

    public void deleteBase()
    {
        try
        {
            DataUser.deleteAll(DataUser.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
