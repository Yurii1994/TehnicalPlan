package com.muv.technicalplan.data;

import com.orm.SugarRecord;

public class DataUser extends SugarRecord
{
    private int user_id;
    private String name;
    private String surname;
    private String surname_father;
    private String enterprise;
    private String position;
    private String login;
    private String password;
    private String email;
    private int type_account;
    private String image;
    private String name_table;

    public DataUser() {
    }

    public DataUser(String name, String surname, String surname_father, String enterprise, String position,
                    String login, String email, int type_account, String password, String image, String name_table)
    {
        this.name = name;
        this.surname = surname;
        this.surname_father = surname_father;
        this.enterprise = enterprise;
        this.position = position;
        this.login = login;
        this.email = email;
        this.type_account = type_account;
        this.password = password;
        this.image = image;
        this.name_table = name_table;
    }

    public String getName_table() {
        return name_table;
    }

    public void setName_table(String name_table) {
        this.name_table = name_table;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public String getSurname_father() {
        return surname_father;
    }

    public void setSurname_father(String surname_father) {
        this.surname_father = surname_father;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType_account() {
        return type_account;
    }

    public void setType_account(int type_account) {
        this.type_account = type_account;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
