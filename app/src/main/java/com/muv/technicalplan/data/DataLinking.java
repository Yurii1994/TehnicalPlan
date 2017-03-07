package com.muv.technicalplan.data;

import com.orm.SugarRecord;

public class DataLinking extends SugarRecord
{
    private String where_user;
    private String from_user;
    private String enterprise;
    private String position;
    private String code;
    private String state;

    public DataLinking() {
    }

    public DataLinking(String where_user, String from_user, String enterprise, String position, String code, String state)
    {
        this.where_user = where_user;
        this.from_user = from_user;
        this.enterprise = enterprise;
        this.position = position;
        this.code = code;
        this.state = state;
    }

    public String getWhere_user() {
        return where_user;
    }

    public void setWhere_user(String where_user) {
        this.where_user = where_user;
    }

    public String getFrom_user() {
        return from_user;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
