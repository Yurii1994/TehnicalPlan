package com.muv.technicalplan.data;

import com.orm.SugarRecord;

public class DataUsers extends SugarRecord
{
    private String name;
    private String surname;
    private String surname_father;
    private String enterprise;
    private String position;
    private String login;
    private String email;
    private String image;

    public DataUsers() {
    }

    public DataUsers(String name, String surname, String surname_father, String enterprise, String position,
                    String login, String email, String image)
    {
        this.name = name;
        this.surname = surname;
        this.surname_father = surname_father;
        this.enterprise = enterprise;
        this.position = position;
        this.login = login;
        this.email = email;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}