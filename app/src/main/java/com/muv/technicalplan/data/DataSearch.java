package com.muv.technicalplan.data;


public class DataSearch
{
    private String name;
    private String surname;
    private String surname_father;
    private String login;
    private String enterprise;
    private String image;

    public DataSearch() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname_father() {
        return surname_father;
    }

    public void setSurname_father(String surname_father) {
        this.surname_father = surname_father;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String nameImage) {
        this.image = nameImage;
    }
}
