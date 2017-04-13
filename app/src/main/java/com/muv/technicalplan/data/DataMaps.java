package com.muv.technicalplan.data;

import com.muv.technicalplan.expandableLayout.ExpandableLayout;
import com.orm.SugarRecord;

public class DataMaps extends SugarRecord
{
    private int idMap;
    private String code;
    private String general;
    private String relative;
    private String description;
    private String normal;
    private String lightweight;
    private String light;
    private String date;
    private String comment_manager;
    private String comment_performer;
    private String position;
    private String name_table;
    private String state_performance;
    private String stitched;
    private String login;
    private String name;
    private String surname;
    private String surname_father;
    private int expand_height;

    public DataMaps() {
    }

    public DataMaps(int id, String code, String general, String relative, String description, String normal,
                    String lightweight, String light, String date, String comment_manager, String comment_performer, String position,
                    String name_table, String state_performance, String stitched, String login, String name,
                    String surname, String surname_father)
    {
        this.idMap = id;
        this.code = code;
        this.general = general;
        this.relative = relative;
        this.description = description;
        this.normal = normal;
        this.lightweight = lightweight;
        this.light = light;
        this.date = date;
        this.comment_manager = comment_manager;
        this.comment_performer = comment_performer;
        this.position = position;
        this.name_table = name_table;
        this.state_performance = state_performance;
        this.stitched = stitched;
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.surname_father = surname_father;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public int getIdMap() {
        return idMap;
    }

    public void setIdMap(int idMap) {
        this.idMap = idMap;
    }

    public int getExpand_height() {
        return expand_height;
    }

    public void setExpand_height(int expand_height) {
        this.expand_height = expand_height;
    }

    public String getStitched() {
        return stitched;
    }

    public void setStitched(String stitched) {
        this.stitched = stitched;
    }

    public String getState_performance() {
        return state_performance;
    }

    public void setState_performance(String state_performance) {
        this.state_performance = state_performance;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName_table() {
        return name_table;
    }

    public void setName_table(String name_table) {
        this.name_table = name_table;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getLightweight() {
        return lightweight;
    }

    public void setLightweight(String lightweight) {
        this.lightweight = lightweight;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment_manager() {
        return comment_manager;
    }

    public void setComment_manager(String comment_manager) {
        this.comment_manager = comment_manager;
    }

    public String getComment_performer() {
        return comment_performer;
    }

    public void setComment_performer(String comment_performer) {
        this.comment_performer = comment_performer;
    }
}
